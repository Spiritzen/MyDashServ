package com.afci.training.planning.util;

import com.afci.training.planning.dto.FormateurCompDTO;
import com.afci.training.planning.entity.FormateurComp;

public final class FormateurCompMapper {
    private FormateurCompMapper() {}

    public static FormateurCompDTO toDTO(FormateurComp fc) {
        return new FormateurCompDTO(
            fc.getCompetence().getIdCompetence(),
            fc.getCompetence().getLabel(),
            fc.getNiveau(),
            fc.getTitle(),
            fc.getDescription(),
            fc.getExperienceYears(),
            fc.getLastUsed(),
            (fc.getStatus() != null ? fc.getStatus().name() : null),
            fc.isVisible(),
            fc.getDerniereMaj()
        );
    }
}
