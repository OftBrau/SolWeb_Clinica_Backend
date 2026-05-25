package edu.upn.clinica.backend.reporte.service;

import edu.upn.clinica.backend.reporte.dto.ReporteDiarioDTO;
import edu.upn.clinica.backend.reporte.dto.ReporteDiarioDTO.*;
import edu.upn.clinica.backend.reporte.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    public ReporteDiarioDTO generarReporteDiario(String fecha) {
        if (fecha == null || fecha.isEmpty()) fecha = LocalDate.now().toString();

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
}
