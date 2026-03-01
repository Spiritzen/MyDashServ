// src/main/java/com/afci/training/planning/service/impl/SessionServiceImpl.java
package com.afci.training.planning.service.impl;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Collections;   // ✅ bon import
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.afci.training.planning.dto.SessionFilter;
import com.afci.training.planning.dto.SessionListDTO;
import com.afci.training.planning.dto.SessionSaveDTO;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.repository.FormationRepository;
import com.afci.training.planning.repository.SessionListProjection;
import com.afci.training.planning.repository.SessionRepository;
import com.afci.training.planning.repository.spec.SessionSpecs;
import com.afci.training.planning.service.SessionService;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repo;
    private final FormationRepository formationRepo;

    public SessionServiceImpl(SessionRepository repo, FormationRepository formationRepo) {
        this.repo = repo;
        this.formationRepo = formationRepo;
    }

    @Override
    public Page<SessionListDTO> search(SessionFilter filter, Pageable pageable) {
        var spec = SessionSpecs.fromFilter(filter, formationRepo); // 👈 passe formationRepo
        var page = repo.findAll(spec, pageable);

        // IDs formation présents dans la page
        var ids = page.getContent().stream()
                .map(Session::getFormationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Chargement groupé des libellés des formations
        Map<Integer, String> intituleById = Collections.emptyMap();
        if (!ids.isEmpty()) {
            intituleById = formationRepo.findAllById(ids).stream().collect(Collectors.toMap(
                    f -> (Integer) callIfExists(f, "getIdFormation"),
                    f -> {
                        String lib = (String) callIfExists(f, "getIntitule");
                        Object id = callIfExists(f, "getIdFormation");
                        return lib != null ? lib : ("#" + id);
                    }
            ));
        }

        final Map<Integer, String> map = intituleById;
        return page.map(s -> toListDTO(s, map));
    }
    

    @Override
    public SessionListDTO findOne(Integer id) {
        var s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Session introuvable"));
        String label = null;
        Integer fid = s.getFormationId();
        if (fid != null) {
            label = formationRepo.findById(fid)
                    .map(f -> (String) callIfExists(f, "getIntitule"))
                    .orElse("#" + fid);
        }
        return toListDTO(s, label == null ? Map.of() : Map.of(fid, label));
    }

    @Override
    public SessionListDTO create(SessionSaveDTO dto) {
        var s = new Session();
        apply(s, dto);
        s = repo.save(s);
        String label = resolveFormationLabel(s.getFormationId());
        return toListDTO(s, label == null ? Map.of() : Map.of(s.getFormationId(), label));
    }

    @Override
    public SessionListDTO update(Integer id, SessionSaveDTO dto) {
        var s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Session introuvable"));
        apply(s, dto);
        s = repo.save(s);
        String label = resolveFormationLabel(s.getFormationId());
        return toListDTO(s, label == null ? Map.of() : Map.of(s.getFormationId(), label));
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Session introuvable");
        }
        repo.deleteById(id);
    }

    // ---- helpers

    private String resolveFormationLabel(Integer formationId) {
        if (formationId == null) return null;
        return formationRepo.findById(formationId)
                .map(f -> (String) callIfExists(f, "getIntitule"))
                .orElse("#" + formationId);
    }

    private SessionListDTO toListDTO(Session s, Map<Integer, String> intituleById) {
        String formationLabel = null;
        if (s.getFormationId() != null) {
            formationLabel = intituleById.getOrDefault(s.getFormationId(), "#" + s.getFormationId());
        }
        String formateurLabel = resolveAssignedTrainerLabelOrDash(s.getIdSession());

        return new SessionListDTO(
                s.getIdSession(),
                s.getDateDebut(),
                s.getDateFin(),
                formationLabel,
                formateurLabel,
                s.getMode(),
                s.getStatut(),
                s.getVille(),
                s.getSalle()
        );
    }

    private static Object callIfExists(Object target, String getter) {
        try {
            var m = target.getClass().getMethod(getter);
            return m.invoke(target);
        } catch (Exception ignore) { return null; }
    }

    private String resolveAssignedTrainerLabelOrDash(Integer sessionId) {
        try {
            SessionListProjection row = repo.findOneWithAssignedTrainer(sessionId);
            if (row == null) return "—";
            String nom = row.getFormateur_nom();
            String prenom = row.getFormateur_prenom();
            if ((nom == null || nom.isBlank()) && (prenom == null || prenom.isBlank())) return "—";
            if (prenom == null || prenom.isBlank()) return nom;
            if (nom == null || nom.isBlank()) return prenom;
            return prenom + " " + nom;
        } catch (Exception ex) {
            return "—";
        }
    }

    private void apply(Session s, SessionSaveDTO dto) {
        s.setFormationId(dto.getFormationId());
        s.setDateDebut(dto.getDateDebut());
        s.setDateFin(dto.getDateFin());
        s.setMode(dto.getMode());
        s.setVille(emptyToNull(dto.getVille()));
        s.setSalle(emptyToNull(dto.getSalle()));

        // Défaut statut
        if (s.getStatut() == null) {
            s.setStatut(Session.Statut.PLANIFIEE);
        }

        // Compléter depuis la formation si manquants
        Integer fid = s.getFormationId();
        if (fid != null) {
            formationRepo.findById(fid).ifPresent(f -> {
                if (s.getMode() == null) {
                    String md = (String) callIfExists(f, "getModeDefaut");
                    if (md == null) md = (String) callIfExists(f, "getMode");
                    if (md != null && !md.isBlank()) {
                        try { s.setMode(Session.Mode.valueOf(md)); } catch (Exception ignore) {}
                    }
                }
                if (s.getVille() == null || s.getVille().isBlank()) {
                    String v = (String) callIfExists(f, "getVilleDefaut");
                    if (v == null) v = (String) callIfExists(f, "getVille");
                    if (v != null && !v.isBlank()) s.setVille(v);
                }
                if (s.getSalle() == null || s.getSalle().isBlank()) {
                    String sa = (String) callIfExists(f, "getSalleDefaut");
                    if (sa == null) sa = (String) callIfExists(f, "getSalle");
                    if (sa != null && !sa.isBlank()) s.setSalle(sa);
                }
            });
        }
    }

    private static String emptyToNull(String v) {
        return (v == null || v.isBlank()) ? null : v;
    }
}
