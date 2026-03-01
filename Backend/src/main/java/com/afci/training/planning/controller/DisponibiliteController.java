package com.afci.training.planning.controller;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.afci.training.planning.dto.CreateDispoDTO;
import com.afci.training.planning.dto.DisponibiliteDTO;
import com.afci.training.planning.dto.UpdateDispoDTO;
import com.afci.training.planning.service.DisponibiliteService;

@RestController
@RequestMapping("/api/disponibilites")
public class DisponibiliteController {

    private final DisponibiliteService dispoService;

    public DisponibiliteController(DisponibiliteService dispoService) {
        this.dispoService = dispoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisponibiliteDTO create(@RequestBody CreateDispoDTO dto) {
        return dispoService.create(dto);
    }

    @PutMapping("/{id}")
    public DisponibiliteDTO update(@PathVariable Integer id, @RequestBody UpdateDispoDTO dto) {
        return dispoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        dispoService.delete(id);
    }

    @GetMapping("/formateur/{formateurId}")
    public List<DisponibiliteDTO> listByFormateur(@PathVariable Integer formateurId,
                                                  @RequestParam LocalDateTime from,
                                                  @RequestParam LocalDateTime to) {
        return dispoService.listByFormateur(formateurId, from, to);
    }
}
