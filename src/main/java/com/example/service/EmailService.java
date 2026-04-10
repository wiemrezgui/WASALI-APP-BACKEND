package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service 
public class EmailService {
	
	@Autowired 
	private JavaMailSender sender;
    @Value("${spring.mail.username}")

    private String fromEmail;

	public boolean send(String email, String resetLink) {
        
        try{
        	MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper hp;
            hp = new MimeMessageHelper(message, true);
            hp.setFrom("WASSALI<haddad.iheb99@gmail.com>");
            hp.setTo(email);
            hp.setSubject("WASSALI - Rénitialiser mot de passe");
            hp.setText("Voici le <a href=\"" + resetLink  + "\" style=\"text-decoration: underline; color: blue;\">lien</a> de reinitialisation de votre mot de passe.", true);
            sender.send(message); 
            return true; 
        }
        catch(Exception e) {
        	return false; 
        }
        
		  
    }
	
	public boolean send(String email, String link, String msg) {
        
        try{
        	MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper hp;
            hp = new MimeMessageHelper(message, true);
            hp.setFrom("WASSALI<haddad.iheb99@gmail.com>");
            hp.setTo(email);
            hp.setSubject("WASSALI - Résultat Inscription");
            hp.setText(msg + " <a href=\"" + link  + "\" style=\"text-decoration: underline; color: blue;\">lien</a>.", true);
            sender.send(message);
            return true; 
        }
        catch(Exception e) {
        	return false; 
        }
        
		  
    }
	
	public boolean sendd(String email, String msg) {
        
        try{
        	MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper hp;
            hp = new MimeMessageHelper(message, true);
            hp.setFrom("WASSALI<haddad.iheb99@gmail.com>");
            hp.setTo(email);
            hp.setSubject("WASSALI - Nouvelle Livraison");
            hp.setText("Vous avez une nouvelle livraison en attente avec la reference : " + msg, true);
            sender.send(message);
            return true; 
        }
        catch(Exception e) {
        	return false; 
        }
        
		  
    }
	
	public boolean senddd(String titre, String email, Long ref, String etat) {
        
        try{
        	MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper hp;
            hp = new MimeMessageHelper(message, true);
            hp.setFrom("WASSALI<haddad.iheb99@gmail.com>");
            hp.setTo(email);
            hp.setSubject("WASSALI - " + titre);
            hp.setText("Votre livraison avec la reference : " + ref + " est " + etat, true);
            sender.send(message);
            return true; 
        }
        catch(Exception e) {
        	return false; 
        }
        
		  
    }
	
	public boolean senddd(String email, Long ref) {
        
        try{
        	MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper hp;
            hp = new MimeMessageHelper(message, true);
            hp.setFrom("WASSALI<haddad.iheb99@gmail.com>");
            hp.setTo(email);
            hp.setSubject("WASSALI - Colis récupéré");
            hp.setText("Votre colis avec la reference : " + ref + " est recupere", true);
            sender.send(message);
            return true; 
        }
        catch(Exception e) {
        	return false; 
        }
        	  
    }
    public void sendContactEmail(String name, String email, String phone, String message) {
        try {
            // 1. Envoyer l'email à l'administrateur
            sendEmailToAdmin(name, email, phone, message);

            // 2. Envoyer l'email de confirmation à l'utilisateur
            sendConfirmationToUser(name, email);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

    // Envoie l'email à l'administrateur
    private void sendEmailToAdmin(String name, String email, String phone, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(fromEmail);
        mailMessage.setTo("haddad.iheb99@gmail.com");
        mailMessage.setSubject("Nouveau message de contact - " + name);

        String emailBody = "========================================\n" +
                "NOUVEAU MESSAGE DE CONTACT\n" +
                "========================================\n\n" +
                "Détails de l'expéditeur :\n" +
                "------------------------\n" +
                "Nom complet : " + name + "\n" +
                "Email : " + email + "\n" +
                "Téléphone : " + (phone != null && !phone.isEmpty() ? phone : "Non fourni") + "\n\n" +
                "Message :\n" +
                "---------\n" +
                message + "\n\n" +
                "========================================\n" +
                "Cet email a été envoyé automatiquement.\n" +
                "========================================";

        mailMessage.setText(emailBody);
        sender.send(mailMessage);
    }

    // Envoie un email de confirmation à l'utilisateur
    private void sendConfirmationToUser(String name, String email) {
        SimpleMailMessage confirmationMessage = new SimpleMailMessage();

        confirmationMessage.setFrom(fromEmail);
        confirmationMessage.setTo(email);
        confirmationMessage.setSubject("Confirmation de votre message - Wassali");

        String confirmationBody = "Bonjour " + name + ",\n\n" +
                "Nous accusons bonne réception de votre message.\n\n" +
                "Notre équipe vous répondra dans les plus brefs délais (sous 24h).\n\n" +
                "Merci de votre confiance.\n\n" +
                "Cordialement,\n" +
                "L'équipe Wassali\n" +
                "Cet email est un accusé de réception automatique.";

        confirmationMessage.setText(confirmationBody);
        sender.send(confirmationMessage);
    }
}
