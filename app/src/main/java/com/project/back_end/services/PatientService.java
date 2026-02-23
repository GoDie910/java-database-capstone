package com.project.back_end.services;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Create Patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 2. Get Patient Appointments
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null || !patient.getId().equals(id)) {
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<AppointmentDTO> appointments = patient.getAppointments()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error retrieving appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 3. Filter by Condition (Past / Future)
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<AppointmentDTO> appointments = appointmentRepository
                    .findByPatientIdAndStatus(id, status)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 4. Filter by Doctor
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<AppointmentDTO> appointments = appointmentRepository
                    .findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, name)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering by doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 5. Filter by Doctor and Condition
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition,
                                                                           String name,
                                                                           long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<AppointmentDTO> appointments = appointmentRepository
                    .findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, name, status)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 6. Get Patient Details
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error retrieving patient details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper Method: Convert Appointment to DTO
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}
