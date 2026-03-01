// src/main/java/com/afci/training/planning/controller/FormateurController.java
package com.afci.training.planning.controller;

import com.afci.training.planning.dto.FormateurCreateDTO;
import com.afci.training.planning.dto.FormateurDTO;
import com.afci.training.planning.dto.FormateurUpdateDTO;
import com.afci.training.planning.service.FormateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
public class FormateurController {

    private final FormateurService service;

    public FormateurController(FormateurService service) {
        this.service = service;
    }

    /** Retourne le formateur lié à l'utilisateur authentifié (email dans auth.getName()). */
    @GetMapping("/me")
    public FormateurDTO me(Authentication auth) {
        String email = auth.getName();  // placé par ton JwtFilter
        return service.findByUserEmail(email);
    }

    @PostMapping
    public ResponseEntity<FormateurDTO> create(@Valid @RequestBody FormateurCreateDTO dto) {
        FormateurDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/formateurs/" + created.getIdFormateur())).body(created);
    }

    @GetMapping
    public List<FormateurDTO> list() {
        return service.findAll();
    }

    // ⚠️ Regex pour éviter que "/me" arrive ici
    @GetMapping("/{id:\\d+}")
    public FormateurDTO get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PutMapping("/{id:\\d+}")
    public FormateurDTO update(@PathVariable Integer id, @Valid @RequestBody FormateurUpdateDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id:\\d+}/activer")
    public FormateurDTO activer(@PathVariable Integer id, @RequestParam("value") boolean value) {
        FormateurUpdateDTO dto = new FormateurUpdateDTO();
        dto.setActif(value);
        return service.update(id, dto);
    }

    @PatchMapping("/{id:\\d+}/soumettre")
    public FormateurDTO soumettre(@PathVariable Integer id) {
        FormateurUpdateDTO dto = new FormateurUpdateDTO();
        dto.setSoumettre(Boolean.TRUE);
        return service.update(id, dto);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
