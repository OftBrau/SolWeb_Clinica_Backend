package edu.upn.clinica.backend.hce.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.repository.HceRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HceService {

    @Autowired private HceRepository      hceRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private DoctorRepository   doctorRepository;

    private static final Color PRIMARY    = new Color(24, 95, 165);
    private static final Color PRIMARY_LT = new Color(230, 241, 251);
    private static final Color SECTION_BG = new Color(24, 95, 165);
    private static final Color WHITE      = Color.WHITE;
    private static final Color DARK       = new Color(30, 41, 59);
    private static final Color GRAY       = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(203, 213, 225);

    public List<HistorialItem> listarTodos() { return hceRepository.findAll(); }

    public List<HistorialItem> listarPorEmail(String email) {
        Integer idPaciente = pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));
        return hceRepository.findByPaciente(idPaciente);
    }

    public List<HistorialItem> listarPorDoctorEmail(String email) {
        Integer idDoctor = doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
        var doctor = doctorRepository.findById(idDoctor)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
        return hceRepository.findByDoctorNombre(doctor.getNombre());
    }

    public List<HistorialItem> listarPorPracticanteEmail(String email) {
        try {
            java.sql.Connection c = java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db_clinica_upn", "root", "Root123!");
            var ps = c.prepareStatement(
                    "SELECT p.id_practicante FROM practicantes p JOIN usuarios u ON p.id_usuario = u.id_usuario WHERE u.email = ?");
            ps.setString(1, email);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return hceRepository.findByPracticante(rs.getInt("id_practicante"));
            }
        } catch (Exception e) {}
        return List.of();
    }

    public HistorialItem obtenerPorIdConsulta(Integer idConsulta) {
        return hceRepository.findByIdConsulta(idConsulta)
                .orElseThrow(() -> new AppException("Documento no encontrado", HttpStatus.NOT_FOUND));
    }

    public byte[] generarReportePDF(Integer idConsulta) {
        HistorialItem h = obtenerPorIdConsulta(idConsulta);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document doc = new Document(PageSize.A4, 50, 50, 50, 60);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            doc.open();

            Font fH1      = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, WHITE);
            Font fH2      = FontFactory.getFont(FontFactory.HELVETICA, 8, WHITE);
            Font fSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, WHITE);
            Font fLabel   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, GRAY);
            Font fValue   = FontFactory.getFont(FontFactory.HELVETICA, 10, DARK);
            Font fValueB  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, DARK);
            Font fFooter  = FontFactory.getFont(FontFactory.HELVETICA, 8, GRAY);
            Font fFooterB = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, GRAY);

            // --- HEADER ---
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell hc = new PdfPCell();
            hc.setBackgroundColor(PRIMARY);
            hc.setPadding(16);
            hc.setBorder(Rectangle.NO_BORDER);

            Paragraph hTitle = new Paragraph("CLÍNICA UPN", fH1);
            hTitle.setAlignment(Element.ALIGN_CENTER);
            hc.addElement(hTitle);

            Paragraph hSub = new Paragraph("Historia Clínica Electrónica - Registro de Atención Médica", fH2);
            hSub.setAlignment(Element.ALIGN_CENTER);
            hc.addElement(hSub);

            header.addCell(hc);
            doc.add(header);
            doc.add(new Paragraph(" "));

            // --- PATIENT INFO BOX ---
            PdfPTable infoBox = new PdfPTable(2);
            infoBox.setWidthPercentage(100);
            infoBox.setWidths(new float[]{1, 2});

            addInfoCard(infoBox, "Paciente", h.getNombrePaciente(), fLabel, fValueB, PRIMARY_LT);
            addInfoCard(infoBox, "Código", h.getCodigoEstudiante(), fLabel, fValue, null);
            String fecha = h.getFecha() != null
                    ? h.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "No registrada";
            addInfoCard(infoBox, "Fecha de atención", fecha, fLabel, fValue, PRIMARY_LT);
            addInfoCard(infoBox, "Médico tratante", h.getNombreDoctor(), fLabel, fValueB, null);
            addInfoCard(infoBox, "Especialidad", h.getEspecialidad(), fLabel, fValue, PRIMARY_LT);
            doc.add(infoBox);
            doc.add(new Paragraph(" "));

            // --- SECTION: DIAGNÓSTICO ---
            addSectionBox(doc, "DIAGNÓSTICO MÉDICO", SECTION_BG, fSection,
                    h.getDiagnosticoCie10(), h.getDescripcionDiag(), fLabel, fValue);

            // --- SECTION: TRATAMIENTO ---
            addSectionBox(doc, "TRATAMIENTO INDICADO", SECTION_BG, fSection,
                    null, h.getTratamiento(), fLabel, fValue);

            // --- SECTION: PRESCRIPCIÓN / RECETA ---
            addSectionBox(doc, "PRESCRIPCIÓN MÉDICA (RECETA)", SECTION_BG, fSection,
                    null, h.getPrescripcion(), fLabel, fValue);

            // --- SIGNATURE AREA ---
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            PdfPTable signLine = new PdfPTable(1);
            signLine.setWidthPercentage(90);
            signLine.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell sl = new PdfPCell(new Phrase(" "));
            sl.setBorder(Rectangle.BOTTOM);
            sl.setBorderWidth(0.5f);
            sl.setBorderColor(BORDER_COLOR);
            sl.setFixedHeight(50);
            sl.setPaddingBottom(4);
            signLine.addCell(sl);
            doc.add(signLine);

            Paragraph signLabel = new Paragraph("Firma y sello del médico tratante", fFooter);
            signLabel.setAlignment(Element.ALIGN_CENTER);
            doc.add(signLabel);

            // --- FOOTER ---
            doc.add(new Paragraph(" "));
            Paragraph footer = new Paragraph(
                    "Documento generado por Clínica UPN — " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    fFooterB);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    private void addInfoCard(PdfPTable t, String label, String value,
                             Font fl, Font fv, Color bg) {
        String display = (value == null || value.isBlank()) ? "—" : value;
        PdfPCell c1 = new PdfPCell(new Phrase(label, fl));
        c1.setPadding(7); c1.setPaddingLeft(12); c1.setBorder(Rectangle.NO_BORDER);
        if (bg != null) c1.setBackgroundColor(bg);

        PdfPCell c2 = new PdfPCell(new Phrase(display, fv));
        c2.setPadding(7); c2.setBorder(Rectangle.NO_BORDER);
        if (bg != null) c2.setBackgroundColor(bg);

        t.addCell(c1); t.addCell(c2);
    }

    private void addSectionBox(Document doc, String title, Color bg, Font fSection,
                                String cie10, String content, Font fLabel, Font fValue)
            throws DocumentException {

        PdfPTable outer = new PdfPTable(1);
        outer.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell(new Phrase(title, fSection));
        titleCell.setBackgroundColor(bg);
        titleCell.setPadding(10);
        titleCell.setPaddingLeft(14);
        titleCell.setBorder(Rectangle.NO_BORDER);
        outer.addCell(titleCell);

        PdfPCell bodyCell = new PdfPCell();
        bodyCell.setPadding(14);
        bodyCell.setPaddingTop(12);
        bodyCell.setBorderColor(BORDER_COLOR);
        bodyCell.setBorder(Rectangle.BOX);
        bodyCell.setBorderWidthTop(0);

        if (cie10 != null) {
            Paragraph cie = new Paragraph();
            cie.add(new Chunk("CIE-10: ", fLabel));
            cie.add(new Chunk(cie10.isBlank() ? "No registrado" : cie10, fValue));
            bodyCell.addElement(cie);
            bodyCell.addElement(new Paragraph(" "));
        }

        String display = (content == null || content.isBlank()) ? "No registrado" : content;
        for (String line : display.split("\n")) {
            if (!line.trim().isEmpty()) {
                bodyCell.addElement(new Paragraph(line.trim(), fValue));
            }
        }

        outer.addCell(bodyCell);
        doc.add(outer);
        doc.add(new Paragraph(" "));
    }
}
