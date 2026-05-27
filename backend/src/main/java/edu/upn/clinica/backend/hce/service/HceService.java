package edu.upn.clinica.backend.hce.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
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
import java.util.List;

@Service
public class HceService {

    @Autowired private HceRepository      hceRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private DoctorRepository   doctorRepository;

    public List<HistorialItem> listarTodos() {
        return hceRepository.findAll();
    }

    public List<HistorialItem> listarPorEmail(String email) {
        Integer idPaciente = pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException(
                        "Paciente no encontrado", HttpStatus.NOT_FOUND));
        return hceRepository.findByPaciente(idPaciente);
    }

    public List<HistorialItem> listarPorDoctorEmail(String email) {
        Integer idDoctor = doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Doctor no encontrado", HttpStatus.NOT_FOUND));
        var doctor = doctorRepository.findById(idDoctor)
                .orElseThrow(() -> new AppException(
                        "Doctor no encontrado", HttpStatus.NOT_FOUND));
        return hceRepository.findByDoctorNombre(doctor.getNombre());
    }

    public HistorialItem obtenerPorIdConsulta(Integer idConsulta) {
        return hceRepository.findByIdConsulta(idConsulta)
                .orElseThrow(() -> new AppException(
                        "Documento no encontrado", HttpStatus.NOT_FOUND));
    }

    public byte[] generarReportePDF(Integer idConsulta) {
        HistorialItem h = obtenerPorIdConsulta(idConsulta);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Color primaryColor = new Color(37, 99, 235);
            Color lightBg = new Color(239, 246, 255);
            Color sectionBg = new Color(37, 99, 235);
            Color white = Color.WHITE;
            Color dark = new Color(30, 41, 59);
            Color gray = new Color(100, 116, 139);

            Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, white);
            Font fSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, white);
            Font fLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, gray);
            Font fValue = FontFactory.getFont(FontFactory.HELVETICA, 11, dark);
            Font fSmall = FontFactory.getFont(FontFactory.HELVETICA, 9, gray);

            // ---------- header bar ----------
            PdfPTable headerBar = new PdfPTable(1);
            headerBar.setWidthPercentage(100);
            headerBar.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPCell headerCell = new PdfPCell(new Phrase("HISTORIA CLÍNICA ELECTRÓNICA", fTitle));
            headerCell.setBackgroundColor(primaryColor);
            headerCell.setPadding(14);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerBar.addCell(headerCell);
            doc.add(headerBar);
            doc.add(new Paragraph(" "));

            // ---------- patient info table ----------
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 2.5f});
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            infoTable.getDefaultCell().setPadding(4);

            addInfoRow(infoTable, "Paciente", h.getNombrePaciente(), lightBg, fLabel, fValue);
            addInfoRow(infoTable, "Código", h.getCodigoEstudiante(), null, fLabel, fValue);
            addInfoRow(infoTable, "Fecha", h.getFecha() != null ? h.getFecha().toString() : "—", lightBg, fLabel, fValue);
            addInfoRow(infoTable, "Médico", h.getNombreDoctor(), null, fLabel, fValue);
            addInfoRow(infoTable, "Especialidad", h.getEspecialidad(), lightBg, fLabel, fValue);
            doc.add(infoTable);
            doc.add(new Paragraph(" "));

            // ---------- sections ----------
            addSection(doc, "DIAGNÓSTICO", sectionBg, fSection);
            addField(doc, "CIE-10", h.getDiagnosticoCie10(), fLabel, fValue);
            addField(doc, "Descripción", h.getDescripcionDiag(), fLabel, fValue);
            doc.add(new Paragraph(" "));

            addSection(doc, "TRATAMIENTO", sectionBg, fSection);
            addField(doc, "Indicaciones", h.getTratamiento(), fLabel, fValue);
            doc.add(new Paragraph(" "));

            addSection(doc, "PRESCRIPCIÓN", sectionBg, fSection);
            addField(doc, "Medicación", h.getPrescripcion(), fLabel, fValue);
            doc.add(new Paragraph(" "));

            // ---------- footer ----------
            Paragraph footer = new Paragraph(
                    "Documento generado por el Sistema Clínica UPN", fSmall);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    private void addInfoRow(PdfPTable t, String label, String value,
                            Color bg, Font fLabel, Font fValue) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fLabel));
        c1.setPadding(6);
        c1.setPaddingLeft(10);
        c1.setBorder(Rectangle.NO_BORDER);
        if (bg != null) c1.setBackgroundColor(bg);

        PdfPCell c2 = new PdfPCell(new Phrase(value != null ? value : "—", fValue));
        c2.setPadding(6);
        c2.setBorder(Rectangle.NO_BORDER);
        if (bg != null) c2.setBackgroundColor(bg);

        t.addCell(c1);
        t.addCell(c2);
    }

    private void addSection(Document doc, String title, Color bg, Font font)
            throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        PdfPCell c = new PdfPCell(new Phrase(title, font));
        c.setBackgroundColor(bg);
        c.setPadding(8);
        c.setPaddingLeft(12);
        c.setBorder(Rectangle.NO_BORDER);
        t.addCell(c);
        doc.add(t);
    }

    private void addField(Document doc, String label, String value,
                          Font fLabel, Font fValue) throws DocumentException {
        if (value == null || value.isBlank()) return;
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ": ", fLabel));
        p.add(new Chunk(value, fValue));
        p.setIndentationLeft(10);
        p.setSpacingBefore(4);
        doc.add(p);
    }
}
