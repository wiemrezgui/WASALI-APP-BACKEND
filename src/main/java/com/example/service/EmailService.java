package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service 
public class EmailService {
	
	@Autowired 
	private JavaMailSender sender; 
	
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
}
