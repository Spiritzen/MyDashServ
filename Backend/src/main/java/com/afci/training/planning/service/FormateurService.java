// src/main/java/com/afci/training/planning/service/FormateurService.java
package com.afci.training.planning.service;

import com.afci.training.planning.dto.FormateurCreateDTO;
import com.afci.training.planning.dto.FormateurDTO;
import com.afci.training.planning.dto.FormateurUpdateDTO;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.FormateurRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class FormateurService {

    private final FormateurRepository repo;
    private final UtilisateurRepository userRepo;   // <<< NEW

    public FormateurService(FormateurRepository repo, UtilisateurRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

 // --- NEW : récupérer le formateur lié à l'email JWT
    @Transactional(readOnly = true)
    public FormateurDTO findByUserEmail(String email) {
        Utilisateur u = userRepo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + email));

        Formateur f = u.getFormateur(); // <<< on utilise la relation 1-1
        if (f == null) {
            throw new EntityNotFoundException("Aucun formateur rattaché à " + email);
        }

        // Pas besoin de recharger via repo.findById(fid) ici, l'entité est déjà là.
        return toDTO(f);
    }

    public FormateurDTO create(@Valid FormateurCreateDTO dto) {
        Formateur f = new Formateur();
        if (dto.getHeuresMaxMensuelles() != null) f.setHeuresMaxMensuelles(dto.getHeuresMaxMensuelles());
        f.setActif(Boolean.FALSE);
        f.setTauxHoraire(dto.getTauxHoraire());
        f.setZoneIntervention(dto.getZoneIntervention());
        f.setDistanceMaxKm(dto.getDistanceMaxKm());
        if (dto.getMoyenDeDeplacement() != null) {
            f.setMoyenDeDeplacement(Formateur.MoyenDeDeplacement.valueOf(dto.getMoyenDeDeplacement()));
        }
        f.setAnneesExperience(dto.getAnneesExperience());
        f.setBioCourte(dto.getBioCourte());
        f.setLinkedinUrl(dto.getLinkedinUrl());
        f.setPortfolioUrl(dto.getPortfolioUrl());
        if (dto.getStatutJuridique() != null) {
            f.setStatutJuridique(Formateur.StatutJuridique.valueOf(dto.getStatutJuridique()));
        }
        f.setSiret(dto.getSiret());
        if (dto.getRibFournit() != null) f.setRibFournit(dto.getRibFournit());
        if (dto.getProfilComplet() != null) f.setProfilComplet(dto.getProfilComplet());

        f = repo.save(f);
        return toDTO(f);
    }

    @Transactional(readOnly = true)
    public List<FormateurDTO> findAll() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public FormateurDTO findById(Integer id) {
        Formateur f = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formateur " + id + " introuvable"));
        return toDTO(f);
    }

    public FormateurDTO update(Integer id, @Valid FormateurUpdateDTO dto) {
        Formateur f = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formateur " + id + " introuvable"));

        if (dto.getHeuresMaxMensuelles() != null) f.setHeuresMaxMensuelles(dto.getHeuresMaxMensuelles());
        if (dto.getActif() != null) f.setActif(dto.getActif());

        if (dto.getTauxHoraire() != null) f.setTauxHoraire(dto.getTauxHoraire());
        if (dto.getZoneIntervention() != null) f.setZoneIntervention(dto.getZoneIntervention());
        if (dto.getDistanceMaxKm() != null) f.setDistanceMaxKm(dto.getDistanceMaxKm());
        if (dto.getMoyenDeDeplacement() != null) {
            f.setMoyenDeDeplacement(Formateur.MoyenDeDeplacement.valueOf(dto.getMoyenDeDeplacement()));
        }
        if (dto.getAnneesExperience() != null) f.setAnneesExperience(dto.getAnneesExperience());
        if (dto.getBioCourte() != null) f.setBioCourte(dto.getBioCourte());
        if (dto.getLinkedinUrl() != null) f.setLinkedinUrl(dto.getLinkedinUrl());
        if (dto.getPortfolioUrl() != null) f.setPortfolioUrl(dto.getPortfolioUrl());
        if (dto.getStatutJuridique() != null) {
            f.setStatutJuridique(Formateur.StatutJuridique.valueOf(dto.getStatutJuridique()));
        }
        if (dto.getSiret() != null) f.setSiret(dto.getSiret());
        if (dto.getRibFournit() != null) f.setRibFournit(dto.getRibFournit());
        if (dto.getProfilComplet() != null) f.setProfilComplet(dto.getProfilComplet());

        if (dto.getSoumettre() != null && dto.getSoumettre().booleanValue()) {
            f.setProfilSoumisLe(Instant.now());
            if (f.getProfilComplet() == null || !f.getProfilComplet()) f.setProfilComplet(Boolean.TRUE);
        }

        return toDTO(f);
    }

    public void delete(Integer id) {
        if (!repo.existsById(id)) throw new EntityNotFoundException("Formateur " + id + " introuvable");
        repo.deleteById(id);
    }

    private FormateurDTO toDTO(Formateur f) {
        return new FormateurDTO(
                f.getIdFormateur(),
                f.getHeuresMaxMensuelles(),
                f.getActif(),
                f.getTauxHoraire(),
                f.getZoneIntervention(),
                f.getDistanceMaxKm(),
                f.getMoyenDeDeplacement() != null ? f.getMoyenDeDeplacement().name() : null,
                f.getAnneesExperience(),
                f.getBioCourte(),
                f.getLinkedinUrl(),
                f.getPortfolioUrl(),
                f.getStatutJuridique() != null ? f.getStatutJuridique().name() : null,
                f.getSiret(),
                f.getRibFournit(),
                f.getProfilComplet(),
                f.getProfilSoumisLe(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
