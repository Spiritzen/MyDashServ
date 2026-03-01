package com.afci.training.planning.service.impl;

import com.afci.training.planning.entity.Affectation;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.exception.NotFoundException;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.repository.projection.FormateurAffectationProjection;
import com.afci.training.planning.service.FormateurAffectationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class FormateurAffectationServiceImpl implements FormateurAffectationService {

    private final AffectationRepository affectationRepo;
    private final UtilisateurRepository utilisateurRepo;

    public FormateurAffectationServiceImpl(AffectationRepository affectationRepo,
                                           UtilisateurRepository utilisateurRepo) {
        this.affectationRepo = affectationRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    private Integer currentFormateurId(String email) {
        return utilisateurRepo.findByEmail(email)
                .map(u -> u.getFormateur() != null ? u.getFormateur().getIdFormateur() : null)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormateurAffectationProjection> listForCurrentFormateur(String email, String statut, Pageable pageable) {
        Integer fid = currentFormateurId(email);
        // ici "statut" = statut de SESSION (EN_COURS / PLANIFIEE / ANNULEE / ...)
        return affectationRepo.findFormateurAffectations(fid, statut, pageable);
    }

    @Override
    @Transactional
    public void accept(String email, Integer affectationId) {
        Integer fid = currentFormateurId(email);

        Affectation a = affectationRepo.findById(affectationId)
                .orElseThrow(() -> new NotFoundException("Affectation introuvable: " + affectationId));

        if (a.getFormateur() == null || !a.getFormateur().getIdFormateur().equals(fid)) {
            throw new NotFoundException("Affectation non accessible.");
        }

        Session s = a.getSession();
        if (s == null) throw new NotFoundException("Session introuvable.");

        // ✅ Le formateur ne peut accepter/refuser que si la SESSION est EN_COURS
        if (s.getStatut() != Session.Statut.EN_COURS) {
            throw new ResponseStatusException(BAD_REQUEST, "Action possible uniquement si la session est EN_COURS.");
        }

        // Affectation (on garde cohérent, mais ton UI se base sur session)
        a.setStatut(Affectation.Statut.CONFIRMEE);

        // ✅ Workflow demandé : acceptation => session PLANIFIEE
        s.setStatut(Session.Statut.PLANIFIEE);
    }

    @Override
    @Transactional
    public void refuse(String email, Integer affectationId) {
        Integer fid = currentFormateurId(email);

        Affectation a = affectationRepo.findById(affectationId)
                .orElseThrow(() -> new NotFoundException("Affectation introuvable: " + affectationId));

        if (a.getFormateur() == null || !a.getFormateur().getIdFormateur().equals(fid)) {
            throw new NotFoundException("Affectation non accessible.");
        }

        Session s = a.getSession();
        if (s == null) throw new NotFoundException("Session introuvable.");

        if (s.getStatut() != Session.Statut.EN_COURS) {
            throw new ResponseStatusException(BAD_REQUEST, "Action possible uniquement si la session est EN_COURS.");
        }

        a.setStatut(Affectation.Statut.ANNULEE);

        // ✅ Workflow demandé : refus => session ANNULEE
        s.setStatut(Session.Statut.ANNULEE);
    }
}
