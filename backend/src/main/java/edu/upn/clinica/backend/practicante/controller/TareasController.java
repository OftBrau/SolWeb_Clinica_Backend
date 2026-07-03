package edu.upn.clinica.backend.practicante.controller;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.practicante.repository.PracticanteRepository;
import edu.upn.clinica.backend.practicante.model.ActividadPracticante;
import edu.upn.clinica.backend.practicante.model.EvaluacionPracticante;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.notificacion.NotificacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/practicante")
public class TareasController {

    @Autowired private PracticanteRepository repo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private DataSource dataSource;
    @Autowired private SimpMessagingTemplate messaging;

    // --- Tareas ---
    @PostMapping("/tareas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> crearTarea(Authentication auth, @RequestBody Map<String, Object> body) {
        Integer idDoctor = getDoctorId(auth);
        Integer idPracticante = ((Number) body.get("idPracticante")).intValue();
        String titulo = (String) body.get("titulo");
        String descripcion = (String) body.get("descripcion");
        String tipo = (String) body.get("tipo");
        String prioridad = (String) body.get("prioridad");
        String fechaLimite = (String) body.get("fechaLimite");

        Map<String, Object> r = repo.saveTarea(idDoctor, idPracticante, titulo, descripcion, tipo, prioridad, fechaLimite);

        String emailPrac = getEmailPracticante(idPracticante);
        if (emailPrac != null) {
            messaging.convertAndSend("/topic/notificaciones/paciente/" + emailPrac,
                    new NotificacionDTO("TAREA_ASIGNADA", "Nueva tarea: " + titulo, (Integer) r.get("id")));
        }
        return ResponseEntity.ok(ApiResponse.ok("Tarea asignada", r));
    }

    @GetMapping("/tareas/doctor")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> tareasDoctor(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Tareas", repo.findTareasByDoctor(getDoctorId(auth))));
    }

    @GetMapping("/mis-tareas")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> misTareas(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Mis tareas", repo.findTareasByPracticante(getPracticanteId(auth))));
    }

    @PutMapping("/tareas/{id}/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(Authentication auth, @PathVariable Integer id, @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        repo.updateTareaEstado(id, estado);
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado"));
    }

    // --- Agenda (para practicante) ---
    @GetMapping("/agenda")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> agenda(Authentication auth, @RequestParam(required = false) String fecha) {
        Integer idPracticante = getPracticanteId(auth);
        String f = fecha != null ? fecha : LocalDate.now().toString();
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT ct.id_cita, ct.hora, ct.estado, ct.tipo, ct.motivo, " +
                "CONCAT(u.nombre,' ',u.apellido) AS paciente, " +
                "CONCAT(du.nombre,' ',du.apellido) AS doctor, d.especialidad, " +
                "COALESCE(co.nombre,'-') AS consultorio " +
                "FROM citas ct " +
                "LEFT JOIN pacientes p ON ct.id_paciente = p.id_paciente " +
                "LEFT JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "JOIN doctores d ON ct.id_doctor = d.id_doctor " +
                "JOIN usuarios du ON d.id_usuario = du.id_usuario " +
                "LEFT JOIN consultorios co ON ct.id_consultorio = co.id_consultorio " +
                "WHERE ct.fecha = ? AND ct.estado IN ('CONFIRMADA','EN_ATENCION') " +
                "AND d.id_doctor IN (SELECT id_supervisor FROM supervision_practicantes WHERE id_practicante = ?) " +
                "ORDER BY ct.hora";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f); ps.setInt(2, idPracticante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idCita", rs.getInt("id_cita"));
                    m.put("hora", rs.getTime("hora").toString().substring(0,5));
                    m.put("estado", rs.getString("estado"));
                    m.put("tipo", rs.getString("tipo"));
                    m.put("motivo", rs.getString("motivo"));
                    m.put("paciente", rs.getString("paciente"));
                    m.put("doctor", rs.getString("doctor"));
                    m.put("especialidad", rs.getString("especialidad"));
                    m.put("consultorio", rs.getString("consultorio"));
                    result.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return ResponseEntity.ok(ApiResponse.ok("Agenda", result));
    }

