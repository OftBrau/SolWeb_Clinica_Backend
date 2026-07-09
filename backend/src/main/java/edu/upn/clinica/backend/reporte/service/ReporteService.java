package edu.upn.clinica.backend.reporte.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.upn.clinica.backend.reporte.dto.ReporteDiarioDTO;
import edu.upn.clinica.backend.reporte.dto.ReporteDiarioDTO.*;
import edu.upn.clinica.backend.reporte.repository.ReporteRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    public ReporteDiarioDTO generarReporteDiario(String fecha) {
        if (fecha == null || fecha.isEmpty()) fecha = LocalDate.now(ZoneId.of("America/Lima")).toString();

        ReporteDiarioDTO dto = new ReporteDiarioDTO();
        dto.setFecha(fecha);

        ResumenGeneralDTO resumen = new ResumenGeneralDTO();
        resumen.setTotalCitas(reporteRepository.contarTotalCitas(fecha));
        resumen.setConfirmadas(reporteRepository.contarCitasPorEstado("CONFIRMADA", fecha));
        resumen.setAtendidas(reporteRepository.contarCitasPorEstado("ATENDIDA", fecha));
        resumen.setCanceladas(reporteRepository.contarCitasPorEstado("CANCELADA", fecha));
        resumen.setNoAsistieron(reporteRepository.contarCitasPorEstado("NO_ASISTIO", fecha));
        resumen.setPacientesAtendidos(reporteRepository.contarPacientesAtendidos(fecha));
        resumen.setDoctoresActivos(reporteRepository.contarDoctoresActivos());
        dto.setResumen(resumen);

        List<Object[]> porEsp = reporteRepository.contarCitasPorEspecialidad(fecha);
        dto.setPorEspecialidad(porEsp.stream().map(row -> {
            CitasPorEspecialidadDTO e = new CitasPorEspecialidadDTO();
            e.setEspecialidad((String) row[0]);
            e.setCantidad((Long) row[1]);
            return e;
        }).toList());

        List<Object[]> porDoc = reporteRepository.contarCitasPorDoctor(fecha);
        dto.setPorDoctor(porDoc.stream().map(row -> {
            CitasPorDoctorDTO d = new CitasPorDoctorDTO();
            d.setNombreDoctor((String) row[0]);
            d.setEspecialidad((String) row[1]);
            d.setCantidad((Long) row[2]);
            return d;
        }).toList());

        return dto;
    }

    public byte[] generarPdf(String fecha) {
        ReporteDiarioDTO dto = generarReporteDiario(fecha);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Color primary = new Color(24, 95, 165);
            Color gray = new Color(100, 116, 139);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, gray);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, primary);

            // Header
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell hc = new PdfPCell();
            hc.setBackgroundColor(primary);
            hc.setPadding(14);
            hc.setBorder(Rectangle.NO_BORDER);
            hc.addElement(new Paragraph("CLINICA UPN", titleFont));
            hc.addElement(new Paragraph("Reporte Operativo Diario - " + dto.getFecha(), subFont));
            header.addCell(hc);
            doc.add(header);
            doc.add(new Paragraph(" "));

            // Resumen
            doc.add(new Paragraph("Resumen General", sectionFont));
            doc.add(new Paragraph(" "));
            PdfPTable rt = new PdfPTable(4);
            rt.setWidthPercentage(100);
            addCell(rt, "Total Citas", primary, headerFont);
            addCell(rt, "Confirmadas", primary, headerFont);
            addCell(rt, "Atendidas", primary, headerFont);
            addCell(rt, "Canceladas", primary, headerFont);
            addCell(rt, String.valueOf(dto.getResumen().getTotalCitas()), Color.WHITE, valueFont);
            addCell(rt, String.valueOf(dto.getResumen().getConfirmadas()), Color.WHITE, valueFont);
            addCell(rt, String.valueOf(dto.getResumen().getAtendidas()), Color.WHITE, valueFont);
            addCell(rt, String.valueOf(dto.getResumen().getCanceladas()), Color.WHITE, valueFont);
            PdfPTable rt2 = new PdfPTable(3);
            rt2.setWidthPercentage(100);
            addCell(rt2, "No Asistieron", primary, headerFont);
            addCell(rt2, "Pacientes Atendidos", primary, headerFont);
            addCell(rt2, "Doctores Activos", primary, headerFont);
            addCell(rt2, String.valueOf(dto.getResumen().getNoAsistieron()), Color.WHITE, valueFont);
            addCell(rt2, String.valueOf(dto.getResumen().getPacientesAtendidos()), Color.WHITE, valueFont);
            addCell(rt2, String.valueOf(dto.getResumen().getDoctoresActivos()), Color.WHITE, valueFont);
            doc.add(rt);
            doc.add(new Paragraph(" "));
            doc.add(rt2);
            doc.add(new Paragraph(" "));

            // Por especialidad
            doc.add(new Paragraph("Citas por Especialidad", sectionFont));
            doc.add(new Paragraph(" "));
            PdfPTable et = new PdfPTable(2);
            et.setWidthPercentage(100);
            addCell(et, "Especialidad", primary, headerFont);
            addCell(et, "Cantidad", primary, headerFont);
            for (CitasPorEspecialidadDTO e : dto.getPorEspecialidad()) {
                addCell(et, e.getEspecialidad(), Color.WHITE, valueFont);
                addCell(et, String.valueOf(e.getCantidad()), Color.WHITE, valueFont);
            }
            doc.add(et);
            doc.add(new Paragraph(" "));

            // Por doctor
            doc.add(new Paragraph("Citas por Doctor", sectionFont));
            doc.add(new Paragraph(" "));
            PdfPTable dt = new PdfPTable(3);
            dt.setWidthPercentage(100);
            addCell(dt, "Doctor", primary, headerFont);
            addCell(dt, "Especialidad", primary, headerFont);
            addCell(dt, "Cantidad", primary, headerFont);
            for (CitasPorDoctorDTO d : dto.getPorDoctor()) {
                addCell(dt, d.getNombreDoctor(), Color.WHITE, valueFont);
                addCell(dt, d.getEspecialidad(), Color.WHITE, valueFont);
                addCell(dt, String.valueOf(d.getCantidad()), Color.WHITE, valueFont);
            }
            doc.add(dt);

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    public byte[] generarExcel(String fecha) {
        ReporteDiarioDTO dto = generarReporteDiario(fecha);
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Reporte Diario");

            XSSFColor blueColor = new XSSFColor(new java.awt.Color(24, 95, 165), null);
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(blueColor);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle boldStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font boldFont = wb.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            int r = 0;

            // Title
            Row titleRow = sheet.createRow(r++);
            Cell tc = titleRow.createCell(0);
            tc.setCellValue("Reporte Operativo Diario - " + dto.getFecha());
            tc.setCellStyle(boldStyle);

            r++; // blank row

            // Resumen
            Row hr1 = sheet.createRow(r++);
            hr1.createCell(0).setCellValue("Resumen General");
            hr1.getCell(0).setCellStyle(boldStyle);

            Row rh = sheet.createRow(r++);
            for (int i = 0; i < 4; i++) {
                Cell c = rh.createCell(i);
                c.setCellStyle(headerStyle);
            }
            rh.getCell(0).setCellValue("Total Citas");
            rh.getCell(1).setCellValue("Confirmadas");
            rh.getCell(2).setCellValue("Atendidas");
            rh.getCell(3).setCellValue("Canceladas");

            Row rv = sheet.createRow(r++);
            rv.createCell(0).setCellValue(dto.getResumen().getTotalCitas());
            rv.createCell(1).setCellValue(dto.getResumen().getConfirmadas());
            rv.createCell(2).setCellValue(dto.getResumen().getAtendidas());
            rv.createCell(3).setCellValue(dto.getResumen().getCanceladas());

            Row rh2 = sheet.createRow(r++);
            for (int i = 0; i < 3; i++) {
                Cell c = rh2.createCell(i);
                c.setCellStyle(headerStyle);
            }
            rh2.getCell(0).setCellValue("No Asistieron");
            rh2.getCell(1).setCellValue("Pacientes Atendidos");
            rh2.getCell(2).setCellValue("Doctores Activos");

            Row rv2 = sheet.createRow(r++);
            rv2.createCell(0).setCellValue(dto.getResumen().getNoAsistieron());
            rv2.createCell(1).setCellValue(dto.getResumen().getPacientesAtendidos());
            rv2.createCell(2).setCellValue(dto.getResumen().getDoctoresActivos());

            r++; // blank row

            // Por especialidad
            Row eh = sheet.createRow(r++);
            eh.createCell(0).setCellValue("Citas por Especialidad");
            eh.getCell(0).setCellStyle(boldStyle);

            Row eh2 = sheet.createRow(r++);
            eh2.createCell(0).setCellValue("Especialidad");
            eh2.createCell(1).setCellValue("Cantidad");
            eh2.getCell(0).setCellStyle(headerStyle);
            eh2.getCell(1).setCellStyle(headerStyle);

            for (CitasPorEspecialidadDTO e : dto.getPorEspecialidad()) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(e.getEspecialidad());
                row.createCell(1).setCellValue(e.getCantidad());
            }

            r++; // blank row

            // Por doctor
            Row dh = sheet.createRow(r++);
            dh.createCell(0).setCellValue("Citas por Doctor");
            dh.getCell(0).setCellStyle(boldStyle);

            Row dh2 = sheet.createRow(r++);
            dh2.createCell(0).setCellValue("Doctor");
            dh2.createCell(1).setCellValue("Especialidad");
            dh2.createCell(2).setCellValue("Cantidad");
            dh2.getCell(0).setCellStyle(headerStyle);
            dh2.getCell(1).setCellStyle(headerStyle);
            dh2.getCell(2).setCellStyle(headerStyle);

            for (CitasPorDoctorDTO d : dto.getPorDoctor()) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(d.getNombreDoctor());
                row.createCell(1).setCellValue(d.getEspecialidad());
                row.createCell(2).setCellValue(d.getCantidad());
            }

            for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);

            wb.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel: " + e.getMessage());
        }
    }

    private void addCell(PdfPTable table, String text, Color bg, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(6);
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        table.addCell(c);
    }
}
