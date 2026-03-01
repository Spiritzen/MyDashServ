package com.afci.training.planning.service;

import com.afci.training.planning.dto.ThemeRequest;
import com.afci.training.planning.entity.Theme;
import com.afci.training.planning.repository.ThemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThemeService {

    private final ThemeRepository repo;

    public ThemeService(ThemeRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Theme> listAll() {
        return repo.findAll();
    }

    @Transactional
    public Theme create(ThemeRequest req) {
        String name = req.getName().trim();
        if (repo.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Un thème '" + name + "' existe déjà.");
        }
        Theme t = new Theme(name);
        if (req.getActive() != null) t.setActive(req.getActive());
        return repo.save(t);
    }

    @Transactional
    public Theme update(Integer id, ThemeRequest req) {
        Theme t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Thème introuvable: " + id));
        String name = req.getName().trim();
        if (!t.getName().equalsIgnoreCase(name) && repo.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Un thème '" + name + "' existe déjà.");
        }
        t.setName(name);
        if (req.getActive() != null) t.setActive(req.getActive());
        return t; // géré par @Transactional
    }

    @Transactional
    public void toggleActive(Integer id, boolean active) {
        Theme t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Thème introuvable: " + id));
        t.setActive(active);
    }
}
