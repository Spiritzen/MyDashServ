// src/main/java/com/afci/training/planning/enums/StatutCandidature.java
package com.afci.training.planning.enums;

public enum StatutCandidature {
    NON_ELIGIBLE,   // profil incomplet ou aucune compétence
    EN_ATTENTE,     // profil soumis, en revue (compte_valide = false)
    VALIDE,         // compte validé par admin/gestionnaire
    REFUSE          // (optionnel) si tu ajoutes un motif de refus côté admin
}
