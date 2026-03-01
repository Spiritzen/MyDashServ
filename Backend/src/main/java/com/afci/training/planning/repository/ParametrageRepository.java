package com.afci.training.planning.repository;

import com.afci.training.planning.entity.Parametrage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParametrageRepository extends JpaRepository<Parametrage, Integer> {
    Optional<Parametrage> findByCle(String cle);
}
