package edu.upn.clinica.backend.asignacion.service;

import edu.upn.clinica.backend.consultorio.repository.ConsultorioRepository;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsignacionAutomaticaService {

    @Autowired
    private ConsultorioRepository consultorioRepository;

    @Autowired
    private CitaRepository citaRepository;

    public Map<String, Integer> encontrarDoctorDisponible(LocalDate fecha, LocalTime hora) {
        List<Integer> doctores = consultorioRepository.findDoctoresMedicinaGeneralActivos();
        String diaSemana = traducirDiaSemana(fecha.getDayOfWeek());

        for (Integer idDoctor : doctores) {
            if (!citaRepository.existeConflicto(idDoctor, fecha, hora)) {
                Integer consultorio = consultorioRepository.findConsultorioForDoctor(
                        idDoctor, diaSemana, hora.toString().substring(0, 5));
                if (consultorio != null) {
                    Map<String, Integer> result = new LinkedHashMap<>();
                    result.put("idDoctor", idDoctor);
                    result.put("idConsultorio", consultorio);
                    return result;
                }
            }
        }

        throw new AppException("No hay medicos disponibles en ese horario. Intente con otra hora.",
                HttpStatus.NOT_FOUND);
    }

    private String traducirDiaSemana(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }
}
