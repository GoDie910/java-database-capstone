package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.project.back_end.model.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    // Constructor Injection
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    // 1️⃣ Book Appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 2️⃣ Update Appointment
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());

        if (existing.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        // Optional: Add validation logic here (e.g., validateAppointment())
        appointmentRepository.save(appointment);

        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    // 3️⃣ Cancel Appointment
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existing = appointmentRepository.findById(id);

        if (existing.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = existing.get();

        // Example ownership validation (adjust based on your token logic)
        Long patientIdFromToken = tokenService.extractUserId(token);

        if (!appointment.getPatient().getId().equals(patientIdFromToken)) {
            response.put("message", "Unauthorized to cancel this appointment");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);

        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    // 4️⃣ Get Appointments by Doctor & Date (Optional patient name filter)
    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {

        Map<String, Object> result = new HashMap<>();

        Long doctorId = tokenService.extractUserId(token);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> appointments;

        if (pname != null && !pname.isBlank()) {
            appointments =
                    appointmentRepository
                            .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                    doctorId, pname, start, end
                            );
        } else {
            appointments =
                    appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetween(
                                    doctorId, start, end
                            );
        }

        result.put("appointments", appointments);
        return result;
    }
}