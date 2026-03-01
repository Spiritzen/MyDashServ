// src/main/java/com/afci/training/planning/service/CandidatureService.java
package com.afci.training.planning.service;

import java.time.Instant; // ✅ on utilise Instant (plus LocalDateTime)

import org.springframework.stereotype.Service;

import com.afci.training.planning.dto.CandidatureStatusDTO;
import com.afci.training.planning.dto.CandidatureSubmitDTO;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.enums.StatutCandidature;
import com.afci.training.planning.repository.FormateurCompRepository;
import com.afci.training.planning.repository.FormateurRepository;
import com.afci.training.planning.repository.UtilisateurRepository;

import jakarta.transaction.Transactional;

@Service
public class CandidatureService {

    private final UtilisateurRepository utilisateurRepo;
    private final FormateurRepository formateurRepo;
    private final FormateurCompRepository formateurCompRepo;

    public CandidatureService(UtilisateurRepository utilisateurRepo,
                              FormateurRepository formateurRepo,
                              FormateurCompRepository formateurCompRepo) {
        this.utilisateurRepo = utilisateurRepo;
        this.formateurRepo = formateurRepo;
        this.formateurCompRepo = formateurCompRepo;
    }

    public CandidatureStatusDTO getStatus(Integer userId) {
        Utilisateur u = utilisateurRepo.getReferenceById(userId);
        Formateur f = (u.getFormateur() != null) ? u.getFormateur() : null;

        boolean identiteOK =
            notBlank(u.getNom()) && notBlank(u.getPrenom()) &&
            notBlank(u.getEmail()) &&
            notBlank(u.getAdresse()) && notBlank(u.getVille()) && notBlank(u.getCodePostal()) &&
            notBlank(u.getTelephone());

        int nbComp = (f == null) ? 0 : formateurCompRepo.countByFormateurId(f.getIdFormateur());
        boolean profilComplet = identiteOK && nbComp >= 1;

        // ✅ Si tes champs sont des boolean primitifs, les getters sont "isXxx()"
        boolean compteValide  = u.isCompteValide();   // <-- remplace getCompteValide()
        boolean emailVerifie  = u.isEmailVerifie();   // <-- remplace getEmailVerifie()

        StatutCandidature statut;
        String msg;

        if (!profilComplet) {
            statut = StatutCandidature.NON_ELIGIBLE;
            msg = "Renseignez identité/coordonnées et ajoutez au moins une compétence.";
        } else if (compteValide) {
            statut = StatutCandidature.VALIDE;
            msg = "Votre compte a été validé.";
        } else if (f != null && f.getProfilSoumisLe() != null) {
            statut = StatutCandidature.EN_ATTENTE;
            msg = "Candidature soumise. En attente de validation.";
        } else {
            statut = StatutCandidature.NON_ELIGIBLE; // profil complet mais non soumis
            msg = "Profil complet : vous pouvez soumettre votre candidature.";
        }

        return new CandidatureStatusDTO(
            statut,
            profilComplet,
            emailVerifie,
            compteValide,
            nbComp,
            msg
        );
    }

    @Transactional
    public CandidatureStatusDTO soumettre(Integer userId, CandidatureSubmitDTO payload) {
        Utilisateur u = utilisateurRepo.getReferenceById(userId);

        // met à jour les infos si fournies
        if (notBlank(payload.getNom())) u.setNom(payload.getNom());
        if (notBlank(payload.getPrenom())) u.setPrenom(payload.getPrenom());
        if (notBlank(payload.getTelephone())) u.setTelephone(payload.getTelephone());
        if (notBlank(payload.getAdresse())) u.setAdresse(payload.getAdresse());
        if (notBlank(payload.getVille())) u.setVille(payload.getVille());
        if (notBlank(payload.getCodePostal())) u.setCodePostal(payload.getCodePostal());

        Formateur f = u.getFormateur();
        if (f == null) throw new IllegalStateException("Aucun profil formateur rattaché.");

        int nbComp = formateurCompRepo.countByFormateurId(f.getIdFormateur());

        boolean identiteOK =
            notBlank(u.getNom()) && notBlank(u.getPrenom()) &&
            notBlank(u.getEmail()) && notBlank(u.getAdresse()) &&
            notBlank(u.getVille()) && notBlank(u.getCodePostal()) &&
            notBlank(u.getTelephone());

        if (!(identiteOK && nbComp >= 1)) {
            throw new IllegalArgumentException("Profil incomplet ou aucune compétence.");
        }

        // ✅ trace la soumission avec Instant (et pas LocalDateTime)
        f.setProfilComplet(true);
        f.setProfilSoumisLe(Instant.now());
        formateurRepo.save(f);

        return getStatus(userId);
    }

    @Transactional
    public CandidatureStatusDTO annulerSoumission(Integer userId) {
        Utilisateur u = utilisateurRepo.getReferenceById(userId);

        // ✅ boolean primitif -> "isCompteValide()"
        if (u.isCompteValide()) {
            throw new IllegalStateException("Compte déjà validé, impossible d'annuler.");
        }
        Formateur f = u.getFormateur();
        if (f == null) throw new IllegalStateException("Aucun profil formateur rattaché.");

        f.setProfilSoumisLe(null);
        formateurRepo.save(f);
        return getStatus(userId);
    }

    // ===== Garde-fou réutilisable (ex: avant création d'une affectation) =====
    public void assertFormateurValide(Utilisateur u) {
        // ✅ boolean primitif -> "isCompteValide()"
        if (!u.isCompteValide()) {
            throw new IllegalStateException("Le compte formateur n'est pas validé.");
        }
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }
}
