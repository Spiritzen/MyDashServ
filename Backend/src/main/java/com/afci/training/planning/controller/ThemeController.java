package com.afci.training.planning.controller;

import com.afci.training.planning.dto.ThemeRequest;
import com.afci.training.planning.dto.ThemeResponse;
import com.afci.training.planning.entity.Theme;
import com.afci.training.planning.util.ThemeMapper;
import com.afci.training.planning.service.ThemeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService service;
    public ThemeController(ThemeService service) { this.service = service; }

    @GetMapping
    public List<ThemeResponse> list() {
        return service.listAll().stream().map(ThemeMapper::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public ThemeResponse create(@RequestBody @Valid ThemeRequest req) {
        Theme t = service.create(req);
        return ThemeMapper.toDto(t);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public ThemeResponse update(@PathVariable Integer id, @RequestBody @Valid ThemeRequest req) {
        Theme t = service.update(id, req);
        return ThemeMapper.toDto(t);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
    public void toggle(@PathVariable Integer id, @RequestParam boolean value) {
        service.toggleActive(id, value);
    }
}
