package com.example.utils;

import com.example.model.Privilege;
import com.example.model.Role;
import com.example.model.User;
import com.example.model.User_Privilege;
import com.example.repository.PrivilegeRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.repository.User_PrivilegeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private User_PrivilegeRepository userPrivilegeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Vérifier si les données existent déjà pour éviter les doublons
        if (roleRepository.count() == 0) {
            // Création des rôles
            Role roleAdmin = new Role(1L);
            roleAdmin.setRole("ADMIN");

            Role roleClient = new Role(2L);
            roleClient.setRole("Client");

            Role roleLivreur = new Role(3L);
            roleLivreur.setRole("Livreur");

            roleRepository.save(roleAdmin);
            roleRepository.save(roleClient);
            roleRepository.save(roleLivreur);

            // Création des privilèges avec les noms exacts
            List<String> privilegeNames = Arrays.asList(
                    "gestion clients",
                    "demande et suivi",
                    "gestion et demandes livreurs",
                    "demande et suivi",
                    "CRUD livraisons"
            );

            for (String privilegeName : privilegeNames) {
                Privilege privilege = new Privilege();
                privilege.setPrivilege(privilegeName);
                privilegeRepository.save(privilege);
            }

            System.out.println("✅ " + privilegeRepository.count() + " privilèges créés");

            // Création de l'utilisateur admin uniquement
            User adminUser = new User(
                    "admin@email.com",
                    "admin123",
                    "Admin",
                    "System",
                    "Non spécifié",
                    "456 Avenue de l'Admin, 69001 Lyon",
                    612345678L,
                    roleAdmin
            );
            adminUser.setActivation(true);
            adminUser.setStatut("ACTIF");
            adminUser.setLocalisation("Lyon");

            // Sauvegarde de l'utilisateur admin
            userRepository.save(adminUser);

            // Attribution de TOUS les privilèges à l'utilisateur admin
            List<Privilege> allPrivileges = privilegeRepository.findAll();
            User savedAdminUser = userRepository.findByEmail("admin@email.com").get();

            for (Privilege privilege : allPrivileges) {
                User_Privilege userPrivilege = new User_Privilege();
                userPrivilege.setUser(savedAdminUser);
                userPrivilege.setPrivilege(privilege);
                userPrivilegeRepository.save(userPrivilege);
            }

            System.out.println("✅ Données de test initialisées avec succès !");
            System.out.println("📊 " + roleRepository.count() + " rôles créés");
            System.out.println("🔑 " + privilegeRepository.count() + " privilèges créés");
            System.out.println("👥 " + userRepository.count() + " utilisateur créé");
            System.out.println("🎫 " + userPrivilegeRepository.count() + " associations user-privilèges créées");
            System.out.println("👑 L'utilisateur admin (admin@email.com) a reçu tous les privilèges");
            System.out.println("📋 Liste des privilèges :");
            for (Privilege privilege : allPrivileges) {
                System.out.println("   - " + privilege.getPrivilege());
            }
        } else {
            System.out.println("ℹ️ Les données existent déjà, aucune initialisation effectuée");
        }
    }
}