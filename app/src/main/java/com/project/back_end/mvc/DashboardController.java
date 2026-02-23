package com.project.back_end.mvc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.service.Service; // <-- adjust if your package name differs

@Controller
public class DashboardController {

    // ============================================
    // 1. Autowire Shared Token Validation Service
    // ============================================
    @Autowired
    private Service service;


    // ============================================
    // 2. Admin Dashboard Route
    // ============================================
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        // Validate token for admin role
        Map<String, Object> validationResult = service.validateToken(token, "admin");

        // If map is empty → token is valid
        if (validationResult.isEmpty()) {
            return "admin/adminDashboard";  // Thymeleaf template
        }

        // If token invalid → redirect to login/home
        return "redirect:/";
    }


    // ============================================
    // 3. Doctor Dashboard Route
    // ============================================
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        // Validate token for doctor role
        Map<String, Object> validationResult = service.validateToken(token, "doctor");

        // If token valid
        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard";  // Thymeleaf template
        }

        // If invalid → redirect
        return "redirect:/";
    }
}