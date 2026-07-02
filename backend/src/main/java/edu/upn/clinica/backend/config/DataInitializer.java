package edu.upn.clinica.backend.config;

import edu.upn.clinica.backend.constants.ClinicConstants;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.scheduling.repository.AvailabilityOverrideRepository;
import edu.upn.clinica.backend.scheduling.repository.AvailabilityTemplateRepository;
import edu.upn.clinica.backend.scheduling.repository.ScheduleAppointmentRepository;
import edu.upn.clinica.backend.scheduling.model.AvailabilityOverride;
import edu.upn.clinica.backend.scheduling.model.AvailabilityTemplate;
import edu.upn.clinica.backend.scheduling.model.ScheduleAppointment;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DoctorRepository doctorRepository;
    private final AvailabilityTemplateRepository templateRepository;
    private final AvailabilityOverrideRepository overrideRepository;
    private final ScheduleAppointmentRepository appointmentRepository;

    public DataInitializer(DoctorRepository doctorRepository,
                           AvailabilityTemplateRepository templateRepository,
                           AvailabilityOverrideRepository overrideRepository,
                           ScheduleAppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.templateRepository = templateRepository;
        this.overrideRepository = overrideRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void run(String... args) {
        var doctors = doctorRepository.findAll();
        if (doctors.isEmpty()) return;

        var doc1 = doctors.get(0);
        var doc2 = doctors.size() > 1 ? doctors.get(1) : doctors.get(0);
        var doc3 = doctors.size() > 2 ? doctors.get(2) : doctors.get(0);

        if (!templateRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doc1.getIdDoctor()).isEmpty()) return;

        saveTemplate(doc1.getIdDoctor(), "LUNES", "08:00", "14:00");
        saveTemplate(doc1.getIdDoctor(), "MARTES", "08:00", "14:00");
        saveTemplate(doc1.getIdDoctor(), "MIERCOLES", "08:00", "14:00");
        saveTemplate(doc1.getIdDoctor(), "JUEVES", "08:00", "14:00");
        saveTemplate(doc1.getIdDoctor(), "VIERNES", "08:00", "14:00");

        saveTemplate(doc2.getIdDoctor(), "LUNES", "08:00", "17:00");
        saveTemplate(doc2.getIdDoctor(), "MIERCOLES", "08:00", "17:00");
        saveTemplate(doc2.getIdDoctor(), "VIERNES", "08:00", "17:00");

        saveTemplate(doc3.getIdDoctor(), "MARTES", "10:00", "16:00");
        saveTemplate(doc3.getIdDoctor(), "JUEVES", "10:00", "16:00");

        LocalDate nextMon = LocalDate.now(ClinicConstants.TIMEZONE);
        while (nextMon.getDayOfWeek() != java.time.DayOfWeek.MONDAY) nextMon = nextMon.plusDays(1);

        AvailabilityOverride block = new AvailabilityOverride();
        block.setDoctorId(doc1.getIdDoctor());
        block.setDate(nextMon);
        block.setStartTime(LocalTime.of(10, 0));
        block.setEndTime(LocalTime.of(12, 0));
        block.setOverrideType("BLOCK");
        block.setReason("Reunión administrativa");
        overrideRepository.save(block);

        LocalDate nextSat = nextMon.plusDays(5);
        AvailabilityOverride add = new AvailabilityOverride();
        add.setDoctorId(doc1.getIdDoctor());
        add.setDate(nextSat);
        add.setStartTime(LocalTime.of(9, 0));
        add.setEndTime(LocalTime.of(13, 0));
        add.setOverrideType("ADD");
        add.setReason("Atención adicional sábado");
        overrideRepository.save(add);
    }

    private void saveTemplate(Integer doctorId, String day, String start, String end) {
        AvailabilityTemplate t = new AvailabilityTemplate();
        t.setDoctorId(doctorId);
        t.setDayOfWeek(day);
        t.setStartTime(LocalTime.parse(start));
        t.setEndTime(LocalTime.parse(end));
        templateRepository.save(t);
    }
}
