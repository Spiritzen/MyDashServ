package com.afci.training.planning.util;

import java.time.LocalDateTime;

public final class DateOverlapUtil {

    private DateOverlapUtil() { }

    public static boolean overlap(LocalDateTime aStart, LocalDateTime aEnd,
                                  LocalDateTime bStart, LocalDateTime bEnd) {
        if (aStart == null || aEnd == null || bStart == null || bEnd == null) return false;
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }
}
