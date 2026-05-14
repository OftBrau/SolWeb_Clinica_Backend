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
}