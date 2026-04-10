package com.example.controller;

import com.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitContactForm(@RequestParam("name") String name,
                                               @RequestParam("email") String email,
                                               @RequestParam(value = "phone", required = false) String phone,
                                               @RequestParam("message") String message) {
        try {
            // Validation des champs obligatoires
            if (name == null || name.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Le nom est obligatoire");
                return ResponseEntity.badRequest().body(error);
            }

            if (email == null || email.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "L'email est obligatoire");
                return ResponseEntity.badRequest().body(error);
            }

            if (message == null || message.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Le message est obligatoire");
                return ResponseEntity.badRequest().body(error);
            }

            // Envoi de l'email
            emailService.sendContactEmail(name, email, phone, message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Email envoyé avec succès");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur lors de l'envoi de l'email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
