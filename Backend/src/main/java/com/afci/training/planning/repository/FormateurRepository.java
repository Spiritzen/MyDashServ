// com.afci.training.planning.repository.FormateurRepository
package com.afci.training.planning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.afci.training.planning.entity.Formateur;

@Repository
public interface FormateurRepository extends JpaRepository<Formateur, Integer> {

    // Remplace l’ancienne méthode cassée :
    // List<Formateur> findByCompetenceFormationId(Integer formationId);

    // Simple et robuste : on ramène les formateurs actifs
    List<Formateur> findByActifTrue();
    
    // Formateurs actifs + compte validé côté utilisateur (et rôle FORMATEUR)
    @Query("""
            select f
            from Formateur f
            where f.actif = true
              and exists (
                select 1 from com.afci.training.planning.entity.Utilisateur u
                where u.formateur.idFormateur = f.idFormateur
                  and u.role = com.afci.training.planning.entity.Utilisateur$Role.FORMATEUR
                  and u.compteValide = true
              )
            """)
     List<Formateur> findAllActiveAndValidated();
    /**
     * Récupère nom/prenom/email côté utilisateur pour une liste d'IDs formateur.
     * LEFT JOIN pour ne rien rater même si l’utilisateur n’existe pas encore.
     */
    @Query(value = """
        select f.id_formateur as fid, u.nom as nom, u.prenom as prenom, u.email as email
        from formateur f
        left join utilisateur u on u.formateur_id = f.id_formateur
        where f.id_formateur in (:ids)
    """, nativeQuery = true)
    List<Object[]> fetchUserFieldsForFormateurIds(@Param("ids") List<Integer> ids);
}
