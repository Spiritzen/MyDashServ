// src/main/java/com/afci/training/planning/service/MatchingService.java
package com.afci.training.planning.service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.training.planning.dto.CandidateDTO;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.entity.FormateurComp;
import com.afci.training.planning.entity.Formation;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.repository.*;

@Service
@Transactional(readOnly = true)
public class MatchingService {

    private static final Logger log = LoggerFactory.getLogger(MatchingService.class);

    private final SessionRepository sessionRepo;
    private final FormationRepository formationRepo;
    private final FormateurRepository formateurRepo;
    private final FormateurCompRepository formateurCompRepository;
    private final DisponibiliteService disponibiliteService;
    private final AffectationRepository affectationRepo;
    private final UtilisateurRepository utilisateurRepo; // laissé si utilisé ailleurs
    private final CongeServiceWrapper congeService;

    public MatchingService(SessionRepository sessionRepo,
                           FormationRepository formationRepo,
                           FormateurRepository formateurRepo,
                           FormateurCompRepository formateurCompRepository,
                           DisponibiliteService disponibiliteService,
                           AffectationRepository affectationRepo,
                           UtilisateurRepository utilisateurRepo,
                           CongeServiceWrapper congeService) {
        this.sessionRepo = sessionRepo;
        this.formationRepo = formationRepo;
        this.formateurRepo = formateurRepo;
        this.formateurCompRepository = formateurCompRepository;
        this.disponibiliteService = disponibiliteService;
        this.affectationRepo = affectationRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.congeService = congeService;
    }

    public List<CandidateDTO> findCandidates(Integer sessionId, int limit) {
        final Session s = sessionRepo.getReferenceById(sessionId);
        final LocalDateTime start = s.getDateDebut();
        final LocalDateTime end   = s.getDateFin();

        final Set<String> required = requiredLabelsFromFormationTitle(s);

        final List<Formateur> pool = formateurRepo.findByActifTrue();
        final List<CandidateDTO> out = new ArrayList<>();
        if (pool.isEmpty()) return out;

        // --------- MAP fid -> (nom, prenom, email) via LEFT JOIN natif ---------
        List<Integer> ids = pool.stream().map(Formateur::getIdFormateur).toList();
        Map<Integer, String[]> userByFid = new HashMap<>(ids.size());
        try {
            for (Object[] row : formateurRepo.fetchUserFieldsForFormateurIds(ids)) {
                Integer fid = row[0] != null ? ((Number) row[0]).intValue() : null;
                String nom    = row[1] != null ? row[1].toString() : null;
                String prenom = row[2] != null ? row[2].toString() : null;
                String email  = row[3] != null ? row[3].toString() : null;
                if (fid != null) userByFid.put(fid, new String[]{nom, prenom, email});
            }
        } catch (Exception e) {
            log.error("fetchUserFieldsForFormateurIds failed", e);
        }

        for (Formateur f : pool) {
            // === Récup champs utilisateur depuis la MAP ===
            String[] up = userByFid.get(f.getIdFormateur());
            String nom    = (up != null && up[0] != null && !up[0].isBlank()) ? up[0] : ("#" + f.getIdFormateur());
            String prenom = (up != null && up[1] != null) ? up[1] : "";
            String email  = (up != null && up[2] != null) ? up[2] : "";

            // 4) Compétences visibles & non rejetées
            List<FormateurComp> comps = formateurCompRepository.findAllByFormateurId(f.getIdFormateur())
                    .stream()
                    .filter(fc -> fc.isVisible() && fc.getStatus() != FormateurComp.CompetenceStatus.REJECTED)
                    .toList();

            Set<String> labelsFormateur = comps.stream()
                    .map(fc -> normLower(fc.getCompetence().getLabel()))
                    .filter(sv -> !sv.isBlank())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // 5) Matching souple
            Set<String> ok = new LinkedHashSet<>();
            for (String req : required) {
                if (labelsFormateur.stream().anyMatch(l -> l.contains(req) || req.contains(l))) ok.add(req);
            }
            List<String> ko = required.stream().filter(r -> !ok.contains(r)).toList();

            // 6) Score
            int score = ok.size() * 100 - ko.size() * 25;

            // 7) Dispo / congés / conflits
            boolean enConge = congeService.existsOverlap(f.getIdFormateur(), start, end);
            boolean noAffectConflict = !affectationRepo.existsOverlap(f.getIdFormateur(), start, end);
            boolean dispoPatternOK = disponibiliteService.matchesWeeklyPattern(f.getIdFormateur(), start, end);
            boolean dispoOK = !enConge && noAffectConflict && dispoPatternOK;
            long nbConflits = affectationRepo.countConflicts(f.getIdFormateur(), start, end);

            // 8) DTO
            CandidateDTO dto = new CandidateDTO();
            dto.setFormateurId(f.getIdFormateur());
            dto.setNom(nom);
            dto.setPrenom(prenom);
            dto.setEmail(email);
            dto.setSkillsOK(new ArrayList<>(ok));
            dto.setSkillsKO(new ArrayList<>(ko));
            dto.setDispoOK(dispoOK);
            dto.setEnConge(enConge);
            dto.setNbConflits((int) nbConflits);
            dto.setScore(score);
            dto.setCommentaires(buildCommentaires(required, ok, enConge, noAffectConflict, dispoPatternOK));

            out.add(dto);
        }

        out.sort(Comparator.comparingInt(CandidateDTO::getScore).reversed());
        return out.size() > limit ? out.subList(0, limit) : out;
    }

    private String buildCommentaires(Set<String> required, Set<String> ok,
                                     boolean enConge, boolean noAffectConflict, boolean dispoPatternOK) {
        List<String> raisons = new ArrayList<>();
        if (!required.isEmpty() && ok.isEmpty()) raisons.add("Aucune compétence requise couverte");
        if (enConge) raisons.add("En congé sur la période");
        if (!noAffectConflict) raisons.add("Conflit avec une affectation existante");
        if (!dispoPatternOK) raisons.add("Hors créneaux de disponibilité hebdo");
        return raisons.isEmpty() ? null : String.join(" ; ", raisons);
    }

    private Set<String> requiredLabelsFromFormationTitle(Session s) {
        Set<String> set = new LinkedHashSet<>();
        if (s.getFormationId() != null) {
            formationRepo.findById(s.getFormationId())
                .map(Formation::getIntitule)
                .map(this::normLower)
                .ifPresent(norm -> {
                    for (String tk : norm.split("[^a-z0-9]+")) if (tk.length() >= 2) set.add(tk);
                });
        }
        return set;
    }

    private String normLower(String s) {
        if (s == null) return "";
        String noAccent = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return noAccent.toLowerCase(Locale.ROOT).trim();
    }

    @Service
    public static class CongeServiceWrapper {
        private final com.afci.training.planning.repository.CongeRepository congeRepo;
        public CongeServiceWrapper(com.afci.training.planning.repository.CongeRepository congeRepo) { this.congeRepo = congeRepo; }
        public boolean existsOverlap(Integer fid, LocalDateTime start, LocalDateTime end) { return congeRepo.existsOverlap(fid, start, end); }
    }
}
