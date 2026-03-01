package com.afci.training.planning.controller;

import com.afci.training.planning.dto.FormationDTO;
import com.afci.training.planning.dto.FormationUpdateDTO;
import com.afci.training.planning.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/formations")
@PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
public class FormationController {

    private final FormationService service;

    public FormationController(FormationService service) {
        this.service = service;
    }

    /** LIST + filtre q + pagination tri */
    @GetMapping
    public Page<FormationDTO> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "intitule,asc") String sort) {

        String[] sp = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(sp.length > 1 ? sp[1] : "asc"), sp[0]);
        Pageable pageable = PageRequest.of(page, size, s);

        return service.findAll(q, pageable);
    }

    /** READ ONE */
    @GetMapping("/{id}")
    public FormationDTO get(@PathVariable Integer id){
        return service.findById(id);
    }

    /** CREATE */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormationDTO create(@Valid @RequestBody FormationDTO dto){
        return service.create(dto);
    }

    /** UPDATE (partiel via DTO) */
    @PutMapping("/{id}")
    public FormationDTO update(@PathVariable Integer id, @RequestBody FormationUpdateDTO dto){
        return service.update(id, dto);
    }

    /** DELETE */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id){
        service.delete(id);
    }
}
