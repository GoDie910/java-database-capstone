package com.project.back_end.services;

import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // 1. Get Signing Key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // 2. Generate Token (7 days expiration)
    public String generateToken(String identifier) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // 7 days

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. Extract Identifier (email or username)
    public String extractIdentifier(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 4. Validate Token
    public boolean validateToken(String token, String user) {

        try {
            String identifier = extractIdentifier(token);

            switch (user.toLowerCase()) {

                case "admin":
                    return adminRepository.findByUsername(identifier) != null;

                case "doctor":
                    return doctorRepository.findByEmail(identifier) != null;

                case "patient":
                    return patientRepository.findByEmail(identifier) != null;

                default:
                    return false;
            }

        } catch (JwtException | IllegalArgumentException e) {
            // Token invalid, expired, or malformed
            return false;
        }
    }
}