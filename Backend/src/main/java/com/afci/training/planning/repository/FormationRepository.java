package com.afci.training.planning.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.training.planning.entity.Formation;

public interface FormationRepository extends JpaRepository<Formation, Integer> {
	 List<Formation> findByIntituleContainingIgnoreCase(String q);
    Page<Formation> findByIntituleContainingIgnoreCase(String q, Pageable pageable);
    // On ne filtre pas "active/actif" pour éviter toute divergence de mapping,
    // on renvoie tout puis on filtrera plus tard si nécessaire.
    List<Formation> findAllByOrderByIntituleAsc();
}
