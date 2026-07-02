package edu.upn.clinica.backend.scheduling.repository;

import edu.upn.clinica.backend.scheduling.model.AvailabilityTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvailabilityTemplateRepository extends JpaRepository<AvailabilityTemplate, Integer> {

    List<AvailabilityTemplate> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(Integer doctorId);

    List<AvailabilityTemplate> findByDoctorIdAndDayOfWeekOrderByStartTime(Integer doctorId, String dayOfWeek);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM AvailabilityTemplate t WHERE t.doctorId = :doctorId")
    void deleteByDoctorId(@Param("doctorId") Integer doctorId);
}
