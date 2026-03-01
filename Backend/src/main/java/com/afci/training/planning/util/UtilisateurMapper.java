package com.afci.training.planning.util;

import com.afci.training.planning.dto.UtilisateurCreateDTO;
import com.afci.training.planning.dto.UtilisateurDTO;
import com.afci.training.planning.dto.UtilisateurUpdateDTO;
import com.afci.training.planning.entity.Utilisateur;

public final class UtilisateurMapper {

    private UtilisateurMapper(){}

    /* ========= READ -> DTO ========= */
    public static UtilisateurDTO toDTO(Utilisateur u){
        if (u == null) return null;

        UtilisateurDTO dto = new UtilisateurDTO();

        // Identité / contact de base
        dto.setIdUtilisateur(u.getIdUtilisateur());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole() != null ? u.getRole().toString() : null); // String ou Enum -> toString()
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setAdresse(u.getAdresse());
        dto.setVille(u.getVille());
        dto.setCodePostal(u.getCodePostal());
        dto.setTelephone(u.getTelephone());

    

        // ✅ URL publique pour affichage front
        if (u.getPhotoPath()!=null && !u.getPhotoPath().isBlank()){
            dto.setPhotoUrl("/files/" + u.getPhotoPath());
        } else {
            dto.setPhotoUrl(null);
        }

        // NOTE: On ne mappe PAS ici des champs qui n’existent pas dans l’entité
        // (compteValide, emailVerifie, formateurId, validatedById, etc.)

        // Si tu as des timestamps sur l’entité (createdAt/updatedAt/lastLoginAt...),
        // et que tu veux les exposer, tu peux les ajouter ici uniquement s’ils existent côté entity:
        // dto.setCreatedAt(u.getCreatedAt());
        // dto.setUpdatedAt(u.getUpdatedAt());
        // dto.setLastLoginAt(u.getLastLoginAt());
        // dto.setValidatedAt(u.getValidatedAt());
        // dto.setValidatedById(u.getValidatedById());

        return dto;
    }

    /* ========= CREATE -> Entity ========= */
    public static Utilisateur fromCreateDTO(UtilisateurCreateDTO d){
        if (d == null) return null;
        Utilisateur u = new Utilisateur();

        // Identité / contact de base (ne pas appeler de getters absents)
        u.setEmail(d.getEmail());
        // ⚠️ d.getRole() N’EXISTE PAS (erreur compil) → on n’y touche pas ici
        u.setNom(d.getNom());
        u.setPrenom(d.getPrenom());
        u.setAdresse(d.getAdresse());
        u.setVille(d.getVille());
        u.setCodePostal(d.getCodePostal());
        u.setTelephone(d.getTelephone());

        // ⚠️ Champs non présents dans CreateDTO selon tes erreurs:
        // - formateurId
        // - compteValide
        // - emailVerifie
        // On ne les touche pas ici.

        // ⚠️ NE PAS setter photoPath ici (upload séparé via endpoint)
        return u;
    }

    /* ========= UPDATE partiel -> Entity ========= */
    public static void copyToEntity(UtilisateurUpdateDTO d, Utilisateur u){
        if (d == null || u == null) return;

        // ⚠️ d.getEmail() N’EXISTE PAS selon tes erreurs -> on ne met PAS à jour l’email ici
        // ⚠️ d.getRole() non plus -> on ne touche pas au rôle

        // Champs “profil” classiques (seulement si présents dans UpdateDTO)
        if (d.getNom() != null)        u.setNom(d.getNom());
        if (d.getPrenom() != null)     u.setPrenom(d.getPrenom());
        if (d.getAdresse() != null)    u.setAdresse(d.getAdresse());
        if (d.getVille() != null)      u.setVille(d.getVille());
        if (d.getCodePostal() != null) u.setCodePostal(d.getCodePostal());
        if (d.getTelephone() != null)  u.setTelephone(d.getTelephone());

        // ⚠️ Pas de formateurId dans UpdateDTO d’après tes erreurs -> on ignore
        // ⚠️ Pas de flags (compteValide / emailVerifie) -> on ignore
        // ⚠️ On ne gère pas photoPath ici (upload/suppression via endpoints dédiés)
    }
}
