// src/main/java/com/afci/training/planning/service/impl/AffectationServiceImpl.java
package com.afci.training.planning.service.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.afci.training.planning.dto.AffectationAssignRequest;
import com.afci.training.planning.dto.AffectationCandidateDTO;
import com.afci.training.planning.entity.Affectation;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.exception.ConflictException;
import com.afci.training.planning.exception.NotFoundException;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.CompetenceRepository;
import com.afci.training.planning.repository.CongeRepository;
import com.afci.training.planning.repository.DisponibiliteRepository;
import com.afci.training.planning.repository.FormateurCompRepository;
import com.afci.training.planning.repository.FormateurRepository;
import com.afci.training.planning.repository.FormationRepository;
import com.afci.training.planning.repository.SessionRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.repository.projection.UtilisateurLiteByFormateurProjection;
import com.afci.training.planning.service.AffectationService;

@Service
public class AffectationServiceImpl implements AffectationService {

    private final SessionRepository sessionRepo;
    private final FormationRepository formationRepo;
    private final FormateurRepository formateurRepo;
    private final FormateurCompRepository formateurCompRepo;
    private final CompetenceRepository competenceRepo;
    private final CongeRepository congeRepo;
    private final DisponibiliteRepository dispoRepo;
    private final AffectationRepository affectationRepo;

    // ✅ AJOUT : pour récupérer prénom/nom/email via Utilisateur (lié au Formateur)
    private final UtilisateurRepository utilisateurRepo;

