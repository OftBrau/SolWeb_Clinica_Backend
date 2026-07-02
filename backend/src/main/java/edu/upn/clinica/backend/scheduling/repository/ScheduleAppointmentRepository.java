package edu.upn.clinica.backend.scheduling.repository;

import edu.upn.clinica.backend.scheduling.model.ScheduleAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleAppointmentRepository extends JpaRepository<ScheduleAppointment, Integer> {

    @Query("SELECT a FROM ScheduleAppointment a WHERE a.doctorId = :doctorId AND a.date = :date " +
           "AND a.status IN ('SCHEDULED', 'COMPLETED') ORDER BY a.startTime")
    List<ScheduleAppointment> findByDoctorAndDate(@Param("doctorId") Integer doctorId,
                                                   @Param("date") LocalDate date);

    List<ScheduleAppointment> findByDoctorIdAndDateBetweenOrderByDateAscStartTimeAsc(
            Integer doctorId, LocalDate from, LocalDate to);

    List<ScheduleAppointment> findByPatientIdOrderByDateDescStartTimeDesc(Integer patientId);

    List<ScheduleAppointment> findByPatientIdAndStatusOrderByDateDescStartTimeDesc(
            Integer patientId, String status);

    boolean existsByDoctorIdAndDateAndStartTimeAndStatus(
            Integer doctorId, LocalDate date, LocalTime startTime, String status);

    boolean existsByPatientIdAndDoctorIdAndDateAndStatus(
            Integer patientId, Integer doctorId, LocalDate date, String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ScheduleAppointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);
}
