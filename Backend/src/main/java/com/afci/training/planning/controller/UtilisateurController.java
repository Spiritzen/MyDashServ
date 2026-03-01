package com.afci.training.planning.controller;

import java.net.URI;
import java.util.Optional;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.afci.training.planning.dto.UtilisateurCreateDTO;
import com.afci.training.planning.dto.UtilisateurDTO;
import com.afci.training.planning.dto.UtilisateurUpdateDTO;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.UtilisateurRepository;
import com.afci.training.planning.service.UtilisateurService;

/**
 * API Utilisateurs
 * Base path : /api/users
 *
 * Notes :
 * - Les fichiers (photos) sont servis publiquement via /files/** (configuré par WebMvcConfig).
 * - Le DTO renvoie aussi photoUrl pour affichage direct côté front.
 */
@RestController
@RequestMapping("/api/users")
public class UtilisateurController {

    private final UtilisateurService service;
    private final UtilisateurRepository repo;

    public UtilisateurController(UtilisateurService service, UtilisateurRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    /* ===================== CREATE ===================== */

    /**
     * Créer un utilisateur (inscription publique).
     * Le service force le rôle à FORMATEUR et crée le profil Formateur lié.
     *
     * @param dto Données d'inscription (email, password, identité/contact)
     * @return UtilisateurDTO créé (Location: /api/users/{id})
     */
    @PostMapping
    public ResponseEntity<UtilisateurDTO> create(@Valid @RequestBody UtilisateurCreateDTO dto){
        UtilisateurDTO created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.getIdUtilisateur()))
                .body(created);
    }

    /* ===================== SELF (ME) ===================== */

    /**
     * Récupérer le profil de l'utilisateur courant.
     *
     * @param auth Authentication (email = auth.getName())
     * @return Profil courant (DTO)
     */
    @GetMapping("/me")
    public UtilisateurDTO me(Authentication auth) {
        return service.findByEmail(auth.getName());
    }

    /**
     * Mettre à jour le profil courant (identité/contact uniquement).
     *
     * @param auth Authentication (email = auth.getName())
     * @param dto  Champs à mettre à jour (partiel)
     * @return DTO mis à jour
     */
    @PutMapping("/me")
    public UtilisateurDTO updateMe(Authentication auth, @RequestBody UtilisateurUpdateDTO dto) {
        return service.updateByEmail(auth.getName(), dto);
    }

    /* ====== PHOTO (SELF) ====== */

    /**
     * Téléverser/remplacer la photo de l'utilisateur courant (multipart).
     * Retourne l'utilisateur à jour (avec photoUrl).
     *
     * @param auth Authentication (email = auth.getName())
     * @param file Fichier image (jpeg | png | webp)
     * @return DTO mis à jour (photoUrl)
     */
    @PostMapping(path = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UtilisateurDTO uploadMyPhoto(Authentication auth,
                                        @RequestParam("file") MultipartFile file) {
        try {
            // Option 1 : un service dédié (recommandé)
            // return service.uploadPhotoByEmail(auth.getName(), file);

            // Option 2 : réutiliser uploadPhoto() via l’ID trouvé par email
            Utilisateur u = repo.findByEmail(auth.getName())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur courant introuvable"));
            return service.uploadPhoto(u.getIdUtilisateur(), file);
        } catch (Exception ex) {
            throw new RuntimeException("Upload image (me) échoué : " + ex.getMessage(), ex);
        }
    }

    /**
     * Supprimer la photo de l'utilisateur courant.
     *
     * @param auth Authentication (email = auth.getName())
     */
    @DeleteMapping("/me/photo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyPhoto(Authentication auth) {
        // Option 1 : un service dédié (recommandé)
        // service.deletePhotoByEmail(auth.getName());

        // Option 2 : réutiliser deletePhoto() via l’ID trouvé par email
        Utilisateur u = repo.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur courant introuvable"));
        service.deletePhoto(u.getIdUtilisateur());
    }

    /* ===================== READ ===================== */

    /**
     * Lister tous les utilisateurs.
     *
     * @return Liste DTO
     */
    @GetMapping
    public List<UtilisateurDTO> list(){
        return service.findAll();
    }

    /**
     * Récupérer un utilisateur par ID.
     *
     * @param id ID utilisateur
     * @return DTO
     */
    @GetMapping("/{id}")
    public UtilisateurDTO get(@PathVariable Integer id){
        return service.findById(id);
    }

    /* ===================== UPDATE ===================== */

    /**
     * Mise à jour (partielle) d'un utilisateur (usage admin/gestionnaire).
     * Identité/contact uniquement (pas de rôle, ni password ici).
     *
     * @param id  ID utilisateur
     * @param dto Champs à mettre à jour
     * @return DTO mis à jour
     */
    @PutMapping("/{id}")
    public UtilisateurDTO update(@PathVariable Integer id, @Valid @RequestBody UtilisateurUpdateDTO dto){
        return service.update(id, dto);
    }

    /**
     * ADMIN/GESTIONNAIRE : valider/invalider un compte (compteValide).
     *
     * @param id    ID utilisateur
     * @param value true pour valider, false pour invalider
     * @return DTO mis à jour
     */
    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public UtilisateurDTO valider(@PathVariable Integer id, @RequestParam("value") boolean value){
        UtilisateurUpdateDTO dto = new UtilisateurUpdateDTO();
        dto.setCompteValide(value);
        return service.update(id, dto);
    }

    /**
     * ADMIN/GESTIONNAIRE : confirmer/annuler la vérification d'email (emailVerifie).
     *
     * @param id    ID utilisateur
     * @param value true pour confirmé, false pour annuler
     * @return DTO mis à jour
     */
    @PatchMapping("/{id}/email-verifie")
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public UtilisateurDTO emailVerifie(@PathVariable Integer id, @RequestParam("value") boolean value){
        UtilisateurUpdateDTO dto = new UtilisateurUpdateDTO();
        dto.setEmailVerifie(value);
        return service.update(id, dto);
    }

    /* ====== PHOTO (ADMIN / GESTIONNAIRE) ====== */

    /**
     * Upload / Remplacement de la photo d’un utilisateur (multipart).
     * Réservé ADMIN/GESTIONNAIRE. Retourne l'utilisateur à jour (avec photoUrl).
     *
     * @param id   ID utilisateur
     * @param file Fichier image (jpeg | png | webp)
     * @return DTO mis à jour (photoUrl)
     */
    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public UtilisateurDTO uploadPhoto(@PathVariable Integer id,
                                      @RequestParam("file") MultipartFile file) {
        try {
            return service.uploadPhoto(id, file);
        } catch (Exception ex) {
            throw new RuntimeException("Upload image échoué : " + ex.getMessage(), ex);
        }
    }

    /**
     * “Lecture” de la photo : redirige vers l’URL publique /files/** si présente.
     * (Optionnel : le front peut utiliser directement photoUrl sans appeler cette route)
     *
     * @param id ID utilisateur
     * @return 302 vers /files/users/xxx.ext ou 404 si pas de photo
     */
    @GetMapping("/{id}/photo")
    public ResponseEntity<Void> getPhotoRedirect(@PathVariable Integer id) {
        Utilisateur u = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + id + " introuvable"));

        String path = Optional.ofNullable(u.getPhotoPath()).orElse("").trim();
        if (path.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // /files/** est exposé publiquement par WebMvcConfig
        URI location = URI.create("/files/" + path);
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build(); // 302
    }

    /**
     * Supprimer la photo d’un utilisateur (sans supprimer l’utilisateur).
     * Réservé ADMIN/GESTIONNAIRE.
     *
     * @param id ID utilisateur
     */
    @DeleteMapping("/{id}/photo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public void deletePhoto(@PathVariable Integer id) {
        service.deletePhoto(id);
    }

    /* ===================== DELETE ===================== */

    /**
     * Supprimer un utilisateur.
     * Supprime aussi la photo associée et le profil formateur lié.
     *
     * @param id ID utilisateur
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public void delete(@PathVariable Integer id){
        service.delete(id);
    }
}
