package com.afci.training.planning.util;

import com.afci.training.planning.dto.CompetenceDTO;
import com.afci.training.planning.entity.Competence;
import com.afci.training.planning.entity.Theme;

public final class CompetenceMapper {
    private CompetenceMapper() {}

    public static CompetenceDTO toDTO(Competence c) {
        if (c == null) return null;
        Theme t = c.getTheme();
        return new CompetenceDTO(
            c.getIdCompetence(),
            c.getLabel(),
            c.getDescription(),
            t != null ? t.getIdTheme() : null,
            t != null ? t.getName()    : null
        );
    }
}
