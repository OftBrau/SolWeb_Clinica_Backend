package edu.upn.clinica.backend.cita.service;

import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecordatorioService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void enviarRecordatoriosDiarios() {
        LocalDate manana = LocalDate.now().plusDays(1);
        List<Cita> citasManana = citaRepository.findAllByFecha(manana);

        for (Cita cita : citasManana) {
            if (!"CONFIRMADA".equals(cita.getEstado()) && !"PENDIENTE".equals(cita.getEstado())) {
                continue;
            }
            try {
                String emailPaciente = pacienteRepository.findById(cita.getIdPaciente())
                        .map(p -> p.getEmail())
                        .orElse(null);
                if (emailPaciente == null) continue;

                String nombrePaciente = pacienteRepository.findById(cita.getIdPaciente())
                        .map(p -> p.getNombre() + " " + p.getApellido())
                        .orElse("Paciente");

                String nombreDoctor = doctorRepository.findById(cita.getIdDoctor())
                        .map(d -> d.getNombre())
                        .orElse("Doctor");

                String especialidad = doctorRepository.findById(cita.getIdDoctor())
                        .map(d -> d.getEspecialidad())
                        .orElse("");

                emailService.enviarRecordatorioCita(
                        emailPaciente, nombrePaciente, nombreDoctor,
                        especialidad, cita.getFecha().toString(), cita.getHora().toString());

            } catch (Exception e) {
                System.err.println("Error enviando recordatorio para cita " +
                        cita.getIdCita() + ": " + e.getMessage());
            }
        }
    }
}
