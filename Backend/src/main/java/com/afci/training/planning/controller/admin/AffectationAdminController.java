package com.afci.training.planning.controller.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.afci.training.planning.dto.AffectationAssignRequest;
import com.afci.training.planning.dto.AffectationCandidateDTO;
import com.afci.training.planning.entity.Affectation;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.service.AffectationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/admin/affectations", "/api/affectation"}) // alias compatible
@PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
public class AffectationAdminController {

    private final AffectationRepository affectationRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final AffectationService affectationService;

    public AffectationAdminController(AffectationRepository affectationRepo,
                                      UtilisateurRepository utilisateurRepo,
                                      AffectationService affectationService) {
        this.affectationRepo = affectationRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.affectationService = affectationService;
    }

    /** GET /api/affectation/candidates?sessionId=12&limit=10 */
    @GetMapping("/candidates")
    public ResponseEntity<List<AffectationCandidateDTO>> candidates(
            @RequestParam Integer sessionId,
            @RequestParam(defaultValue = "10") int limit) {

        // On tire directement le service qui renvoie déjà AffectationCandidateDTO
        List<AffectationCandidateDTO> out = affectationService.findCandidates(sessionId);
        if (limit > 0 && out.size() > limit) {
            out = out.subList(0, limit);
        }
        return ResponseEntity.ok(out);
    }

    /** POST /api/affectation/assign { sessionId, formateurId } */
    @PostMapping("/assign")
    public ResponseEntity<Void> assign(@Valid @RequestBody AffectationAssignRequest req) {
        affectationService.assign(req);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/affectation/session/{id} */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AffectationView>> listForSession(@PathVariable Integer sessionId) {
        List<Affectation> list = affectationRepo.findBySession_IdSession(sessionId);
        List<AffectationView> out = new ArrayList<>();

        for (Affectation a : list) {
            Integer fid = a.getFormateur() != null ? a.getFormateur().getIdFormateur() : null;

            String prenom = null;
            String nom = null;

            if (fid != null) {
                utilisateurRepo.findFirstByFormateurId(fid).ifPresent(u -> {
                    // lambda doit capturer via array/holder si on veut muter… on fait simple :
                });
                Utilisateur u = utilisateurRepo.findFirstByFormateurId(fid).orElse(null);
                if (u != null) { prenom = u.getPrenom(); nom = u.getNom(); }
            }

            Session s = a.getSession();
            out.add(new AffectationView(
                    a.getIdAffectation(),
                    (a.getStatut() != null ? a.getStatut().name() : null),
                    fid,
                    prenom,
                    nom,
                    (s != null ? s.getIdSession() : null),
                    (s != null ? s.getDateDebut() : null),
                    (s != null ? s.getDateFin() : null)
            ));
        }
        return ResponseEntity.ok(out);
    }

    public static record AffectationView(
            Integer idAffectation,
            String statut,
            Integer formateurId,
            String prenom,
            String nom,
            Integer sessionId,
            LocalDateTime dateDebut,
            LocalDateTime dateFin
    ) {}
}
