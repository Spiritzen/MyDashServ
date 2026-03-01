package com.afci.training.planning.repository;

import com.afci.training.planning.entity.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompetenceRepository extends JpaRepository<Competence, Integer> {
    List<Competence> findByTheme_IdTheme(Integer idTheme);
}
