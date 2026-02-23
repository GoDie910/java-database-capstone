package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.project.back_end.DTO.Login;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1️⃣ Get Doctor Availability
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, start, end);

        List<String> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        return doctor.getAvailabilities().stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    // 2️⃣ Save Doctor
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // already exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 3️⃣ Update Doctor
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 4️⃣ Get All Doctors
    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // 5️⃣ Delete Doctor
    @Transactional
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 6️⃣ Validate Doctor Login
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "DOCTOR");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // 7️⃣ Find Doctor By Name
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctorRepository.findByNameLike(name));
        return map;
    }

    // 8️⃣ Filter: Name + Specialty + Time
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(
            String name, String specialty, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name, specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // 9️⃣ Filter: Name + Time
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(
            String name, String amOrPm) {

        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // 🔟 Filter: Name + Specialty
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(
            String name, String specialty) {

        Map<String, Object> map = new HashMap<>();
        map.put("doctors",
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name, specialty));
        return map;
    }

    // 1️⃣1️⃣ Filter: Specialty + Time
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(
            String specialty, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // 1️⃣2️⃣ Filter: Specialty Only
    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {

        Map<String, Object> map = new HashMap<>();
        map.put("doctors",
                doctorRepository.findBySpecialtyIgnoreCase(specialty));
        return map;
    }

    // 1️⃣3️⃣ Filter: Time Only
    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {

        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // 🔒 Private Helper Method
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {

        return doctors.stream()
                .filter(d -> d.getAvailabilities().stream().anyMatch(time -> {
                    int hour = Integer.parseInt(time.split(":")[0]);
                    return ("AM".equalsIgnoreCase(amOrPm) && hour < 12) ||
                           ("PM".equalsIgnoreCase(amOrPm) && hour >= 12);
                }))
                .collect(Collectors.toList());
    }
}
