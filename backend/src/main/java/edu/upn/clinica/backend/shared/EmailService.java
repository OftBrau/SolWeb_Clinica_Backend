package edu.upn.clinica.backend.shared;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    // ─── Enviar credenciales a paciente nuevo ───────────────
    @Async
    public void enviarCredenciales(String email, String nombre, String password) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(email);
            helper.setSubject("Bienvenido a la Clínica UPN - Tus credenciales");

            String cuerpo = """
                    <html><body>
                    <h2>Bienvenido/a, %s</h2>
                    <p>Tu cuenta ha sido creada exitosamente.</p>
                    <p><strong>Correo:</strong> %s</p>
                    <p><strong>Contraseña temporal:</strong> %s</p>
                    <p>Te recomendamos cambiar tu contraseña después de iniciar sesión.</p>
                    <br><p>Clínica UPN</p>
                    </body></html>
                    """.formatted(nombre, email, password);

            helper.setText(cuerpo, true);
            mailSender.send(mensaje);

        } catch (Exception e) {
            System.err.println("Error al enviar credenciales a " + email + ": " + e.getMessage());
        }
    }

    // ─── Enviar confirmación de cita ────────────────────────
    @Async
    public void enviarConfirmacionCita(String email, String nombrePaciente,
                                       String nombreDoctor, String especialidad,
                                       String fecha, String hora) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(email);
            helper.setSubject("Confirmación de Cita - Clínica UPN");

            String cuerpo = """
                    <html><body>
                    <h2>Cita Confirmada</h2>
                    <p>Hola, <strong>%s</strong>. Tu cita ha sido agendada exitosamente.</p>
                    <table border="1" cellpadding="8" cellspacing="0">
                        <tr><td><strong>Médico</strong></td><td>%s</td></tr>
                        <tr><td><strong>Especialidad</strong></td><td>%s</td></tr>
                        <tr><td><strong>Fecha</strong></td><td>%s</td></tr>
                        <tr><td><strong>Hora</strong></td><td>%s</td></tr>
                    </table>
                    <br><p>Clínica UPN</p>
                    </body></html>
                    """.formatted(nombrePaciente, nombreDoctor, especialidad, fecha, hora);

            helper.setText(cuerpo, true);
            mailSender.send(mensaje);

        } catch (Exception e) {
            System.err.println("Error al enviar confirmación a " + email + ": " + e.getMessage());
        }
    }

    // ─── Enviar recordatorio de cita (CUS_13) ──────────────
    @Async
    public void enviarRecordatorioCita(String email, String nombrePaciente,
                                       String nombreDoctor, String especialidad,
                                       String fecha, String hora) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(email);
            helper.setSubject("Recordatorio de Cita - Clínica UPN");

            String cuerpo = """
                    <html><body>
                    <h2>Recordatorio de Cita</h2>
                    <p>Hola, <strong>%s</strong>. Te recordamos que tienes una cita mañana.</p>
                    <table border="1" cellpadding="8" cellspacing="0">
                        <tr><td><strong>Médico</strong></td><td>%s</td></tr>
                        <tr><td><strong>Especialidad</strong></td><td>%s</td></tr>
                        <tr><td><strong>Fecha</strong></td><td>%s</td></tr>
                        <tr><td><strong>Hora</strong></td><td>%s</td></tr>
                    </table>
                    <p>Por favor, confirma tu asistencia o reprograma si es necesario.</p>
                    <br><p>Clínica UPN</p>
                    </body></html>
                    """.formatted(nombrePaciente, nombreDoctor, especialidad, fecha, hora);

            helper.setText(cuerpo, true);
            mailSender.send(mensaje);

        } catch (Exception e) {
            System.err.println("Error al enviar recordatorio a " + email + ": " + e.getMessage());
        }
    }
}