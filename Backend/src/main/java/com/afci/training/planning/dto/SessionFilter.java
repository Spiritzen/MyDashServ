// src/main/java/com/afci/training/planning/dto/SessionFilter.java
package com.afci.training.planning.dto;

import com.afci.training.planning.entity.Session.Statut;
import java.time.LocalDate;
import java.util.Optional;

public class SessionFilter {
    public Optional<String> search = Optional.empty();   // (formation label / ville / salle) -> à enrichir
    public Optional<Statut> statut = Optional.empty();
    public Optional<Integer> formationId = Optional.empty();
    public Optional<LocalDate> from = Optional.empty();
    public Optional<LocalDate> to = Optional.empty();
}