    // --- RAFA ---
    @GetMapping("/rafa")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rafa(Authentication auth) {
        Integer idPracticante = getPracticanteId(auth);
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                    "SELECT u.nombre, u.apellido, u.email, pp.universidad, " +
                    "pp.titulo_profesional, pp.conclusion_rafa, pp.competencias_rafa, pp.anio_graduacion " +
                    "FROM practicantes p JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "LEFT JOIN perfil_profesional pp ON pp.id_practicante = p.id_practicante AND pp.activo = TRUE " +
                    "WHERE p.id_practicante = ?")) {
                ps.setInt(1, idPracticante);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.put("nombre", safeStr(rs.getString("nombre")) + " " + safeStr(rs.getString("apellido")));
                        result.put("email", safeStr(rs.getString("email")));
                        result.put("universidad", safeStr(rs.getString("universidad")));
                        result.put("titulo_profesional", safeStr(rs.getString("titulo_profesional")));
                        result.put("anio_graduacion", rs.getObject("anio_graduacion"));
                        result.put("conclusion_rafa", safeStr(rs.getString("conclusion_rafa")));
                        result.put("competencias_rafa", safeStr(rs.getString("competencias_rafa")));
                    }
                }
            }

            List<Map<String, String>> actividades = new ArrayList<>();
            List<ActividadPracticante> acts = repo.findActividadesByPracticante(idPracticante);
            for (ActividadPracticante a : acts) {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("fecha", a.getFecha() != null ? a.getFecha().toString() : "");
                m.put("titulo", a.getTitulo() != null ? a.getTitulo() : "");
                m.put("descripcion", a.getDescripcion() != null ? a.getDescripcion() : "");
                actividades.add(m);
            }
            result.put("actividades", actividades);

            List<EvaluacionPracticante> evals = repo.findEvaluacionesByPracticante(idPracticante);
            List<Map<String, String>> evaluaciones = new ArrayList<>();
            for (EvaluacionPracticante e : evals) {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("fecha", e.getFecha() != null ? e.getFecha().toString() : "");
                m.put("puntuacion", String.valueOf(e.getPuntuacion()));
                m.put("comentario", e.getComentario() != null ? e.getComentario() : "");
                evaluaciones.add(m);
            }
            result.put("evaluaciones", evaluaciones);
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(ApiResponse.ok("RAFA", result));
    }

    // --- Helpers ---
    private Integer getDoctorId(Authentication auth) {
        return doctorRepo.findIdByEmail(auth.getName())
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.FORBIDDEN));
    }

    private Integer getPracticanteId(Authentication auth) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                "SELECT p.id_practicante FROM practicantes p JOIN usuarios u ON p.id_usuario = u.id_usuario WHERE u.email = ?")) {
            ps.setString(1, auth.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_practicante");
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        throw new AppException("Solo practicantes pueden acceder", HttpStatus.FORBIDDEN);
    }

    private String getEmailPracticante(Integer idPracticante) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.email FROM practicantes p JOIN usuarios u ON p.id_usuario = u.id_usuario WHERE p.id_practicante = ?")) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("email"); }
        } catch (Exception e) {}
        return null;
    }

    private String getEmailDoctor(Integer idDoctor) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.email FROM doctores d JOIN usuarios u ON d.id_usuario = u.id_usuario WHERE d.id_doctor = ?")) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("email"); }
        } catch (Exception e) {}
        return null;
    }

    @GetMapping("/evaluaciones/{idPracticante}/pdf")
    public ResponseEntity<byte[]> evaluacionesPdf(@PathVariable Integer idPracticante) {
        try {
            // Datos del practicante
            String nombre = ""; String email = "";
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                    "SELECT u.nombre, u.apellido, u.email FROM practicantes p JOIN usuarios u ON p.id_usuario = u.id_usuario WHERE p.id_practicante = ?")) {
                ps.setInt(1, idPracticante);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nombre = rs.getString("nombre") + " " + rs.getString("apellido");
                        email = rs.getString("email");
                    }
                }
            }

            List<EvaluacionPracticante> evals = repo.findEvaluacionesByPracticante(idPracticante);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 50, 50, 50, 60);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Color primary = new Color(24, 95, 165);
            Color gray = new Color(100, 116, 139);
            Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
            Font fSub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
            Font fLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, gray);
            Font fValue = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font fH3 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, primary);

            // Header
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell hc = new PdfPCell();
            hc.setBackgroundColor(primary);
            hc.setPadding(14);
            hc.setBorder(Rectangle.NO_BORDER);
            hc.addElement(new Paragraph("CLINICA UPN", fTitle));
            hc.addElement(new Paragraph("Reporte de Evaluaciones", fSub));
            header.addCell(hc);
            doc.add(header);
            doc.add(new Paragraph(" "));

            // Practicante info
            doc.add(new Paragraph("Practicante: " + nombre, fH3));
            doc.add(new Paragraph("Email: " + email, fValue));
            doc.add(new Paragraph(" "));

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 1, 1, 4});
            addCell(table, "Fecha", primary, fSub);
            addCell(table, "Punt.", primary, fSub);
            addCell(table, "Supervisor", primary, fSub);
            addCell(table, "Comentario", primary, fSub);

            boolean alt = false;
            for (EvaluacionPracticante e : evals) {
                Color bg = alt ? new Color(245, 247, 250) : Color.WHITE;
                addCell(table, e.getFecha() != null ? e.getFecha().toString() : "-", bg, fValue);
                addCell(table, String.valueOf(e.getPuntuacion()), bg, fValue);
                addCell(table, String.valueOf(e.getIdSupervisor()), bg, fValue);
                addCell(table, e.getComentario() != null ? e.getComentario() : "-", bg, fValue);
                alt = !alt;
            }
            doc.add(table);

            // Footer
            doc.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Generado el " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fLabel);
            footer.setAlignment(Element.ALIGN_RIGHT);
            doc.add(footer);

            doc.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "evaluaciones_" + idPracticante + ".pdf");
            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private void addCell(PdfPTable t, String text, Color bg, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setPadding(6);
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        t.addCell(c);
    }

    private String safeStr(String s) { return s != null ? s : "-"; }
}
