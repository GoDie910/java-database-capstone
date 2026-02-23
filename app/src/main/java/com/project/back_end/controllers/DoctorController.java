package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                   @PathVariable Long doctorId,
                                                   @PathVariable String date,
                                                   @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, user);

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        LocalDate parsedDate = LocalDate.parse(date);

        List<String> availability =
                doctorService.getDoctorAvailability(doctorId, parsedDate);

        Map<String, Object> response = new HashMap<>();
        response.put("availability", availability);

        return ResponseEntity.ok(response);
    }

    // 2. Get All Doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {

        List<Doctor> doctors = doctorService.getDoctors();

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);

        return ResponseEntity.ok(response);
    }

    // 3. Add New Doctor (Admin only)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody Doctor doctor,
                                                          @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Map<String, String> response = new HashMap<>();

        try {
            int result = doctorService.saveDoctor(doctor);

            if (result == 1) {
                response.put("message", "Doctor added to db");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "Doctor already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 5. Update Doctor (Admin only)
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor,
                                                            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Map<String, String> response = new HashMap<>();

        try {
            int result = doctorService.updateDoctor(doctor);

            if (result == 1) {
                response.put("message", "Doctor updated");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 6. Delete Doctor (Admin only)
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id,
                                                            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Map<String, String> response = new HashMap<>();

        try {
            int result = doctorService.deleteDoctor(id);

            if (result == 1) {
                response.put("message", "Doctor deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Doctor not found with id");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(@PathVariable String name,
                                                             @PathVariable String time,
                                                             @PathVariable String speciality) {

        Map<String, Object> result =
                service.filterDoctor(name, speciality, time);

        return ResponseEntity.ok(result);
    }
}