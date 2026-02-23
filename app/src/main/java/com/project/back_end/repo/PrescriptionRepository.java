package com.project.back_end.repo;

import java.util.List;

import com.project.back_end.model.Prescription;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // 1️⃣ Find prescriptions by appointment ID
    List<Prescription> findByAppointmentId(Long appointmentId);
}