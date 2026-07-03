package com.intifix.shared.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.intifix.shared.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Subida de imágenes. Si Cloudinary está configurado ({@code app.cloudinary.cloud-name}),
 * sube a la nube y devuelve la {@code secure_url}; si no, guarda en la carpeta local
 * {@code app.uploads.dir} y sirve la imagen por {@code /uploads/**}. El contrato del
 * endpoint (devuelve {@code {"url": ...}}) es el mismo en ambos casos.
 *
 * <p>Seguridad: la validación de tipo usa <em>magic bytes</em> (cabeceras binarias del
 * archivo), no el Content-Type del cliente, que es manipulable por el atacante.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/uploads")
public class FileUploadController {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif", PDF_CONTENT_TYPE);
    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5 MB

    // Magic bytes de los formatos permitidos (OWASP File Upload Cheat Sheet)
    private static final byte[] MAGIC_JPEG  = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] MAGIC_PNG   = {(byte)0x89, 0x50, 0x4E, 0x47};
    private static final byte[] MAGIC_GIF87 = {0x47, 0x49, 0x46, 0x38, 0x37, 0x61};
    private static final byte[] MAGIC_GIF89 = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61};
    private static final byte[] MAGIC_PDF   = {0x25, 0x50, 0x44, 0x46, 0x2D};   // %PDF-
    // WEBP: bytes 0-3 = "RIFF", bytes 8-11 = "WEBP"
    private static final byte[] MAGIC_WEBP_RIFF = {0x52, 0x49, 0x46, 0x46};
    private static final byte[] MAGIC_WEBP_TYPE = {0x57, 0x45, 0x42, 0x50};

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    /** Vacío cuando Cloudinary no está configurado (cae al almacenamiento local). */
    private final Optional<Cloudinary> cloudinary;

    public FileUploadController(Optional<Cloudinary> cloudinary) {
        this.cloudinary = cloudinary;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, String>>> subirImagen(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("El archivo está vacío."));
        }
        if (file.getSize() > MAX_BYTES) {
            return ResponseEntity.badRequest().body(ApiResponse.error("La imagen no puede superar 5 MB."));
        }

        // Validación de tipo doble: Content-Type declarado + magic bytes reales del archivo.
        // Confiar solo en getContentType() es un OWASP A08 (insecure design): el atacante
        // puede cambiar la cabecera HTTP y subir un ejecutable disfrazado de imagen.
        String contentType = file.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Formato no permitido. Usa JPG, PNG, WEBP, GIF o PDF."));
        }
        byte[] header = leerCabecera(file, 12);
        if (!tieneSignaturaValida(header)) {
            log.warn("Rechazo de subida: Content-Type={} pero magic bytes no coinciden.", contentType);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("El contenido del archivo no corresponde al tipo declarado."));
        }

        String url;
        if (cloudinary.isPresent()) {
            // Cloudinary configurado: TODAS las imágenes van a la nube. Si falla,
            // devolvemos error claro en vez de guardar local silenciosamente
            // (una URL local no sirve fuera de este servidor).
            try {
                url = subirACloudinary(file, cloudinary.get());
            } catch (Exception e) {
                log.error("Falló la subida a Cloudinary: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(ApiResponse.error("No se pudo subir la imagen a Cloudinary. Intenta de nuevo en un momento."));
            }
        } else {
            // Solo cuando Cloudinary NO está configurado (dev local sin credenciales).
            url = guardarLocal(file, contentType);
        }

        log.info("Imagen subida: {} ({} bytes)", url, file.getSize());
        return ResponseEntity.ok(ApiResponse.success("Imagen subida correctamente.", Map.of("url", url)));
    }

    /** Sube los bytes a Cloudinary y devuelve la URL segura (https) del recurso. */
    private String subirACloudinary(MultipartFile file, Cloudinary client) throws IOException {
        // resource_type "image" para TODO (imágenes Y PDFs):
        // Con "auto" Cloudinary clasifica los PDFs como "raw" (no imagen/video) y los
        // guarda en /raw/upload/ — quedan en la sección "Raw files", no en "Images".
        // Con "image" los PDFs quedan en /image/upload/ con format:pdf, visibles en
        // Cloudinary Media Library bajo Images → filtro Format:PDF.
        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = client.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "intifix",
                        "public_id", UUID.randomUUID().toString(),
                        "resource_type", "image"));
        return String.valueOf(resultado.get("secure_url"));
    }

    /** Guarda en disco bajo {@code app.uploads.dir} y devuelve la URL absoluta /uploads/**. */
    private String guardarLocal(MultipartFile file, String contentType) throws IOException {
        String extension = switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            case PDF_CONTENT_TYPE -> ".pdf";
            default -> ".jpg";
        };
        String nombreArchivo = UUID.randomUUID() + extension;

        Path carpeta = Paths.get(uploadsDir).toAbsolutePath().normalize();
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve(nombreArchivo);

        try (var in = file.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        }

        // URL absoluta para que pase validaciones http(s) y se sirva por /uploads/**.
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(nombreArchivo)
                .toUriString();
    }

    // --- Magic bytes helpers (OWASP File Upload Cheat Sheet) ----------------

    /** Lee los primeros {@code n} bytes del archivo sin consumir el stream completo. */
    private static byte[] leerCabecera(MultipartFile file, int n) throws IOException {
        byte[] buf = new byte[n];
        try (InputStream is = file.getInputStream()) {
            int leidos = is.read(buf, 0, n);
            if (leidos < n) {
                byte[] recortado = new byte[leidos];
                System.arraycopy(buf, 0, recortado, 0, leidos);
                return recortado;
            }
        }
        return buf;
    }

    /** Devuelve true si {@code header} coincide con alguna de las firmas permitidas. */
    private static boolean tieneSignaturaValida(byte[] header) {
        return startsWith(header, MAGIC_JPEG)
            || startsWith(header, MAGIC_PNG)
            || startsWith(header, MAGIC_GIF87)
            || startsWith(header, MAGIC_GIF89)
            || startsWith(header, MAGIC_PDF)
            || isWebp(header);
    }

    private static boolean isWebp(byte[] header) {
        // WEBP: bytes 0-3 = "RIFF"  y  bytes 8-11 = "WEBP"
        return header.length >= 12
            && startsWith(header, MAGIC_WEBP_RIFF)
            && header[8] == MAGIC_WEBP_TYPE[0]
            && header[9] == MAGIC_WEBP_TYPE[1]
            && header[10] == MAGIC_WEBP_TYPE[2]
            && header[11] == MAGIC_WEBP_TYPE[3];
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }
}
