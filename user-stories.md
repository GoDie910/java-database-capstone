# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

---

# Admin User Stories

---

## 1. Admin Login

**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can securely manage the platform._

**Acceptance Criteria:**
1. The system displays a login form with username and password fields.
2. The system authenticates the admin using valid credentials.
3. The system redirects the admin to the admin dashboard after successful login.
4. The system displays an error message when credentials are invalid.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Passwords must be securely stored using hashing.
- Sessions should expire after inactivity.

---

## 2. Admin Logout

**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access after finishing my session._

**Acceptance Criteria:**
1. The system provides a logout option in the admin interface.
2. The system terminates the active admin session.
3. The system redirects the admin to the login page.
4. The admin cannot access restricted pages after logging out.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Session tokens must be invalidated after logout.

---

## 3. Add Doctor

**Title:**  
_As an admin, I want to add doctors to the portal, so that they can provide medical services in the system._

**Acceptance Criteria:**
1. The system provides a form to create a doctor profile.
2. The admin can enter required doctor information (name, specialty, contact details).
3. The system validates mandatory fields before saving.
4. The system stores the doctor information in the database.
5. The new doctor appears in the list of doctors.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- The system should prevent duplicate doctor records.
- Input data must be validated.

---

## 4. Delete Doctor Profile

**Title:**  
_As an admin, I want to delete a doctor’s profile from the portal, so that inactive or incorrect accounts are removed._

**Acceptance Criteria:**
1. The system displays a list of doctors.
2. The admin can select a doctor profile to delete.
3. The system asks for confirmation before deletion.
4. The system removes the doctor record from the database.
5. The deleted doctor no longer appears in the doctors list.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Consider using soft deletion instead of permanent deletion.
- The system should check if the doctor has active appointments before deletion.

---

## 5. View Appointment Statistics (Stored Procedure)

**Title:**  
_As an admin, I want to run a stored procedure in MySQL to view the number of appointments per month, so that I can track system usage statistics._

**Acceptance Criteria:**
1. A stored procedure exists in MySQL that calculates monthly appointment counts.
2. The admin can execute the stored procedure using the MySQL CLI.
3. The stored procedure returns the number of appointments grouped by month.
4. The results can be reviewed for reporting and analysis.

**Priority:** Low  
**Story Points:** 2  

**Notes:**
- This functionality is executed outside the application via MySQL CLI.
- Results may later be integrated into the admin dashboard.

---

# Patient User Stories

---

## 1. View Doctors Without Login

**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore available options before registering._

**Acceptance Criteria:**
1. The system displays a public list of doctors.
2. The list shows basic doctor information (name, specialty, availability).
3. The patient can access the list without authentication.
4. The system prevents access to restricted features without login.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Only non-sensitive information should be displayed.
- Consider adding search or filter options.

---

## 2. Patient Registration (Sign Up)

**Title:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments in the system._

**Acceptance Criteria:**
1. The system provides a registration form with email and password fields.
2. The system validates the input data.
3. The system creates a patient account after successful registration.
4. The system confirms account creation with a success message.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Email must be unique.
- Passwords must be securely stored using hashing.

---

## 3. Patient Login

**Title:**  
_As a patient, I want to log into the portal, so that I can manage my appointments and account._

**Acceptance Criteria:**
1. The system displays a login form for patients.
2. The system authenticates the patient using valid credentials.
3. The system redirects the patient to the dashboard after login.
4. The system displays an error message for invalid credentials.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Session management should be implemented securely.

---

## 4. Patient Logout

**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account after using the system._

**Acceptance Criteria:**
1. The system provides a logout option in the patient interface.
2. The system terminates the active session.
3. The system redirects the patient to the login page.
4. The patient cannot access protected pages after logout.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Session tokens must be invalidated after logout.

---

## 5. Book an Appointment

**Title:**  
_As a patient, I want to book an hour-long appointment with a doctor, so that I can consult with a medical professional._

**Acceptance Criteria:**
1. The patient can select a doctor from the list.
2. The patient can choose an available time slot of one hour.
3. The system checks doctor availability before confirming the booking.
4. The system saves the appointment in the database.
5. The system displays a confirmation of the booked appointment.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Prevent double-booking of time slots.
- Consider time zone handling.

---

## 6. View Upcoming Appointments

**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. The patient can access a list of upcoming appointments.
2. The system displays appointment details (doctor, date, time, status).
3. The list only shows appointments belonging to the logged-in patient.
4. The system updates the list when appointments are added or canceled.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Consider sorting appointments by date and time.
- Past appointments should be displayed separately.

---

# Doctor User Stories

---

## 1. Doctor Login

**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. The system displays a login form for doctors.
2. The system authenticates the doctor using valid credentials.
3. The system redirects the doctor to the doctor dashboard after successful login.
4. The system displays an error message if the credentials are invalid.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Passwords must be stored securely using hashing.
- Session management must be implemented securely.

---

## 2. Doctor Logout

**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my personal and professional data._

**Acceptance Criteria:**
1. The system provides a logout option in the doctor interface.
2. The system terminates the active session.
3. The system redirects the doctor to the login page.
4. The doctor cannot access restricted pages after logging out.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Session tokens must be invalidated after logout.

---

## 3. View Appointment Calendar

**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized and manage my schedule._

**Acceptance Criteria:**
1. The system displays a calendar view of the doctor’s appointments.
2. The calendar shows appointment details (date, time, patient name, status).
3. The system only displays appointments assigned to the logged-in doctor.
4. The calendar updates when appointments are created, modified, or canceled.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Consider different calendar views (daily, weekly, monthly).

---

## 4. Manage Availability

**Title:**  
_As a doctor, I want to mark my unavailability, so that patients can only book appointments in available time slots._

**Acceptance Criteria:**
1. The system allows the doctor to define unavailable time slots.
2. The system prevents patients from booking appointments during unavailable periods.
3. The system updates the doctor’s availability in real time.
4. The system displays only available slots to patients.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Consider recurring availability patterns.
- Handle conflicts with existing appointments.

---

## 5. Update Doctor Profile

**Title:**  
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. The system provides a profile edit form for doctors.
2. The doctor can update specialization, contact information, and other details.
3. The system validates the updated information.
4. The system saves the updated profile in the database.
5. The updated information is visible to patients.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Validate email and phone number formats.
- Track profile update history if needed.

---

## 6. View Patient Details

**Title:**  
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared for consultations._

**Acceptance Criteria:**
1. The system displays patient information for upcoming appointments.
2. The doctor can access details such as patient name, appointment date, and basic medical information.
3. The system restricts access to patient data based on authorization rules.
4. The system ensures that only relevant patient data is displayed.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Sensitive medical data must be protected.
- Access should comply with privacy and security policies.

---
