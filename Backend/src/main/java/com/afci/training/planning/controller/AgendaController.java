package com.afci.training.planning.controller;



import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.afci.training.planning.dto.AgendaEventDTO;
import com.afci.training.planning.service.AgendaService;

@RestController
@RequestMapping("/api")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    // GET /api/formateurs/{id}/agenda?from=2025-11-01T00:00:00&to=2025-11-30T23:59:59&withFree=true
    @GetMapping("/formateurs/{formateurId}/agenda")
    public List<AgendaEventDTO> getAgenda(@PathVariable Integer formateurId,
                                          @RequestParam LocalDateTime from,
                                          @RequestParam LocalDateTime to,
                                          @RequestParam(name = "withFree", defaultValue = "false") boolean withFree) {
        return agendaService.getAgenda(formateurId, from, to, withFree);
    }
}
