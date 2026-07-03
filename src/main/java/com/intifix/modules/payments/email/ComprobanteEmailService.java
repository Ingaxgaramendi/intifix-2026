package com.intifix.modules.payments.email;

import com.intifix.modules.payments.entity.TipoComprobante;
import com.intifix.modules.payments.event.FacturaEmitidaEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * Envía al cliente, por correo, el comprobante (boleta/factura) de su pago.
 * Usa JdbcTemplate directo para no depender del SecurityContext (se ejecuta en
 * un hilo @Async sin usuario autenticado). Si MAIL_USERNAME está vacío, el
 * envío se omite silenciosamente sin romper el flujo de pago.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComprobanteEmailService {

    private final JavaMailSender mailSender;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.mail.from:no-reply@intifix.com}")
    private String from;

    @Value("${app.mail.from-name:INTIFIX}")
    private String fromName;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    private static final DateTimeFormatter FECHA_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Lima"));

    private static final String SQL_CLIENTE = """
            SELECT u.nombre, u.email, u.numero_documento,
                   p.monto_total, p.comision_plataforma, p.monto_neto_tecnico,
                   p.impuesto_total, p.transaction_id
            FROM pagos p
            JOIN servicios s ON s.id_servicio = p.id_servicio
            JOIN usuarios u  ON u.id_usuario  = s.id_cliente
            WHERE p.id_pago = ?
            """;

    public void enviarComprobante(FacturaEmitidaEvent event) {
        if (mailUsername == null || mailUsername.isBlank()) {
            log.info("Correo no configurado (MAIL_USERNAME vacío); se omite envío de comprobante {}",
                    event.getCodigoComprobante());
            return;
        }

        ClienteInfo info;
        try {
            info = jdbcTemplate.queryForObject(SQL_CLIENTE,
                    (rs, n) -> new ClienteInfo(
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("numero_documento"),
                            rs.getBigDecimal("monto_total"),
                            rs.getBigDecimal("comision_plataforma"),
                            rs.getBigDecimal("monto_neto_tecnico"),
                            rs.getBigDecimal("impuesto_total"),
                            rs.getString("transaction_id")
                    ),
                    event.getIdPago());
        } catch (Exception e) {
            log.warn("No se pudo obtener datos del cliente para el comprobante {} (pago {}): {}",
                    event.getCodigoComprobante(), event.getIdPago(), e.getMessage());
            return;
        }

        if (info == null || info.email() == null || info.email().isBlank()) {
            log.warn("Email del cliente vacío para el pago {}", event.getIdPago());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(info.email());
            helper.setSubject(asunto(event.getTipo(), event.getCodigoComprobante()));
            helper.setText(construirHtml(event, info), true);
            mailSender.send(message);
            log.info("Comprobante {} enviado por correo a {}", event.getCodigoComprobante(), info.email());
        } catch (Exception e) {
            log.error("Error al enviar el comprobante {} por correo a {}",
                    event.getCodigoComprobante(), info.email(), e);
        }
    }

    private String asunto(TipoComprobante tipo, String codigo) {
        String etiqueta = tipo == TipoComprobante.FACTURA ? "Factura" : "Boleta";
        return "Tu " + etiqueta + " electrónica de INTIFIX · " + codigo;
    }

    private String construirHtml(FacturaEmitidaEvent event, ClienteInfo c) {
        BigDecimal neto       = nz(c.montoNetoTecnico());
        BigDecimal comNeta    = nz(c.comisionPlataforma());
        BigDecimal igv        = nz(c.impuestoTotal());
        BigDecimal comBruta   = comNeta.add(igv);
        BigDecimal total      = nz(c.montoTotal());

        String tipoLabel = event.getTipo() == TipoComprobante.FACTURA
                ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA ELECTRÓNICA";
        ZonedDateTime fecha = event.getFechaEmision() != null
                ? event.getFechaEmision() : ZonedDateTime.now(ZoneId.of("America/Lima"));
        String nombre  = c.nombre() != null ? c.nombre() : c.email();
        String doc     = c.numeroDocumento() != null ? c.numeroDocumento() : "—";

        return """
            <div style="font-family:Segoe UI,Arial,sans-serif;background:#f5f5f4;padding:24px;">
              <div style="max-width:600px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;border:1px solid #e7e5e4;">
                <div style="background:#7a1f3d;padding:24px 28px;color:#fff;">
                  <div style="font-size:24px;font-weight:800;letter-spacing:-.5px;">INTIFIX</div>
                  <div style="font-size:13px;opacity:.85;margin-top:4px;">%s</div>
                </div>
                <div style="padding:28px;">
                  <p style="font-size:15px;color:#1c1917;margin:0 0 6px;">Hola <b>%s</b>,</p>
                  <p style="font-size:14px;color:#57534e;margin:0 0 20px;">
                    ¡Gracias por tu pago! Aquí tienes tu comprobante electrónico.
                  </p>

                  <table style="width:100%%;border-collapse:collapse;background:#faf9f8;border-radius:12px;">
                    <tr>
                      <td style="padding:14px 16px;font-size:13px;color:#57534e;">Comprobante</td>
                      <td style="padding:14px 16px;font-size:14px;font-weight:700;color:#7a1f3d;text-align:right;">%s · %s</td>
                    </tr>
                    <tr>
                      <td style="padding:0 16px 14px;font-size:13px;color:#57534e;">Emisor</td>
                      <td style="padding:0 16px 14px;font-size:13px;text-align:right;">INTIFIX S.A.C. · RUC 20612345678</td>
                    </tr>
                    <tr>
                      <td style="padding:0 16px 14px;font-size:13px;color:#57534e;">Documento del cliente</td>
                      <td style="padding:0 16px 14px;font-size:13px;text-align:right;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:0 16px 14px;font-size:13px;color:#57534e;">Fecha de emisión</td>
                      <td style="padding:0 16px 14px;font-size:13px;text-align:right;">%s</td>
                    </tr>
                    %s
                  </table>

                  <table style="width:100%%;border-collapse:collapse;margin-top:20px;">
                    <tr>
                      <td style="padding:10px 4px;font-size:14px;color:#57534e;border-bottom:1px solid #f0eeec;">Monto del servicio (neto al técnico)</td>
                      <td style="padding:10px 4px;font-size:14px;text-align:right;border-bottom:1px solid #f0eeec;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 4px;font-size:14px;color:#57534e;border-bottom:1px solid #f0eeec;">Comisión de intermediación INTIFIX (1%%)</td>
                      <td style="padding:10px 4px;font-size:14px;text-align:right;border-bottom:1px solid #f0eeec;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 4px;font-size:12px;color:#78716c;border-bottom:1px solid #f0eeec;padding-left:16px;">— IGV (18%%) sobre la comisión</td>
                      <td style="padding:10px 4px;font-size:12px;color:#78716c;text-align:right;border-bottom:1px solid #f0eeec;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:16px 4px 4px;font-size:16px;font-weight:800;color:#1c1917;">TOTAL PAGADO</td>
                      <td style="padding:16px 4px 4px;font-size:16px;font-weight:800;text-align:right;color:#1c1917;">%s</td>
                    </tr>
                  </table>

                  <p style="font-size:11px;color:#a8a29e;margin-top:22px;line-height:1.5;">
                    INTIFIX actúa como plataforma de intermediación. El IGV grava únicamente el
                    servicio de intermediación (comisión), conforme al modelo de marketplace.
                  </p>
                </div>
                <div style="background:#faf9f8;padding:16px;text-align:center;font-size:11px;color:#a8a29e;border-top:1px solid #f0eeec;">
                  Correo automático de INTIFIX · No respondas a este mensaje.
                </div>
              </div>
            </div>
            """.formatted(
                tipoLabel,
                escape(nombre),
                event.getTipo(), event.getCodigoComprobante(),
                escape(doc),
                FECHA_FMT.format(fecha),
                c.transactionId() != null
                        ? "<tr><td style=\"padding:0 16px 14px;font-size:13px;color:#57534e;\">N° transacción</td>"
                          + "<td style=\"padding:0 16px 14px;font-size:13px;text-align:right;\">"
                          + escape(c.transactionId()) + "</td></tr>"
                        : "",
                soles(neto),
                soles(comBruta),
                soles(igv),
                soles(total)
        );
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static String soles(BigDecimal v) {
        return String.format(Locale.US, "S/ %,.2f", nz(v));
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private record ClienteInfo(
            String nombre,
            String email,
            String numeroDocumento,
            BigDecimal montoTotal,
            BigDecimal comisionPlataforma,
            BigDecimal montoNetoTecnico,
            BigDecimal impuestoTotal,
            String transactionId
    ) {}
}
