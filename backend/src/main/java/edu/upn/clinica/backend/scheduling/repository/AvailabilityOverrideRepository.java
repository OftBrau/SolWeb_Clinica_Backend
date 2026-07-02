package edu.upn.clinica.backend.scheduling.repository;

import edu.upn.clinica.backend.scheduling.model.AvailabilityOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityOverrideRepository extends JpaRepository<AvailabilityOverride, Integer> {

    List<AvailabilityOverride> findByDoctorIdAndDateOrderByStartTime(Integer doctorId, LocalDate date);

    @Query("SELECT o FROM AvailabilityOverride o WHERE o.doctorId = :doctorId " +
           "AND YEAR(o.date) = :year AND MONTH(o.date) = :month ORDER BY o.date, o.startTime")
    List<AvailabilityOverride> findByDoctorAndMonth(@Param("doctorId") Integer doctorId,
                                                     @Param("year") int year,
                                                     @Param("month") int month);
}
