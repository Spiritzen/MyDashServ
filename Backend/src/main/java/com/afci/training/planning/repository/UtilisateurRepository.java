package com.afci.training.planning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.projection.UtilisateurLiteByFormateurProjection;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    // --- Base
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByEmailIgnoreCase(String email);

    // --- Admin : compteurs / listes
    @Query("""
        select count(u)
        from Utilisateur u
        where u.role = com.afci.training.planning.entity.Utilisateur$Role.FORMATEUR
          and u.compteValide = false
          and u.formateur is not null
          and u.formateur.profilSoumisLe is not null
    """)
    long countPendingCandidatures();

    @Query("""
        select distinct u
        from Utilisateur u
          left join fetch u.formateur f
        where u.role = com.afci.training.planning.entity.Utilisateur$Role.FORMATEUR
          and (
                (:statut = 'EN_ATTENTE' and u.compteValide = false and f is not null and f.profilSoumisLe is not null)
             or (:statut = 'VALIDE'     and u.compteValide = true)
          )
        order by u.prenom asc, u.nom asc
    """)
    List<Utilisateur> findByStatutCandidature(@Param("statut") String statut);

    // --- Matching : récupérer l'utilisateur via formateur_id (clé étrangère dans "utilisateur")
    // 1) Dérivation Spring Data : u.formateur.idFormateur = :fid
    Optional<Utilisateur> findFirstByFormateur_IdFormateur(Integer fid);

    // 2) JPQL explicite (équivalent)
    @Query("select u from Utilisateur u where u.formateur.idFormateur = :fid")
    Optional<Utilisateur> findFirstByFormateurId(@Param("fid") Integer fid);

    // 3) Secours natif si jamais la traverse OneToOne pose souci
    @Query(value = "select * from utilisateur where formateur_id = :fid limit 1", nativeQuery = true)
    Optional<Utilisateur> findFirstByFormateurIdNative(@Param("fid") Integer fid);
    // --- EXISTANT (laisse ton code admin/compteurs etc.)
    // Optional<Utilisateur> findByEmail(String email);
    // Optional<Utilisateur> findByEmailIgnoreCase(String email);
    // long countPendingCandidatures();
    // List<Utilisateur> findByStatutCandidature(String statut);
    // … (tu laisses ce que tu avais)

    /** 
     * Récupère toutes les liaisons formateur ↔ utilisateur avec juste ce qu’il faut
     * pour afficher les candidats (formateur_id, nom, prenom, email).
     * Pas de surprise de mapping : on passe par un SELECT simple.
     */
    @Query(value = """
        select u.formateur_id as formateurId, u.nom as nom, u.prenom as prenom, u.email as email
        from utilisateur u
        where u.formateur_id is not null
    """, nativeQuery = true)
    List<Object[]> fetchUserLiteByFormateur();
    @Query("""
    		   select u.formateur.idFormateur as formateurId,
    		          u.prenom as prenom,
    		          u.nom as nom,
    		          u.email as email
    		   from Utilisateur u
    		   where u.formateur.idFormateur in :ids
    		""")
    		List<UtilisateurLiteByFormateurProjection> fetchUsersLiteByFormateurs(@Param("ids") java.util.List<Integer> ids);
    
}
