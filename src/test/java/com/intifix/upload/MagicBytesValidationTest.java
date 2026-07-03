package com.intifix.upload;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica la lógica de magic-bytes de FileUploadController via reflexión,
 * sin levantar el contexto Spring ni dependencias externas.
 */
class MagicBytesValidationTest {

    // Invoca el método privado static tieneSignaturaValida(byte[]) por reflexión
    private boolean tieneSignaturaValida(byte[] header) throws Exception {
        Class<?> clazz = Class.forName("com.intifix.shared.upload.FileUploadController");
        Method m = clazz.getDeclaredMethod("tieneSignaturaValida", byte[].class);
        m.setAccessible(true);
        return (boolean) m.invoke(null, (Object) header);
    }

    static Stream<byte[]> firmasValidas() {
        return Stream.of(
            new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},  // JPEG
            new byte[]{(byte)0x89, 0x50, 0x4E, 0x47, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},              // PNG
            new byte[]{0x47, 0x49, 0x46, 0x38, 0x37, 0x61, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},                    // GIF87a
            new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},                    // GIF89a
            new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},                    // PDF (%PDF-)
            new byte[]{0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50}                     // WEBP
        );
    }

    @ParameterizedTest
    @MethodSource("firmasValidas")
    void aceptaFirmasValidas(byte[] header) throws Exception {
        assertThat(tieneSignaturaValida(header)).isTrue();
    }

    @Test
    void rechazaEjecutableDisguazadoDeImagen() throws Exception {
        // EXE / PE header: MZ
        byte[] exe = new byte[]{0x4D, 0x5A, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        assertThat(tieneSignaturaValida(exe)).isFalse();
    }

    @Test
    void rechazaArchivoZip() throws Exception {
        // ZIP / DOCX / XLSX: PK\x03\x04
        byte[] zip = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        assertThat(tieneSignaturaValida(zip)).isFalse();
    }

    @Test
    void rechazaArchivoVacio() throws Exception {
        assertThat(tieneSignaturaValida(new byte[0])).isFalse();
    }

    @Test
    void rechazaWebpSinMarcaWebp() throws Exception {
        // Tiene "RIFF" pero los bytes 8-11 no son "WEBP"
        byte[] riffNoWebp = new byte[]{0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x41, 0x56, 0x49, 0x20};
        assertThat(tieneSignaturaValida(riffNoWebp)).isFalse();
    }
}
