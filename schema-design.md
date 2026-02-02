# Smart Clinic – Database Schema Design

This document describes the database design of the Smart Clinic Management System using both MySQL (relational database) and MongoDB (document-based database). The goal is to clearly define structured and flexible data storage and justify design decisions.

---

## MySQL Database Design

MySQL is used to store structured, validated, and relational data. Core entities such as patients, doctors, appointments, and administrators require strong consistency and relationships, making them suitable for a relational database.

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(100), Not Null
- last_name: VARCHAR(100), Not Null
- email: VARCHAR(150), Unique, Not Null
- phone: VARCHAR(20), Unique, Not Null
- password: VARCHAR(255), Not Null
- date_of_birth: DATE, Null
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

**Notes:**
- Email and phone must be unique.
- Passwords are stored as hashed values.
- Patient records are retained even if appointments are deleted.

---

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(100), Not Null
- last_name: VARCHAR(100), Not Null
- specialization: VARCHAR(150), Not Null
- email: VARCHAR(150), Unique, Not Null
- phone: VARCHAR(20), Unique, Not Null
- availability_status: BOOLEAN, Default TRUE
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

**Notes:**
- Doctors should not be deleted permanently; soft deletion can be applied.
- Specialization helps patients filter doctors.

---

### Table: admins
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(100), Unique, Not Null
- email: VARCHAR(150), Unique, Not Null
- password: VARCHAR(255), Not Null
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

**Notes:**
- Admin accounts control system management.
- Passwords must be hashed.

---

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- appointment_time: DATETIME, Not Null
- duration_minutes: INT, Default 60
- status: ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED'), Default 'SCHEDULED'
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

**Notes:**
- A doctor should not have overlapping appointments (enforced by application logic).
- If a patient is deleted, appointments should be retained for historical records.
- Appointments are tied to both doctors and patients.

---

### Table: doctor_availability
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- available_from: DATETIME, Not Null
- available_to: DATETIME, Not Null
- status: ENUM('AVAILABLE', 'UNAVAILABLE'), Default 'AVAILABLE'

**Notes:**
- This table allows doctors to define working hours and unavailable periods.
- It helps prevent booking conflicts.

---

### Design Considerations (MySQL)

- Relational integrity is enforced using foreign keys.
- Critical data (users, appointments, roles) is stored in MySQL due to its structured nature.
- Historical data such as past appointments should be preserved.
- Validation rules (email format, phone format) are handled at the application layer.

---

## MongoDB Collection Design

MongoDB is used to store flexible and semi-structured data that may evolve over time. This includes prescriptions, doctor notes, feedback, logs, and other documents that do not require rigid schemas.

### Collection: prescriptions

MongoDB is suitable for prescriptions because they may vary in structure, contain nested data, and evolve without breaking existing records.

```json
{
  "_id": "ObjectId('65fabc1234567890')",
  "appointmentId": 102,
  "patientId": 12,
  "doctorId": 5,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "duration": "5 days"
    },
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "Every 8 hours",
      "duration": "7 days"
    }
  ],
  "doctorNotes": "Patient shows mild symptoms of infection.",
  "tags": ["infection", "antibiotic"],
  "metadata": {
    "createdAt": "2026-02-02T10:30:00Z",
    "lastUpdated": "2026-02-02T10:45:00Z"
  }
}
