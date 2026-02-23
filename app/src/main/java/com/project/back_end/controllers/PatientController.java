package com.project.back_end.controllers;

import com.project.back_end.models.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 1. Get Patient Details
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("patient", patientService.getPatientDetails(token));

        return ResponseEntity.ok(response);
    }

    // 2. Create New Patient (Signup)
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {

        Map<String, String> response = new HashMap<>();

        try {

            boolean exists = patientService.existsByEmailOrPhone(
                    patient.getEmail(),
                    patient.getPhoneNo()
            );

            if (exists) {
                response.put("message", "Patient with email id or phone no already exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            patientService.createPatient(patient);
            response.put("message", "Signup successful");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long id,
                                                   @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        List<?> appointments = patientService.getPatientAppointment(id);

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);

        return ResponseEntity.ok(response);
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(@PathVariable String condition,
                                                      @PathVariable String name,
                                                      @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Map<String, Object> result =
                service.filterPatient(condition, name, token);

        return ResponseEntity.ok(result);
    }
}