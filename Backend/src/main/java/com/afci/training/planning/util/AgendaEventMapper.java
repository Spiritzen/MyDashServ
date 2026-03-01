package com.afci.training.planning.util;

import com.afci.training.planning.dto.AgendaEventDTO;
import java.time.LocalDateTime;

public class AgendaEventMapper {

    public static AgendaEventDTO build(String id, String type, String title,
                                       LocalDateTime start, LocalDateTime end,
                                       String color, String recurrence, Integer relatedId) {
        return new AgendaEventDTO(id, type, title, start, end, color, recurrence, relatedId);
    }
}