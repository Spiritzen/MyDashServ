package com.afci.training.planning.controller;

import com.afci.training.planning.dto.FormateurCompDTO;
import com.afci.training.planning.dto.FormateurCompRequest;
import com.afci.training.planning.entity.*;
import com.afci.training.planning.repository.CompetenceRepository;
import com.afci.training.planning.repository.FormateurCompRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.util.FormateurCompMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/competences")
public class MeCompetencesController {

    private final FormateurCompRepository fcRepo;
    private final CompetenceRepository compRepo;
    private final UtilisateurRepository userRepo;

    public MeCompetencesController(FormateurCompRepository fcRepo,
                                   CompetenceRepository compRepo,
                                   UtilisateurRepository userRepo) {
        this.fcRepo = fcRepo; this.compRepo = compRepo; this.userRepo = userRepo;
    }

    private Integer currentFormateurId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Utilisateur non authentifié");
        String email = auth.getName();
        Utilisateur u = userRepo.findByEmailIgnoreCase(email)
                .orElseGet(() -> userRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Utilisateur introuvable: " + email)));
        if (u.getFormateur() == null || u.getFormateur().getIdFormateur() == null)
            throw new AccessDeniedException("Aucun profil formateur lié au compte");
        return u.getFormateur().getIdFormateur();
    }

    /** Liste mes compétences déclarées */
    @GetMapping
    public List<FormateurCompDTO> listMine() {
        Integer fId = currentFormateurId();
        return fcRepo.findByFormateur_IdFormateur(fId).stream()
                .map(FormateurCompMapper::toDTO).toList();
    }

    /** Crée ou met à jour ma déclaration (upsert) */
    @PostMapping
    @Transactional
    public FormateurCompDTO upsert(@RequestBody FormateurCompRequest req) {
        if (req == null || req.getCompetenceId() == null)
            throw new IllegalArgumentException("competenceId obligatoire");

        Integer fId = currentFormateurId();
        Competence c = compRepo.findById(req.getCompetenceId())
                .orElseThrow(() -> new IllegalArgumentException("Compétence inconnue"));

        // Références légères
        Formateur f = new Formateur();
        f.setIdFormateur(fId);

        FormateurCompId id = new FormateurCompId(fId, c.getIdCompetence());
        FormateurComp fc = fcRepo.findById(id).orElseGet(() -> new FormateurComp(f, c, req.getNiveau()));

        // Copie des champs fournis (update partiel)
        if (req.getNiveau() != null)          fc.setNiveau(req.getNiveau());
        if (req.getTitle() != null)           fc.setTitle(req.getTitle());
        if (req.getDescription() != null)     fc.setDescription(req.getDescription());
        if (req.getExperienceYears() != null) fc.setExperienceYears(req.getExperienceYears());
        if (req.getLastUsed() != null)        fc.setLastUsed(req.getLastUsed());
        if (req.getVisible() != null)         fc.setVisible(req.getVisible());

        // Si pas de statut encore défini -> PENDING (validation ultérieure)
        if (fc.getStatus() == null) {
            fc.setStatus(FormateurComp.CompetenceStatus.PENDING);
        }

        return FormateurCompMapper.toDTO(fcRepo.save(fc));
    }
}
