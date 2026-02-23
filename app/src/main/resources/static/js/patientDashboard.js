// ===============================
// Imports
// ===============================
import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';


// ===============================
// DOM Ready
// ===============================
document.addEventListener("DOMContentLoaded", () => {

  // Load doctors on page load
  loadDoctorCards();

  // Bind signup modal button
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  // Bind login modal button
  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  // Search & Filter listeners
  document.getElementById("searchBar")
    .addEventListener("input", filterDoctorsOnChange);

  document.getElementById("filterTime")
    .addEventListener("change", filterDoctorsOnChange);

  document.getElementById("filterSpecialty")
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
    console.error("Failed to load doctors:", error);
    document.getElementById("content").innerHTML =
      "<p>Error loading doctors. Please try again later.</p>";
  }
}


// ===============================
// Filter Doctors
// ===============================
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar").value.trim() || null;
    const time = document.getElementById("filterTime").value || null;
    const specialty = document.getElementById("filterSpecialty").value || null;

    const response = await filterDoctors(name, time, specialty);

    // Depending on your backend structure:
    const doctors = response.doctors || response;

    renderDoctorCards(doctors);

  } catch (error) {
    console.error("Filtering failed:", error);
    document.getElementById("content").innerHTML =
      "<p>Error filtering doctors. Please try again.</p>";
  }
}


// ===============================
// Render Utility
// ===============================
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML =
      "<p>No doctors found with the given filters.</p>";
    return;
  }

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}


// ===============================
// Patient Signup
// ===============================
window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { name, email, password, phone, address };

    const { success, message } = await patientSignup(data);

    if (success) {
      alert(message);
      document.getElementById("modal").classList.remove("active");
      loadDoctorCards();
    } else {
      alert(message);
    }

  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};


// ===============================
// Patient Login
// ===============================
window.loginPatient = async function () {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await patientLogin({ email, password });

    if (response.ok) {
      const result = await response.json();

      localStorage.setItem("token", result.token);

      // Optional: set role if used in your system
      if (typeof selectRole === "function") {
        selectRole("loggedPatient");
      }

      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      alert("❌ Invalid credentials!");
    }

  } catch (error) {
    console.error("Login failed:", error);
    alert("❌ Failed to login.");
  }
};