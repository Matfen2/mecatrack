package fr.mecatrack.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {

    boolean existsByReferenceIgnoreCase(String reference);
}