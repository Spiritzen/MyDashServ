package com.afci.training.planning.repository;

import com.afci.training.planning.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    Optional<Theme> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
