package edu.upn.clinica.backend.doctor.service;

import edu.upn.clinica.backend.doctor.dto.ActualizarDoctorRequest;
import edu.upn.clinica.backend.doctor.dto.CrearDoctorRequest;
import edu.upn.clinica.backend.doctor.dto.DoctorDTO;
import edu.upn.clinica.backend.doctor.dto.DisponibilidadDTO;
import edu.upn.clinica.backend.doctor.repository.DisponibilidadRepository;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.doctor.repository.DisponibilidadRepository.DisponibilidadRow;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final SecureRandom RANDOM = new SecureRandom();

    public List<DisponibilidadDTO> listarDisponibilidad(Integer idDoctor) {
        return disponibilidadRepository.findByDoctor(idDoctor).stream()
                .map(this::toDisponibilidadDTO)
                .toList();
    }

    public DisponibilidadDTO crearDisponibilidad(DisponibilidadDTO dto) {
        if (disponibilidadRepository.existeConflicto(
                dto.getIdDoctor(), dto.getDiaSemana(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new AppException("Ya existe disponibilidad en ese horario", HttpStatus.CONFLICT);
        }
        DisponibilidadRow row = new DisponibilidadRow();
        row.setIdDoctor(dto.getIdDoctor());
        row.setDiaSemana(dto.getDiaSemana());
        row.setHoraInicio(dto.getHoraInicio());
        row.setHoraFin(dto.getHoraFin());
        row = disponibilidadRepository.save(row);
        return toDisponibilidadDTO(row);
    }

    public DisponibilidadDTO actualizarDisponibilidad(Integer id, DisponibilidadDTO dto) {
        DisponibilidadRow row = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new AppException("Disponibilidad no encontrada", HttpStatus.NOT_FOUND));
        row.setDiaSemana(dto.getDiaSemana());
        row.setHoraInicio(dto.getHoraInicio());
        row.setHoraFin(dto.getHoraFin());
        disponibilidadRepository.update(row);
        return toDisponibilidadDTO(row);
    }

    public void eliminarDisponibilidad(Integer id) {
        disponibilidadRepository.findById(id)
                .orElseThrow(() -> new AppException("Disponibilidad no encontrada", HttpStatus.NOT_FOUND));
        disponibilidadRepository.delete(id);
    }

    // ============ Admin ============

    public List<DoctorDTO> listarTodos() {
        return doctorRepository.findAllAdmin();
    }

    public void actualizarEspecialidad(Integer idDoctor, String especialidad) {
        doctorRepository.findById(idDoctor)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
        doctorRepository.updateEspecialidad(idDoctor, especialidad);
    }

    public void registrar(CrearDoctorRequest request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new AppException("El email ya está registrado", HttpStatus.CONFLICT);
        }

        String passwordTemp = generarPasswordTemporal();
        String hash = passwordEncoder.encode(passwordTemp);

        doctorRepository.save(request, hash);
    }

    public void actualizar(Integer idDoctor, ActualizarDoctorRequest request) {
        doctorRepository.findById(idDoctor)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
        doctorRepository.update(idDoctor, request);
    }

    public void eliminar(Integer idDoctor) {
        doctorRepository.findById(idDoctor)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
        doctorRepository.deleteById(idDoctor);
    }

    public void actualizarFoto(Integer idDoctor, String fotoUrl) {
        doctorRepository.findById(idDoctor)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
        doctorRepository.updateFoto(idDoctor, fotoUrl);
    }

    private String generarPasswordTemporal() {
        byte[] bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private DisponibilidadDTO toDisponibilidadDTO(DisponibilidadRow row) {
        
        return new DisponibilidadDTO(
                row.getIdDisponibilidad(), row.getIdDoctor(),
                row.getDiaSemana(), row.getHoraInicio(), row.getHoraFin());
    }
}
