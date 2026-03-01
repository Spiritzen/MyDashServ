// src/main/java/com/afci/training/planning/service/SessionService.java
package com.afci.training.planning.service;

import com.afci.training.planning.dto.SessionFilter;
import com.afci.training.planning.dto.SessionListDTO;
import com.afci.training.planning.dto.SessionSaveDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service applicatif pour la gestion des Sessions (CRUD + recherche).
 * <p>
 * Contrats :
 * <ul>
 *   <li>{@link #search(SessionFilter, Pageable)} : recherche paginée avec filtres</li>
 *   <li>{@link #findOne(Integer)} : lecture d'une session</li>
 *   <li>{@link #create(SessionSaveDTO)} : création</li>
 *   <li>{@link #update(Integer, SessionSaveDTO)} : mise à jour</li>
 *   <li>{@link #delete(Integer)} : suppression</li>
 * </ul>
 */
public interface SessionService {

    /**
     * Recherche paginée de sessions selon les filtres fournis.
     */
    Page<SessionListDTO> search(@NotNull SessionFilter filter, @NotNull Pageable pageable);

    /**
     * Retourne une session par identifiant.
     * @throws jakarta.persistence.EntityNotFoundException si non trouvée
     */
    SessionListDTO findOne(@NotNull Integer id);

    /**
     * Crée une nouvelle session à partir du DTO.
     * @throws jakarta.persistence.EntityNotFoundException si la formation référencée n'existe pas
     */
    SessionListDTO create(@Valid @NotNull SessionSaveDTO dto);

    /**
     * Met à jour une session existante.
     * @throws jakarta.persistence.EntityNotFoundException si la session ou la formation n'existe pas
     */
    SessionListDTO update(@NotNull Integer id, @Valid @NotNull SessionSaveDTO dto);

    /**
     * Supprime une session.
     */
    void delete(@NotNull Integer id);
}
