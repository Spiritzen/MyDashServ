package com.afci.training.planning.repository.projection;

import java.time.LocalDateTime;

public interface FormateurAffectationProjection {

    // Affectation
    Integer getAffectationId();
    String getAffectationStatut(); // PROPOSEE / CONFIRMEE / ANNULEE
    String getCommentaire();
    LocalDateTime getCreatedAt();

    // Session
    Integer getSessionId();
    LocalDateTime getDateDebut();
    LocalDateTime getDateFin();
    String getMode();          // PRESENTIEL/DISTANCIEL/...
    String getSessionStatut(); // PLANIFIEE / EN_COURS / ANNULEE / TERMINEE / ...

    String getVille();
    String getSalle();

    // Formation
    Integer getFormationId();
    String getFormationIntitule();
}
