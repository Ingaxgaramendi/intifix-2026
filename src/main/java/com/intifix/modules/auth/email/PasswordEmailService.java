package com.intifix.modules.auth.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@intifix.com}")
    private String from;

    @Value("${app.mail.from-name:INTIFIX}")
    private String fromName;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Async
    public void enviarRecuperacion(String correo, String token) {
        if (mailUsername == null || mailUsername.isBlank()) {
            log.info("Correo no configurado; se omite envío de recuperación para {}", correo);
            return;
        }
        String link = frontendUrl + "/reset-password?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(correo);
            helper.setSubject("Recupera tu contraseña — INTIFIX");
            helper.setText(htmlRecuperacion(link), true);
            mailSender.send(message);
            log.info("Correo de recuperación enviado a {}", correo);
        } catch (Exception e) {
            log.error("Error al enviar correo de recuperación a {}", correo, e);
        }
    }

    @Async
    public void enviarConfirmacionCambio(String correo) {
        if (mailUsername == null || mailUsername.isBlank()) {
            log.info("Correo no configurado; se omite confirmación de cambio para {}", correo);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(correo);
            helper.setSubject("Tu contraseña fue actualizada — INTIFIX");
            helper.setText(htmlConfirmacion(correo), true);
            mailSender.send(message);
            log.info("Confirmación de cambio de contraseña enviada a {}", correo);
        } catch (Exception e) {
            log.error("Error al enviar confirmación de cambio de contraseña a {}", correo, e);
        }
    }

    private String htmlRecuperacion(String link) {
        return """
            <div style="font-family:Segoe UI,Arial,sans-serif;background:#f5f5f4;padding:24px;">
              <div style="max-width:560px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;border:1px solid #e7e5e4;">
                <div style="background:#7a1f3d;padding:24px 28px;color:#fff;">
                  <div style="font-size:24px;font-weight:800;letter-spacing:-.5px;">INTIFIX</div>
                  <div style="font-size:13px;opacity:.85;margin-top:4px;">Recuperación de contraseña</div>
                </div>
                <div style="padding:32px 28px;">
                  <p style="font-size:16px;font-weight:600;color:#1c1917;margin:0 0 12px;">¿Olvidaste tu contraseña?</p>
                  <p style="font-size:14px;color:#57534e;margin:0 0 24px;line-height:1.6;">
                    Recibimos una solicitud para restablecer la contraseña de tu cuenta INTIFIX.
                    Haz clic en el botón de abajo para crear una nueva contraseña. Este enlace
                    expira en <strong>1 hora</strong>.
                  </p>
                  <div style="text-align:center;margin:0 0 28px;">
                    <a href="%s" style="display:inline-block;background:#7a1f3d;color:#fff;text-decoration:none;padding:14px 32px;border-radius:10px;font-size:15px;font-weight:600;letter-spacing:.2px;">
                      Restablecer contraseña
                    </a>
                  </div>
                  <p style="font-size:12px;color:#a8a29e;line-height:1.6;margin:0;">
                    Si no solicitaste restablecer tu contraseña, puedes ignorar este correo.
                    Tu cuenta permanece segura.
                  </p>
                  <p style="font-size:12px;color:#a8a29e;margin:12px 0 0;">
                    Si el botón no funciona, copia y pega este enlace en tu navegador:<br/>
                    <span style="color:#7a1f3d;word-break:break-all;">%s</span>
                  </p>
                </div>
                <div style="background:#faf9f8;padding:16px;text-align:center;font-size:11px;color:#a8a29e;border-top:1px solid #f0eeec;">
                  Correo automático de INTIFIX · No respondas a este mensaje.
                </div>
              </div>
            </div>
            """.formatted(link, link);
    }

    private String htmlConfirmacion(String correo) {
        return """
            <div style="font-family:Segoe UI,Arial,sans-serif;background:#f5f5f4;padding:24px;">
              <div style="max-width:560px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;border:1px solid #e7e5e4;">
                <div style="background:#7a1f3d;padding:24px 28px;color:#fff;">
                  <div style="font-size:24px;font-weight:800;letter-spacing:-.5px;">INTIFIX</div>
                  <div style="font-size:13px;opacity:.85;margin-top:4px;">Seguridad de la cuenta</div>
                </div>
                <div style="padding:32px 28px;">
                  <div style="display:flex;align-items:center;gap:12px;background:#f0fdf4;border:1px solid #bbf7d0;border-radius:12px;padding:16px;margin-bottom:24px;">
                    <span style="font-size:24px;">✓</span>
                    <p style="font-size:15px;font-weight:600;color:#166534;margin:0;">Contraseña actualizada correctamente</p>
                  </div>
                  <p style="font-size:14px;color:#57534e;line-height:1.6;margin:0 0 16px;">
                    La contraseña de la cuenta <strong>%s</strong> fue cambiada exitosamente.
                  </p>
                  <p style="font-size:14px;color:#57534e;line-height:1.6;margin:0;">
                    Si no realizaste este cambio, contacta inmediatamente a soporte de INTIFIX,
                    ya que alguien más podría haber accedido a tu cuenta.
                  </p>
                </div>
                <div style="background:#faf9f8;padding:16px;text-align:center;font-size:11px;color:#a8a29e;border-top:1px solid #f0eeec;">
                  Correo automático de INTIFIX · No respondas a este mensaje.
                </div>
              </div>
            </div>
            """.formatted(escape(correo));
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
