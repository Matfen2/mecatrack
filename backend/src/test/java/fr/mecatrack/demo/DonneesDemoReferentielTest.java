package fr.mecatrack.demo;

import fr.mecatrack.TestcontainersConfiguration;
import fr.mecatrack.equipement.CriteresRechercheEquipement;
import fr.mecatrack.equipement.EquipementRepository;
import fr.mecatrack.equipement.EquipementService;
import fr.mecatrack.equipement.StatutEquipement;
import fr.mecatrack.referentiel.TypeEquipementRepository;
import fr.mecatrack.referentiel.ZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Avec le profil "demo", un parc d'équipements réaliste est créé au démarrage.
 * Même configuration que ComptesDemoTest : Spring réutilise le même contexte.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("demo")
class DonneesDemoReferentielTest {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private TypeEquipementRepository typeRepository;

    @Autowired
    private EquipementRepository equipementRepository;

    @Autowired
    private EquipementService equipementService;

    @Autowired
    private DonneesDemoReferentielInitialiseur initialiseur;

    @Test
    @DisplayName("Les zones, types et équipements de démonstration sont créés")
    void donneesCreees() {
        assertThat(zoneRepository.count()).isEqualTo(DonneesDemoReferentielInitialiseur.ZONES.size());
        assertThat(typeRepository.count()).isEqualTo(DonneesDemoReferentielInitialiseur.TYPES.size());
        assertThat(equipementRepository.count()).isEqualTo(DonneesDemoReferentielInitialiseur.EQUIPEMENTS.size());
    }

    @Test
    @DisplayName("Certains équipements sont archivés, pour illustrer le filtre par statut")
    void equipementsArchives() {
        var archives = equipementService.rechercher(
                new CriteresRechercheEquipement(StatutEquipement.ARCHIVE, null, null, null), PageRequest.of(0, 50));

        assertThat(archives.getTotalElements()).isPositive();
        assertThat(archives.getContent()).allMatch(e -> e.getStatut() == StatutEquipement.ARCHIVE);
    }

    @Test
    @DisplayName("Relancer l'initialisation ne crée pas de doublon (idempotence)")
    void initialisationIdempotente() {
        long zones = zoneRepository.count();
        long equipements = equipementRepository.count();

        initialiseur.run(null);

        assertThat(zoneRepository.count()).isEqualTo(zones);
        assertThat(equipementRepository.count()).isEqualTo(equipements);
    }
}