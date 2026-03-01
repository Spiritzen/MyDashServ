// src/main/java/com/afci/training/planning/service/AdminFormateurService.java
package com.afci.training.planning.service;

import com.afci.training.planning.dto.admin.AdminCompetenceItemDTO;
import com.afci.training.planning.dto.admin.AdminEvidenceDocDTO;
import com.afci.training.planning.dto.admin.AdminFormateurDetailDTO;
import com.afci.training.planning.dto.admin.AdminFormateurListItemDTO;
import com.afci.training.planning.entity.Competence;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.entity.FormateurComp;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.EvidenceDocRepository;
import com.afci.training.planning.repository.FormateurCompRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminFormateurService {

    private final UtilisateurRepository utilisateurRepo;
    private final FormateurCompRepository formateurCompRepo;
    private final EvidenceDocRepository evidenceRepo;

    public AdminFormateurService(UtilisateurRepository utilisateurRepo,
                                 FormateurCompRepository formateurCompRepo,
                                 EvidenceDocRepository evidenceRepo) {
        this.utilisateurRepo = utilisateurRepo;
        this.formateurCompRepo = formateurCompRepo;
        this.evidenceRepo = evidenceRepo;
    }

    /** Pastille rouge (sidebar) : nombre de candidatures en attente */
    public long countPending() {
        return utilisateurRepo.countPendingCandidatures();
    }

    /** Liste “condensée” selon statut (EN_ATTENTE | VALIDE) */
    public List<AdminFormateurListItemDTO> listByStatut(String statut) {
        List<Utilisateur> users = utilisateurRepo.findByStatutCandidature(statut);
        List<AdminFormateurListItemDTO> out = new ArrayList<>(users.size());
        for (Utilisateur u : users) {
            Integer formateurId = (u.getFormateur() != null ? u.getFormateur().getIdFormateur() : null);
            int nbComp = (formateurId != null) ? formateurCompRepo.countByFormateurId(formateurId) : 0;

            out.add(new AdminFormateurListItemDTO(
                u.getIdUtilisateur(),
                u.getEmail(),
                u.getNom(),
                u.getPrenom(),
                u.isEmailVerifie(),
                u.isCompteValide(),
                (u.getPhotoPath() != null && !u.getPhotoPath().isBlank()) ? "/files/" + u.getPhotoPath() : null,
                (u.getFormateur() != null ? u.getFormateur().getProfilSoumisLe() : null),
                nbComp
            ));
        }
        return out;
    }

    /** Détail complet pour la modale (profil + compétences + preuves) */
    public AdminFormateurDetailDTO getDetail(Integer userId) {
        Utilisateur u = utilisateurRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + userId + " introuvable"));

        Formateur f = u.getFormateur();
        List<AdminCompetenceItemDTO> competences = new ArrayList<>();

        if (f != null) {
            List<FormateurComp> fcs = formateurCompRepo.findByFormateur_IdFormateur(f.getIdFormateur());

            for (FormateurComp fc : fcs) {
                Competence c = fc.getCompetence();
                Integer idComp = (c != null ? c.getIdCompetence() : null);
                String label = (c != null ? c.getLabel() : "—");

                // Preuves (docs) attachées
                var docs = new ArrayList<AdminEvidenceDocDTO>();
                if (idComp != null) {
                	evidenceRepo.findByFormateurComp_Formateur_IdFormateurAndFormateurComp_Competence_IdCompetence(
                		    f.getIdFormateur(), idComp
                		)
                        .forEach(ev -> {
                            String url = "/api/admin/evidences/" + ev.getIdDoc() + "/download";
                            Instant uploadedAt = ev.getUploadedAt() != null
                                ? ev.getUploadedAt().atZone(ZoneId.systemDefault()).toInstant()
                                : null;
                            docs.add(new AdminEvidenceDocDTO(
                                ev.getIdDoc(),
                                ev.getFilename(),
                                ev.getMimeType(),
                                ev.getSizeBytes(),
                                uploadedAt,
                                url
                            ));
                        });
                }

                // visible → boolean primitif => isVisible()
                boolean visible = false;
                try {
                    visible = fc.isVisible();
                } catch (NoSuchMethodError ignored) {
                    // si jamais c'était un Boolean getVisible(), tu peux fallback ici :
                    // visible = Boolean.TRUE.equals(fc.getVisible());
                }

                competences.add(new AdminCompetenceItemDTO(
                    idComp,
                    label,
                    fc.getNiveau() == null ? null : fc.getNiveau().intValue(),
                    fc.getTitle(),
                    fc.getDescription(),
                    fc.getExperienceYears() == null ? null : fc.getExperienceYears().intValue(),
                    fc.getLastUsed(),
                    fc.getStatus() != null ? fc.getStatus().name() : null,
                    visible,
                    docs
                ));
            }
        }

        String photoUrl = (u.getPhotoPath() != null && !u.getPhotoPath().isBlank())
                ? "/files/" + u.getPhotoPath() : null;

        return new AdminFormateurDetailDTO(
            u.getIdUtilisateur(),
            u.getEmail(),
            u.getNom(),
            u.getPrenom(),
            u.getAdresse(),
            u.getVille(),
            u.getCodePostal(),
            u.getTelephone(),
            u.isEmailVerifie(),
            u.isCompteValide(),
            photoUrl,
            (f != null ? f.getProfilSoumisLe() : null),
            // ⚠️ Ici on passe bien List<AdminCompetenceItemDTO>
            competences
        );
    }

    // ===== Validation / Invalidation d’un compte formateur =====
    @Transactional
    public void valider(Integer userId, boolean value, String comment, Utilisateur validatedBy) {
        Utilisateur u = utilisateurRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + userId + " introuvable"));
        Formateur f = u.getFormateur();

        if (value) {
            u.setCompteValide(true);
            u.setValidatedAt(Instant.now());
            u.setValidatedBy(validatedBy);
            if (f != null) f.setActif(Boolean.TRUE);
        } else {
            u.setCompteValide(false);
            if (f != null) {
                f.setProfilSoumisLe(null);   // retour à l’état brouillon
                f.setActif(Boolean.FALSE);
            }
        }
        // Dirty checking JPA
    }
}
