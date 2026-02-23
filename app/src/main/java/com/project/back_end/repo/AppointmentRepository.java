package com.project.back_end.repo;

import java.time.LocalDateTime;
import java.util.List;

import com.project.back_end.model.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1️⃣ Retrieve appointments for a doctor within a time range
    @Query("""
           SELECT a FROM Appointment a
           LEFT JOIN FETCH a.doctor d
           LEFT JOIN FETCH d.availabilities
           WHERE d.id = :doctorId
           AND a.appointmentTime BETWEEN :start AND :end
           """)
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    // 2️⃣ Filter by doctor, partial patient name (case-insensitive), and time range
    @Query("""
           SELECT a FROM Appointment a
           LEFT JOIN FETCH a.doctor d
           LEFT JOIN FETCH a.patient p
           WHERE d.id = :doctorId
           AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))
           AND a.appointmentTime BETWEEN :start AND :end
           """)
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId,
            String patientName,
            LocalDateTime start,
            LocalDateTime end
    );

    // 3️⃣ Delete all appointments by doctor ID
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    // 4️⃣ Find all appointments for a specific patient
    List<Appointment> findByPatientId(Long patientId);

    // 5️⃣ Find appointments by patient and status ordered by time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
            Long patientId,
            int status
    );

    // 6️⃣ Filter by doctor name and patient ID
    @Query("""
           SELECT a FROM Appointment a
           JOIN a.doctor d
           WHERE a.patient.id = :patientId
           AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
           """)
    List<Appointment> filterByDoctorNameAndPatientId(
            String doctorName,
            Long patientId
    );

    // 7️⃣ Filter by doctor name, patient ID, and status
    @Query("""
           SELECT a FROM Appointment a
           JOIN a.doctor d
           WHERE a.patient.id = :patientId
           AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
           AND a.status = :status
           """)
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            String doctorName,
            Long patientId,
            int status
    );
}