    public AffectationServiceImpl(SessionRepository sessionRepo,
                                  FormationRepository formationRepo,
                                  FormateurRepository formateurRepo,
                                  FormateurCompRepository formateurCompRepo,
                                  CompetenceRepository competenceRepo,
                                  CongeRepository congeRepo,
                                  DisponibiliteRepository dispoRepo,
                                  AffectationRepository affectationRepo,
                                  UtilisateurRepository utilisateurRepo) {
        this.sessionRepo = sessionRepo;
        this.formationRepo = formationRepo;
        this.formateurRepo = formateurRepo;
        this.formateurCompRepo = formateurCompRepo;
        this.competenceRepo = competenceRepo;
        this.congeRepo = congeRepo;
        this.dispoRepo = dispoRepo;
        this.affectationRepo = affectationRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    // ---------------------------
    // CANDIDATS (matching & score)
    // ---------------------------
    @Override
    @Transactional(readOnly = true)
    public List<AffectationCandidateDTO> findCandidates(Integer sessionId) {
        Session s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Session introuvable"));

        LocalDateTime start = s.getDateDebut();
        LocalDateTime end   = s.getDateFin();
        if (start == null || end == null) {
            throw new ResponseStatusException(BAD_REQUEST, "La session doit avoir des dates valides.");
        }

        // Intitulé formation
        String intitule = formationRepo.findById(s.getFormationId())
                .map(f -> (String) callIfExists(f, "getIntitule"))
                .orElse("");
        String titleLC = intitule == null ? "" : intitule.toLowerCase(Locale.ROOT);

        // Tous formateurs actifs + validés
        List<Formateur> formateurs = formateurRepo.findAllActiveAndValidated();

        // ✅ Précharge users (prenom/nom/email) en 1 requête (évite N+1)
        List<Integer> formateurIds = formateurs.stream()
                .map(f -> (Integer) callIfExists(f, "getIdFormateur"))
                .filter(id -> id != null)
                .toList();

        // ⚠️ Nécessite cette méthode côté UtilisateurRepository :
        // List<UtilisateurLiteByFormateurProjection> fetchUsersLiteByFormateurs(List<Integer> ids);
        Map<Integer, UtilisateurLiteByFormateurProjection> userByFormateurId =
                formateurIds.isEmpty()
                        ? Map.of()
                        : utilisateurRepo.fetchUsersLiteByFormateurs(formateurIds).stream()
                            .collect(Collectors.toMap(
                                UtilisateurLiteByFormateurProjection::getFormateurId,
                                u -> u,
                                (a, b) -> a // en cas de doublon (théoriquement non)
                            ));

        // Précharge toutes les compétences (id -> label)
        Map<Integer, String> compLabelById = competenceRepo.findAll().stream()
                .collect(Collectors.toMap(
                        c -> (Integer) callIfExists(c, "getIdCompetence"),
                        c -> (String) callIfExists(c, "getLabel")
                ));

        List<AffectationCandidateDTO> out = new ArrayList<>();

        for (Formateur f : formateurs) {
            Integer fid = (Integer) callIfExists(f, "getIdFormateur");
            if (fid == null) continue;

            // Compétences du formateur
            var fcs = formateurCompRepo.findAllByFormateurId(fid);
            List<String> labels = fcs.stream()
                    .map(fc -> compLabelById.getOrDefault((Integer) callIfExists(fc, "getCompetenceId"), null))
                    .filter(lbl -> lbl != null && !lbl.isBlank())
                    .toList();

            // matching naïf : label présent dans l'intitulé
            List<String> ok = new ArrayList<>();
            List<String> ko = new ArrayList<>();
            for (String lbl : labels) {
                if (titleLC.contains(lbl.toLowerCase(Locale.ROOT))) ok.add(lbl);
                else ko.add(lbl);
            }

            // Conflits / indispos
            boolean hasBusy  = !dispoRepo.findBusyOverlaps(fid, start, end).isEmpty();
            boolean hasConge = !congeRepo.findOverlaps(fid, start, end).isEmpty();
            int conflicts    = (int) affectationRepo.countConflicts(fid, start, end);

            // score
            double score = 0;
            if (!ok.isEmpty()) {
                score += 30;
                score += (ok.size() - 1) * 10;
            }
            if (hasConge) score -= 40;
            if (hasBusy)  score -= 30;
            score -= conflicts * 25;

            // ✅ Infos humaines via Utilisateur
            UtilisateurLiteByFormateurProjection u = userByFormateurId.get(fid);

            AffectationCandidateDTO dto = new AffectationCandidateDTO();
            dto.setFormateurId(fid);
            dto.setPrenom(u != null ? u.getPrenom() : null);
            dto.setNom(u != null ? u.getNom() : null);
            dto.setEmail(u != null ? u.getEmail() : null);

            dto.setSkillsOK(ok);
            dto.setSkillsKO(ko);
            dto.setDispoOK(!hasBusy && !hasConge && conflicts == 0);
            dto.setEnConge(hasConge);
            dto.setNbConflits(conflicts);
            dto.setScore(score);
            dto.setCommentaires(null);

            out.add(dto);
        }

        // tri décroissant par score
        out.sort(Comparator.comparingDouble(AffectationCandidateDTO::getScore).reversed());
        return out;
    }

    // --------------
    // ASSIGN (direct)
    // --------------
    @Override
    @Transactional
    public void assign(AffectationAssignRequest req) {
        if (req == null || req.getSessionId() == null || req.getFormateurId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Paramètres manquants.");
        }
        assignerDirect(req.getSessionId(), req.getFormateurId());
    }

    // ------------------------
    // PROPOSER / RÉPONDRE / DIRECT
    // ------------------------
    @Override
    @Transactional
    public void proposer(Integer sessionId, Integer formateurId, String commentaire) {
        if (sessionId == null || formateurId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Paramètres manquants.");
        }

        boolean exists = affectationRepo.existsBySession_IdSessionAndFormateur_IdFormateurAndStatut(
                sessionId, formateurId, Affectation.Statut.PROPOSEE);
        if (exists) return;

        Session s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session introuvable: " + sessionId));
        Formateur f = formateurRepo.findById(formateurId)
                .orElseThrow(() -> new NotFoundException("Formateur introuvable: " + formateurId));

        Affectation a = new Affectation();
        a.setSession(s);
        a.setFormateur(f);
        a.setStatut(Affectation.Statut.PROPOSEE);
        a.setCommentaire(commentaire);
        a.setCreatedAt(LocalDateTime.now());
        a.setHeuresPrevues(0);
        affectationRepo.save(a);
    }

    @Override
    @Transactional
    public void repondre(Integer affectationId, boolean accept) {
        Affectation a = affectationRepo.findById(affectationId)
                .orElseThrow(() -> new NotFoundException("Proposition introuvable: " + affectationId));

        if (!accept) {
            a.setStatut(Affectation.Statut.ANNULEE);
            return;
        }

        Session s = a.getSession();
        Integer fid = a.getFormateur().getIdFormateur();

        boolean conflict = affectationRepo.existsOverlap(fid, s.getDateDebut(), s.getDateFin());
        if (conflict) {
            a.setStatut(Affectation.Statut.ANNULEE);
            throw new ConflictException("Chevauchement détecté : affectation refusée.");
        }

        a.setStatut(Affectation.Statut.CONFIRMEE);

        // Annuler les autres PROPOSEES de la même session
        List<Affectation> others = affectationRepo.findBySession_IdSession(s.getIdSession());
        for (Affectation other : others) {
            if (!other.getIdAffectation().equals(a.getIdAffectation())
                    && other.getStatut() == Affectation.Statut.PROPOSEE) {
                other.setStatut(Affectation.Statut.ANNULEE);
            }
        }
    }

    @Override
    @Transactional
    public void assignerDirect(Integer sessionId, Integer formateurId) {
        if (sessionId == null || formateurId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Paramètres manquants.");
        }

        Session s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session introuvable: " + sessionId));
        Formateur f = formateurRepo.findById(formateurId)
                .orElseThrow(() -> new NotFoundException("Formateur introuvable: " + formateurId));

        if (s.getDateDebut() == null || s.getDateFin() == null) {
            throw new ConflictException("La session n'a pas de dates valides.");
        }
        if (s.getDateFin().isBefore(s.getDateDebut())) {
            throw new ConflictException("La date de fin est antérieure à la date de début.");
        }

        boolean conflict = affectationRepo.existsOverlap(f.getIdFormateur(), s.getDateDebut(), s.getDateFin());
        if (conflict) {
            throw new ConflictException("Affectation impossible : chevauchement détecté.");
        }

        // Confirme une existante ou crée une nouvelle
        Optional<Affectation> maybe = affectationRepo.findBySession_IdSession(s.getIdSession())
                .stream()
                .filter(a -> a.getFormateur().getIdFormateur().equals(f.getIdFormateur()))
                .findFirst();

        Affectation target = maybe.orElseGet(() -> {
            Affectation a = new Affectation();
            a.setSession(s);
            a.setFormateur(f);
            a.setCreatedAt(LocalDateTime.now());
            a.setHeuresPrevues(0);
            return a;
        });

        target.setStatut(Affectation.Statut.CONFIRMEE);
        affectationRepo.save(target);

        // Annule les autres propositions de cette session
        List<Affectation> others = affectationRepo.findBySession_IdSession(s.getIdSession());
        for (Affectation other : others) {
            if (!other.getIdAffectation().equals(target.getIdAffectation())
                    && other.getStatut() == Affectation.Statut.PROPOSEE) {
                other.setStatut(Affectation.Statut.ANNULEE);
            }
        }

        // Marque la session comme AFFECTEE
        s.setStatut(Session.Statut.AFFECTEE);
        sessionRepo.save(s);
    }

    // ----------
    // UTILITIES
    // ----------
    private static Object callIfExists(Object target, String getter) {
        if (target == null || getter == null) return null;
        try {
            var m = target.getClass().getMethod(getter);
            return m.invoke(target);
        } catch (Exception ignore) {
            return null;
        }
    }
}
