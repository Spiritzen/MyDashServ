package com.afci.training.planning.controller;

import com.afci.training.planning.dto.CompetenceDTO;
import com.afci.training.planning.entity.Competence;
import com.afci.training.planning.repository.CompetenceRepository;
import com.afci.training.planning.util.CompetenceMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competences")
public class CompetenceController {

    private final CompetenceRepository repo;

    public CompetenceController(CompetenceRepository repo) {
        this.repo = repo;
    }

    // GET /api/competences           -> toutes
    // GET /api/competences?themeId=1 -> filtrées par thème
    @GetMapping
    public List<CompetenceDTO> list(@RequestParam(required = false) Integer themeId) {
        List<Competence> data = (themeId == null)
                ? repo.findAll()
                : repo.findByTheme_IdTheme(themeId);
        return data.stream().map(CompetenceMapper::toDTO).toList();
    }

    // (optionnel) GET détail par id
    @GetMapping("/{id}")
    public CompetenceDTO findOne(@PathVariable Integer id) {
        Competence c = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compétence introuvable: " + id));
        return CompetenceMapper.toDTO(c);
    }
}
