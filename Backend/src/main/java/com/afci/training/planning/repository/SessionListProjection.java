package com.afci.training.planning.repository;

import java.time.LocalDateTime;

public interface SessionListProjection {
    Integer getId_session();
    java.time.LocalDateTime getDate_debut();
    java.time.LocalDateTime getDate_fin();
    Integer getFormation_id();
    String getMode();
    String getStatut();
    String getVille();
    String getSalle();
    Integer getId_formateur();
    String getFormateur_prenom();
    String getFormateur_nom();
}