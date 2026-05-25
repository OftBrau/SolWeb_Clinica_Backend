package edu.upn.clinica.backend.hce.service;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.repository.HceRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HceService {

    @Autowired private HceRepository      hceRepository;
    @Autowired private PacienteRepository pacienteRepository;

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

    public String generarReporteTexto(Integer idConsulta) {
        HistorialItem h = obtenerPorIdConsulta(idConsulta);
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIA CLÍNICA ELECTRÓNICA ===\n\n");
        sb.append("Paciente: ").append(h.getNombrePaciente()).append("\n");
        sb.append("Código: ").append(h.getCodigoEstudiante()).append("\n");
        sb.append("Fecha: ").append(h.getFecha()).append("\n");
        sb.append("Médico: ").append(h.getNombreDoctor()).append("\n");
        sb.append("Especialidad: ").append(h.getEspecialidad()).append("\n\n");
        sb.append("--- Diagnóstico ---\n");
        sb.append("CIE-10: ").append(h.getDiagnosticoCie10()).append("\n");
        sb.append("Descripción: ").append(h.getDescripcionDiag()).append("\n\n");
        sb.append("--- Tratamiento ---\n");
        sb.append(h.getTratamiento()).append("\n\n");
        sb.append("--- Prescripción ---\n");
        sb.append(h.getPrescripcion()).append("\n\n");
        sb.append("=== FIN DEL REPORTE ===\n");
        return sb.toString();
    }
}
