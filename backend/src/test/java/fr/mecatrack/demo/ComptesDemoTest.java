package fr.mecatrack.demo;

import fr.mecatrack.TestcontainersConfiguration;
import fr.mecatrack.auth.AuthService;
import fr.mecatrack.utilisateur.Role;
import fr.mecatrack.utilisateur.Utilisateur;
import fr.mecatrack.utilisateur.UtilisateurRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Avec le profil "demo", un compte par rôle est créé au démarrage.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("demo")
class ComptesDemoTest {

    @Autowired
    private UtilisateurRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ComptesDemoInitialiseur initialiseur;

    @Value("${mecatrack.demo.mot-de-passe}")
    private String motDePasseDemo;

    @Test
    @DisplayName("Un compte de démonstration existe pour chaque rôle")
    void unCompteParRole() {
        assertThat(repository.findByEmailIgnoreCase("admin@mecatrack.fr"))
                .get().extracting(Utilisateur::getRole).isEqualTo(Role.ADMIN);
        assertThat(repository.findByEmailIgnoreCase("technicien@mecatrack.fr"))
                .get().extracting(Utilisateur::getRole).isEqualTo(Role.TECHNICIEN);
        assertThat(repository.findByEmailIgnoreCase("demandeur@mecatrack.fr"))
                .get().extracting(Utilisateur::getRole).isEqualTo(Role.DEMANDEUR);
    }

    @Test
    @DisplayName("Le mot de passe de démonstration permet de se connecter")
    void connexionAvecLeMotDePasseDeDemo() {
        var resultat = authService.connecter("admin@mecatrack.fr", motDePasseDemo);

        assertThat(resultat.jeton().valeur()).isNotBlank();
    }

    @Test
    @DisplayName("Relancer l'initialisation ne crée pas de doublon (idempotence)")
    void initialisationIdempotente() {
        long avant = repository.count();

        initialiseur.run(null);

        assertThat(repository.count()).isEqualTo(avant);
    }
}