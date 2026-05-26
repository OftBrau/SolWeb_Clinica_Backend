package edu.upn.clinica.backend.hce.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.repository.HceRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class HceService {

    @Autowired private HceRepository      hceRepository;
    @Autowired private PacienteRepository pacienteRepository;

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

    public HistorialItem obtenerPorIdConsulta(Integer idConsulta) {
        return hceRepository.findByIdConsulta(idConsulta)
                .orElseThrow(() -> new AppException(
                        "Documento no encontrado", HttpStatus.NOT_FOUND));
    }

    public byte[] generarReportePDF(Integer idConsulta) {
        HistorialItem h = obtenerPorIdConsulta(idConsulta);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("HISTORIA CLÍNICA ELECTRÓNICA", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Paciente: " + h.getNombrePaciente(), headerFont));
            document.add(new Paragraph("Código: " + h.getCodigoEstudiante(), normalFont));
            document.add(new Paragraph("Fecha: " + h.getFecha(), normalFont));
            document.add(new Paragraph("Médico: " + h.getNombreDoctor(), normalFont));
            document.add(new Paragraph("Especialidad: " + h.getEspecialidad(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("DIAGNÓSTICO", headerFont));
            document.add(new Paragraph("CIE-10: " + h.getDiagnosticoCie10(), normalFont));
            document.add(new Paragraph("Descripción: " + h.getDescripcionDiag(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("TRATAMIENTO", headerFont));
            document.add(new Paragraph(h.getTratamiento(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("PRESCRIPCIÓN", headerFont));
            document.add(new Paragraph(h.getPrescripcion(), normalFont));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
        return baos.toByteArray();
    }
}
