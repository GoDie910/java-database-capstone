// Import API base URL
import { API_BASE_URL } from "../config/config.js";

// Base endpoint for patient-related APIs
const PATIENT_API = API_BASE_URL + "/patient";


/*
  Function: patientSignup
  Purpose: Register a new patient
*/
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(result.message || "Signup failed");
    }

    return {
      success: true,
      message: result.message || "Signup successful"
    };

  } catch (error) {
    console.error("Error :: patientSignup ::", error);
    return {
      success: false,
      message: error.message || "Something went wrong"
    };
  }
}


/*
  Function: patientLogin
  Purpose: Authenticate patient
  Returns full response so frontend can extract token
*/
export async function patientLogin(data) {
  try {
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    return response; // frontend handles token extraction

  } catch (error) {
    console.error("Error :: patientLogin ::", error);
    return null;
  }
}


/*
  Function: getPatientData
  Purpose: Fetch logged-in patient details using token
*/
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);

    if (!response.ok) {
      throw new Error("Failed to fetch patient data");
    }

    const data = await response.json();

    return data.patient || null;

  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}


/*
  Function: getPatientAppointments
  Purpose: Fetch appointments for patient or doctor dashboard
*/
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(
      `${PATIENT_API}/${id}/${user}/${token}`
    );

    if (!response.ok) {
      throw new Error("Failed to fetch appointments");
    }

    const data = await response.json();

    return data.appointments || [];

  } catch (error) {
    console.error("Error fetching appointments:", error);
    return [];
  }
}


/*
  Function: filterAppointments
  Purpose: Filter appointments by condition and patient name
*/
export async function filterAppointments(condition, name, token) {
  try {
    // Handle empty filters safely
    const safeCondition = condition || "null";
    const safeName = name || "null";

    const response = await fetch(
      `${PATIENT_API}/filter/${safeCondition}/${safeName}/${token}`
    );

    if (!response.ok) {
      throw new Error("Failed to filter appointments");
    }

    const data = await response.json();

    return data.appointments || [];

  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("Something went wrong while filtering appointments.");
    return [];
  }
}
