// src/main/java/com/afci/training/planning/controller/AdminSessionController.java
package com.afci.training.planning.controller;

import com.afci.training.planning.dto.*;
import com.afci.training.planning.entity.Session.Statut;
import com.afci.training.planning.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/sessions")
@PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) { this.service = service; }

    // GET /api/admin/sessions?search=...&statut=PLANIFIEE&formationId=1&from=2025-10-01&to=2025-10-31&page=0&size=10&sort=dateDebut,asc
    @GetMapping
    public Page<SessionListDTO> list(
            @RequestParam Optional<String> search,
            @RequestParam Optional<Statut> statut,
            @RequestParam Optional<Integer> formationId,
            @RequestParam Optional<LocalDate> from,
            @RequestParam Optional<LocalDate> to,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue="dateDebut,asc") String sort
    ) {
        var sp = sort.split(",");
        var pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sp.length > 1 ? sp[1] : "asc"), sp[0]));

        var filter = new SessionFilter();
        filter.search = search;
        filter.statut = statut;
        filter.formationId = formationId;
        filter.from = from;
        filter.to = to;

        return service.search(filter, pageable);
    }

    @GetMapping("/{id}")
    public SessionListDTO one(@PathVariable Integer id) {
        return service.findOne(id);
    }

    @PostMapping
    public SessionListDTO create(@Valid @RequestBody SessionSaveDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public SessionListDTO update(@PathVariable Integer id, @Valid @RequestBody SessionSaveDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
