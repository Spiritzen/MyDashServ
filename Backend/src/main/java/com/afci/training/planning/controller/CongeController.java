package com.afci.training.planning.controller;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.afci.training.planning.dto.CongeDTO;
import com.afci.training.planning.dto.CreateCongeDTO;
import com.afci.training.planning.dto.UpdateCongeDTO;
import com.afci.training.planning.service.CongeService;

@RestController
@RequestMapping("/api/conges")
public class CongeController {

    private final CongeService congeService;

    public CongeController(CongeService congeService) {
        this.congeService = congeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CongeDTO create(@RequestBody CreateCongeDTO dto) {
        return congeService.create(dto);
    }

    @PutMapping("/{id}")
    public CongeDTO update(@PathVariable Integer id, @RequestBody UpdateCongeDTO dto) {
        return congeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        congeService.delete(id);
    }

    @GetMapping("/formateur/{formateurId}")
    public List<CongeDTO> listByFormateur(@PathVariable Integer formateurId,
                                          @RequestParam LocalDateTime from,
                                          @RequestParam LocalDateTime to) {
        return congeService.listByFormateur(formateurId, from, to);
    }
}
