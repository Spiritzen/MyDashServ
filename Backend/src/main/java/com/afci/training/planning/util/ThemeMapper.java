package com.afci.training.planning.util;

import com.afci.training.planning.dto.ThemeResponse;
import com.afci.training.planning.entity.Theme;

public final class ThemeMapper {
    private ThemeMapper() {}
    public static ThemeResponse toDto(Theme t) {
        return new ThemeResponse(t.getIdTheme(), t.getName(), t.isActive(), t.getCreatedAt(), t.getUpdatedAt());
    }
}