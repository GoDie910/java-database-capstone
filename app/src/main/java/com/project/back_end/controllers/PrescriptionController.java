package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // 1. Save Prescription (Doctor only)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody Prescription prescription) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Map<String, String> response = new HashMap<>();

        try {

            // Update appointment status (e.g., COMPLETED / PRESCRIBED)
            appointmentService.updateAppointmentStatus(
                    prescription.getAppointmentId(),
                    "PRESCRIBED"
            );

            prescriptionService.savePrescription(prescription);

            response.put("message", "Prescription saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 2. Get Prescription by Appointment ID (Doctor only)
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Map<String, Object> response = new HashMap<>();

        Prescription prescription =
                prescriptionService.getPrescription(appointmentId);

        if (prescription == null) {
            response.put("message",
                    "No prescription found for appointment id " + appointmentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("prescription", prescription);
        return ResponseEntity.ok(response);
    }
}