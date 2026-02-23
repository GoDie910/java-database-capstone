// Import API base URL
import { API_BASE_URL } from "../config/config.js";

// Define Doctor API endpoint
const DOCTOR_API = API_BASE_URL + "/doctor";


/*
  Function: getDoctors
  Fetch all doctors
*/
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);

    if (!response.ok) {
      throw new Error("Failed to fetch doctors");
    }

    const data = await response.json();

    // Assuming backend returns { doctors: [...] }
    return data.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}


/*
  Function: deleteDoctor
  Delete doctor by ID (Admin only)
*/
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/delete/${id}/${token}`, {
      method: "DELETE"
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Doctor deletion processed"
    };

  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "Failed to delete doctor"
    };
  }
}


/*
  Function: saveDoctor
  Add a new doctor (Admin only)
*/
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/save/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Doctor saved successfully"
    };

  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "Failed to save doctor"
    };
  }
}


/*
  Function: filterDoctors
  Filter doctors by name, time, specialty
*/
export async function filterDoctors(name, time, specialty) {
  try {
    // Handle empty filters
    const safeName = name || "null";
    const safeTime = time || "null";
    const safeSpecialty = specialty || "null";

    const response = await fetch(
      `${DOCTOR_API}/filter/${safeName}/${safeTime}/${safeSpecialty}`
    );

    if (!response.ok) {
      console.error("Failed to filter doctors");
      return { doctors: [] };
    }

    const data = await response.json();

    return data.doctors || [];

  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Something went wrong while filtering doctors.");
    return [];
  }
}
