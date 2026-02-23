package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    // Constructor Injection
    public AppointmentController(AppointmentService appointmentService,
                                 Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 1. Get Appointments (Doctor only)
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        LocalDate parsedDate = LocalDate.parse(date);

        Map<String, Object> result =
                appointmentService.getAppointment(patientName, parsedDate, token);

        return ResponseEntity.ok(result);
    }

    // 2. Book Appointment (Patient only)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int check = service.validateAppointment(appointment);

        Map<String, String> response = new HashMap<>();

        if (check == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (check == 0) {
            response.put("message", "Appointment time not available");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        int result = appointmentService.bookAppointment(appointment);

        if (result == 1) {
            response.put("message", "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Error booking appointment");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 3. Update Appointment (Patient only)
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return appointmentService.updateAppointment(appointment);
    }

    // 4. Cancel Appointment (Patient only)
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}