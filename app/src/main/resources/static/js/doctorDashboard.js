// ===============================
// Imports
// ===============================
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";


// ===============================
// Global Variables
// ===============================

// Table body reference
const tableBody = document.getElementById("patientTableBody");

// Today's date in YYYY-MM-DD format
let selectedDate = new Date().toISOString().split("T")[0];

// Token from localStorage
const token = localStorage.getItem("token");

// Patient name filter (default null as string for backend)
let patientName = "null";


// ===============================
// Event Listeners
// ===============================
document.addEventListener("DOMContentLoaded", () => {

    // Set date picker default value to today
    const datePicker = document.getElementById("datePicker");
    datePicker.value = selectedDate;

    // Load today's appointments
    loadAppointments();

    // ===========================
    // Search Bar
    // ===========================
    document.getElementById("searchBar")
        .addEventListener("input", (e) => {

            const value = e.target.value.trim();

            if (value !== "") {
                patientName = value;
            } else {
                patientName = "null";
            }

            loadAppointments();
        });

    // ===========================
    // Today Button
    // ===========================
    document.getElementById("todayButton")
        .addEventListener("click", () => {

            selectedDate = new Date().toISOString().split("T")[0];
            datePicker.value = selectedDate;

            loadAppointments();
        });

    // ===========================
    // Date Picker
    // ===========================
    datePicker.addEventListener("change", (e) => {

        selectedDate = e.target.value;
        loadAppointments();
    });
});


// ===============================
// Load Appointments
// ===============================
async function loadAppointments() {

    try {

        // Clear previous rows
        tableBody.innerHTML = "";

        // Fetch appointments from backend
        const appointments = await getAllAppointments(
            selectedDate,
            patientName,
            token
        );

        // If no appointments
        if (!appointments || appointments.length === 0) {

            const row = document.createElement("tr");
            row.innerHTML = `
                <td colspan="5" style="text-align:center;">
                    No Appointments found for today.
                </td>
            `;
            tableBody.appendChild(row);
            return;
        }

        // Render appointments
        appointments.forEach(appointment => {

            // Construct patient object
            const patient = {
                id: appointment.patient.id,
                name: appointment.patient.name,
                phone: appointment.patient.phone,
                email: appointment.patient.email
            };

            // Create row
            const row = createPatientRow(patient, appointment);

            // Append to table
            tableBody.appendChild(row);
        });

    } catch (error) {

        console.error("Error loading appointments:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align:center;">
                    Error loading appointments. Try again later.
                </td>
            </tr>
        `;
    }
}


/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/
