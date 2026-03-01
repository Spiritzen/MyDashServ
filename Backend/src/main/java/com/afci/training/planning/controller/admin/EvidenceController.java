// src/main/java/com/afci/training/planning/controller/admin/EvidenceController.java
package com.afci.training.planning.controller.admin;

import com.afci.training.planning.dto.admin.AdminEvidenceDocDTO;
import com.afci.training.planning.entity.EvidenceDoc;
import com.afci.training.planning.repository.EvidenceDocRepository;
import com.afci.training.planning.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/admin/evidences")
@PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')") // 👈 autorise les 2 rôles ici
public class EvidenceController {

    private final EvidenceDocRepository repo;
    private final StorageService storage;

    public EvidenceController(EvidenceDocRepository repo, StorageService storage) {
        this.repo = repo;
        this.storage = storage;
    }

    // ===== Helpers =====
    private static HttpHeaders buildDownloadHeaders(EvidenceDoc ev, boolean asAttachment) {
        String filename = ev.getFilename();
        if (!StringUtils.hasText(filename)) filename = "document";
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        String cd = (asAttachment ? "attachment" : "inline")
                + "; filename=\"" + filename.replace("\"", "") + "\""
                + "; filename*=UTF-8''" + encoded;

        HttpHeaders h = new HttpHeaders();
        h.set(HttpHeaders.CONTENT_DISPOSITION, cd);
        if (StringUtils.hasText(ev.getSha256())) {
            h.setETag("\"" + ev.getSha256() + "\"");
        }
        h.setCacheControl(CacheControl.noCache().getHeaderValue());
        return h;
    }

    private static MediaType safeMediaType(String mime) {
        try { return StringUtils.hasText(mime) ? MediaType.parseMediaType(mime) : MediaType.APPLICATION_OCTET_STREAM; }
        catch (Exception e) { return MediaType.APPLICATION_OCTET_STREAM; }
    }

    private EvidenceDoc getOr404(Integer id) {
        return repo.findById(id).orElseThrow(() ->
            new EntityNotFoundException("Evidence #" + id + " introuvable"));
    }

    private ResponseEntity<FileSystemResource> serve(EvidenceDoc ev, boolean asAttachment) {
        File f = new File(ev.getStoragePath());
        if (!f.exists()) return ResponseEntity.notFound().build();
        HttpHeaders h = buildDownloadHeaders(ev, asAttachment);
        return ResponseEntity.ok()
                .headers(h)
                .contentType(safeMediaType(ev.getMimeType()))
                .contentLength(f.length())
                .body(new FileSystemResource(f));
    }

    // ===== Métadonnées d'un document =====
    @GetMapping("/{id}")
    public AdminEvidenceDocDTO meta(@PathVariable Integer id) {
        EvidenceDoc ev = getOr404(id);
        return new AdminEvidenceDocDTO(
                ev.getIdDoc(),
                ev.getFilename(),
                ev.getMimeType(),
                ev.getSizeBytes(),
                ev.getUploadedAt() != null ? ev.getUploadedAt().atZone(ZoneId.systemDefault()).toInstant() : null,
                // lien de download:
                "/api/admin/evidences/" + ev.getIdDoc() + "/download"
        );
    }

 // ===== Liste des docs pour un couple (formateur, compétence) =====
    @GetMapping("/formateurs/{formateurId}/competences/{competenceId}")
    public List<AdminEvidenceDocDTO> listByFormateurAndCompetence(@PathVariable Integer formateurId,
                                                                  @PathVariable Integer competenceId) {
        return repo.findByFormateurComp_Formateur_IdFormateurAndFormateurComp_Competence_IdCompetence(formateurId, competenceId)
                .stream()
                .map(ev -> new AdminEvidenceDocDTO(
                        ev.getIdDoc(),
                        ev.getFilename(),
                        ev.getMimeType(),
                        ev.getSizeBytes(),
                        ev.getUploadedAt() != null ? ev.getUploadedAt().atZone(ZoneId.systemDefault()).toInstant() : null,
                        "/api/admin/evidences/" + ev.getIdDoc() + "/download"
                ))
                .toList();
    }

    // ===== Affichage inline =====
    @GetMapping("/{id}/view")
    public ResponseEntity<FileSystemResource> view(@PathVariable Integer id) {
        EvidenceDoc ev = getOr404(id);
        return serve(ev, false);
    }

    // ===== Téléchargement (sans contexte) =====
    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> download(@PathVariable Integer id) {
        EvidenceDoc ev = getOr404(id);
        return serve(ev, true);
    }

 // ===== Téléchargement “contextuel” (vérifie l’appartenance au formateur) =====
    @GetMapping("/formateurs/{formateurId}/docs/{docId}/download")
    public ResponseEntity<FileSystemResource> downloadInContext(@PathVariable Integer formateurId,
                                                                @PathVariable Integer docId) {
        EvidenceDoc ev = repo.findByIdDocAndFormateurComp_Formateur_IdFormateur(docId, formateurId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Document introuvable"));
        return serve(ev, true);
    }

    // ===== Suppression (si autorisée) =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        EvidenceDoc ev = getOr404(id);
        storage.delete(ev.getStoragePath());
        repo.delete(ev);
        return ResponseEntity.noContent().build();
    }
}
