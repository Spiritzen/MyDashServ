package com.afci.training.planning.controller;

import com.afci.training.planning.dto.DocumentDTO;
import com.afci.training.planning.entity.*;
import com.afci.training.planning.repository.EvidenceDocRepository;
import com.afci.training.planning.repository.FormateurCompRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.service.StorageService;
import com.afci.training.planning.util.DocumentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/me/competences")
public class MeCompetenceDocsController {

    private final FormateurCompRepository fcRepo;
    private final EvidenceDocRepository docRepo;
    private final UtilisateurRepository userRepo;
    private final StorageService storage;

    public MeCompetenceDocsController(FormateurCompRepository fcRepo,
                                      EvidenceDocRepository docRepo,
                                      UtilisateurRepository userRepo,
                                      StorageService storage) {
        this.fcRepo = fcRepo;
        this.docRepo = docRepo;
        this.userRepo = userRepo;
        this.storage = storage;
    }

    /** Récupère l'id_formateur du user connecté via son email (auth.getName()). */
    private Integer currentFormateurId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }
        String email = auth.getName(); // -> ton JwtFilter doit mettre l'email ici
        Utilisateur u = userRepo.findByEmailIgnoreCase(email)
                .orElseGet(() -> userRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Utilisateur introuvable: " + email)));
        if (u.getFormateur() == null || u.getFormateur().getIdFormateur() == null) {
            throw new AccessDeniedException("Aucun profil formateur lié au compte");
        }
        return u.getFormateur().getIdFormateur();
    }

    @GetMapping("/{competenceId}/docs")
    public List<DocumentDTO> listDocs(@PathVariable Integer competenceId) {
        Integer fId = currentFormateurId();
        return docRepo
            .findByFormateurComp_Formateur_IdFormateurAndFormateurComp_Competence_IdCompetence(fId, competenceId)
            .stream()
            .map(DocumentMapper::toDTO)
            .toList();
    }

    @PostMapping(path = "/{competenceId}/docs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public List<DocumentDTO> uploadDocs(@PathVariable Integer competenceId,
                                        @RequestPart("files") List<MultipartFile> files) throws Exception {
        Integer fId = currentFormateurId();
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni (champ multipart 'files').");
        }

        FormateurCompId id = new FormateurCompId(fId, competenceId);
        FormateurComp fc = fcRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compétence non déclarée pour ce formateur"));

        var out = new java.util.ArrayList<DocumentDTO>(files.size());
        for (MultipartFile f : files) {
            var s = storage.saveEvidence(fId, competenceId, f);
            EvidenceDoc doc = new EvidenceDoc(fc, s.filename(), s.mimeType(), s.size(), s.absPath(), s.sha256());
            out.add(DocumentMapper.toDTO(docRepo.save(doc)));
        }
        return out;
    }

    @DeleteMapping("/{competenceId}/docs/{docId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteDoc(@PathVariable Integer competenceId, @PathVariable Integer docId) {
        Integer fId = currentFormateurId();
        EvidenceDoc doc = docRepo.findById(docId).orElseThrow();
        // garde-fou : le doc doit appartenir au formateur + compétence
        if (!doc.getFormateurComp().getFormateur().getIdFormateur().equals(fId) ||
            !doc.getFormateurComp().getCompetence().getIdCompetence().equals(competenceId)) {
            throw new AccessDeniedException("Not your document");
        }
        storage.delete(doc.getStoragePath()); // supprime sur disque
        docRepo.delete(doc);                   // supprime en BDD
    }
}
