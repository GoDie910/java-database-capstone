// ===============================
// Imports
// ===============================
import { openModal } from "../js/components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";


// ===============================
// Event: Add Doctor Button
// ===============================
document.addEventListener("DOMContentLoaded", () => {

    // Load all doctors when page loads
    loadDoctorCards();

    // Add Doctor button listener
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    }

    // Search & filter listeners
    document.getElementById("searchBar")
        .addEventListener("input", filterDoctorsOnChange);

    document.getElementById("timeFilter")
        .addEventListener("change", filterDoctorsOnChange);

    document.getElementById("specialtyFilter")
        .addEventListener("change", filterDoctorsOnChange);
});


// ===============================
// Load All Doctors
// ===============================
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}


// ===============================
// Filter Doctors On Change
// ===============================
async function filterDoctorsOnChange() {
    try {
        const name = document.getElementById("searchBar").value.trim() || null;
        const time = document.getElementById("timeFilter").value || null;
        const specialty = document.getElementById("specialtyFilter").value || null;

        const doctors = await filterDoctors(name, time, specialty);

        if (doctors && doctors.length > 0) {
            renderDoctorCards(doctors);
        } else {
            const contentDiv = document.getElementById("content");
            contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        }

    } catch (error) {
        alert("Error filtering doctors.");
        console.error(error);
    }
}


// ===============================
// Render Doctor Cards
// ===============================
function renderDoctorCards(doctors) {

    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}


// ===============================
// Add Doctor (Admin)
// ===============================
async function adminAddDoctor() {

    try {
        // Collect form values (make sure these IDs exist in modal form)
        const name = document.getElementById("doctorName").value;
        const email = document.getElementById("doctorEmail").value;
        const phone = document.getElementById("doctorPhone").value;
        const password = document.getElementById("doctorPassword").value;
        const specialty = document.getElementById("doctorSpecialty").value;

        // Collect availability checkboxes
        const availabilityCheckboxes = document.querySelectorAll(
            "input[name='availability']:checked"
        );

        const availability = Array.from(availabilityCheckboxes)
            .map(cb => cb.value);

        // Get token
        const token = localStorage.getItem("token");

        if (!token) {
            alert("You must be logged in as admin.");
            return;
        }

        const doctor = {
            name,
            email,
            phone,
            password,
            specialty,
            availability
        };

        await saveDoctor(doctor, token);

        alert("Doctor added successfully!");

        // Close modal
        document.getElementById("modal").classList.remove("active");

        // Reload doctors list
        loadDoctorCards();

    } catch (error) {
        alert("Failed to add doctor.");
        console.error(error);
    }
}


// ===============================
// Make function globally accessible
// (So modal form can call it)
// ===============================
window.adminAddDoctor = adminAddDoctor;

/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
