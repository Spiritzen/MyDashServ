package com.afci.training.planning.controller.admin;

import com.afci.training.planning.dto.admin.AdminFormateurDetailDTO;
import com.afci.training.planning.dto.admin.AdminFormateurListItemDTO;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.service.AdminFormateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/formateurs")
@PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
public class AdminFormateurController {

    private final AdminFormateurService service;
    private final UtilisateurRepository utilisateurRepo;

    public AdminFormateurController(AdminFormateurService service,
                                    UtilisateurRepository utilisateurRepo) {
        this.service = service;
        this.utilisateurRepo = utilisateurRepo;
    }

    @GetMapping("/count-pending")
    public ResponseEntity<Long> countPending() {
        return ResponseEntity.ok(service.countPending());
    }

    @GetMapping
    public ResponseEntity<List<AdminFormateurListItemDTO>> list(@RequestParam("statut") String statut) {
        return ResponseEntity.ok(service.listByStatut(statut));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminFormateurDetailDTO> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getDetail(id));
    }

    @PatchMapping("/{id}/valider")
    public ResponseEntity<Void> valider(@PathVariable Integer id,
                                        @RequestParam("value") boolean value,
                                        @RequestParam(value = "comment", required = false) String comment,
                                        Authentication auth) {
        String email = (auth != null ? auth.getName() : null);
        Utilisateur me = (email != null)
                ? utilisateurRepo.findByEmailIgnoreCase(email).orElse(null)
                : null;

        service.valider(id, value, comment, me);
        return ResponseEntity.noContent().build();
    }
}
