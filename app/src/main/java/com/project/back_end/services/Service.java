package com.project.back_end.services;

import com.project.back_end.models.*;
import com.project.back_end.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1. Validate Token
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {

        Map<String, String> response = new HashMap<>();

        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    // 2. Validate Admin Login
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {

        Map<String, String> response = new HashMap<>();

        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null || 
                !admin.getPassword().equals(receivedAdmin.getPassword())) {

                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error during authentication");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 3. Filter Doctor
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {

        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors;

        if ((name == null || name.isEmpty()) &&
            (specialty == null || specialty.isEmpty()) &&
            (time == null || time.isEmpty())) {

            doctors = doctorService.getAllDoctors();

        } else {
            doctors = doctorService
                    .filterDoctorsByNameSpecilityandTime(name, specialty, time);
        }

        response.put("doctors", doctors);
        return response;
    }

    // 4. Validate Appointment
    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> doctorOpt =
                doctorRepository.findById(appointment.getDoctor().getId());

        if (doctorOpt.isEmpty()) {
            return -1; // doctor not found
        }

        Doctor doctor = doctorOpt.get();

        List<String> availableSlots =
                doctorService.getDoctorAvailability(
                        doctor.getId(),
                        appointment.getAppointmentTime().toLocalDate()
                );

        String requestedTime =
                appointment.getAppointmentTime().toLocalTime().toString();

        for (String slot : availableSlots) {
            if (slot.equals(requestedTime)) {
                return 1; // valid
            }
        }

        return 0; // unavailable
    }

    // 5. Validate Patient Registration
    public boolean validatePatient(Patient patient) {

        Patient existing = patientRepository
                .findByEmailOrPhone(patient.getEmail(), patient.getPhone());

        return existing == null;
    }

    // 6. Validate Patient Login
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());

            if (patient == null ||
                !patient.getPassword().equals(login.getPassword())) {

                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error during login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 7. Filter Patient Appointments
    public ResponseEntity<Map<String, Object>> filterPatient(String condition,
                                                              String name,
                                                              String token) {

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Long patientId = patient.getId();

            if (condition != null && !condition.isEmpty() &&
                name != null && !name.isEmpty()) {

                return patientService
                        .filterByDoctorAndCondition(condition, name, patientId);

            } else if (condition != null && !condition.isEmpty()) {

                return patientService.filterByCondition(condition, patientId);

            } else if (name != null && !name.isEmpty()) {

                return patientService.filterByDoctor(name, patientId);

            } else {

                return patientService.getPatientAppointment(patientId, token);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}