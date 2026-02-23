/* =========================================
   DOCTOR CARD COMPONENT
========================================= */

import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

/* =========================================
   Create Doctor Card
========================================= */

export function createDoctorCard(doctor) {

  /* ---------------------------------------
     Main Card Container
  --------------------------------------- */
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  /* ---------------------------------------
     Get User Role
  --------------------------------------- */
  const role = localStorage.getItem("userRole");

  /* ---------------------------------------
     Doctor Info Section
  --------------------------------------- */
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialization}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available: ${
    Array.isArray(doctor.availableTimes)
      ? doctor.availableTimes.join(", ")
      : doctor.availableTimes
  }`;

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  /* ---------------------------------------
     Action Buttons Container
  --------------------------------------- */
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  /* =======================================
     ADMIN ROLE ACTIONS
  ======================================= */
  if (role === "admin") {

    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";

    removeBtn.addEventListener("click", async () => {

      const confirmDelete = confirm("Are you sure you want to delete this doctor?");
      if (!confirmDelete) return;

      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please login again.");
        window.location.href = "/";
        return;
      }

      try {
        await deleteDoctor(doctor.id, token);
        alert("Doctor deleted successfully.");
        card.remove(); // Remove from DOM
      } catch (error) {
        console.error(error);
        alert("Failed to delete doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  /* =======================================
     PATIENT (NOT LOGGED IN)
  ======================================= */
  else if (role === "patient") {

    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  /* =======================================
     LOGGED-IN PATIENT
  ======================================= */
  else if (role === "loggedPatient") {

    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", async (e) => {

      const token = localStorage.getItem("token");

      if (!token) {
        alert("Session expired. Please login again.");
        window.location.href = "/";
        return;
      }

      try {
        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error(error);
        alert("Unable to fetch patient details.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  /* ---------------------------------------
     Final Assembly
  --------------------------------------- */
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
