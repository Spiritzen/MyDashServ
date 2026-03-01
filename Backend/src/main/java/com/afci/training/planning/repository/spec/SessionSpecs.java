// src/main/java/com/afci/training/planning/repository/spec/SessionSpecs.java
package com.afci.training.planning.repository.spec;

import com.afci.training.planning.dto.SessionFilter;
import com.afci.training.planning.entity.Formation;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.repository.FormationRepository;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class SessionSpecs {

    private SessionSpecs() {}

    public static Specification<Session> fromFilter(SessionFilter filter, FormationRepository formationRepo) {
        return (Root<Session> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> ands = new ArrayList<>();

            // ----- STATUT -----
            filter.statut.ifPresent(st -> ands.add(cb.equal(root.get("statut"), st)));

            // ----- FORMATION ID -----
            filter.formationId.ifPresent(fid -> ands.add(cb.equal(root.get("formationId"), fid)));

            // ----- DATES -----
            if (filter.from.isPresent()) {
                LocalDateTime start = filter.from.get().atStartOfDay(); // inclusif
                ands.add(cb.greaterThanOrEqualTo(root.get("dateDebut"), start));
            }
            if (filter.to.isPresent()) {
                LocalDateTime end = LocalDateTime.of(filter.to.get(), LocalTime.MAX); // inclusif fin de journée
                ands.add(cb.lessThanOrEqualTo(root.get("dateFin"), end));
            }

            // ----- SEARCH (ville OU intitulé formation) -----
            if (filter.search.isPresent() && !filter.search.get().isBlank()) {
                String q = "%" + filter.search.get().trim().toLowerCase() + "%";

                // ville LIKE q
                Predicate byVille = cb.like(cb.lower(root.get("ville")), q);

                // formation.intitule LIKE q (via subquery pour éviter dépendance forte au mapping)
                // Option 1 : subquery
                Subquery<Integer> sq = query.subquery(Integer.class);
                Root<Formation> f = sq.from(Formation.class);
                sq.select(f.get("idFormation")); // adapte si ta PK s'appelle autrement
                Predicate link = cb.equal(f.get("idFormation"), root.get("formationId"));
                Predicate byLib = cb.like(cb.lower(f.get("intitule")), q);
                sq.where(cb.and(link, byLib));

                Predicate byFormationLibelle = cb.exists(sq);

                // (ville LIKE q) OR (formation.intitule LIKE q)
                Predicate searchOr = cb.or(byVille, byFormationLibelle);
                ands.add(searchOr);
            }

            // Si tu as un JOIN ailleurs, protège-toi des doublons
            query.distinct(true);

            return ands.isEmpty() ? cb.conjunction() : cb.and(ands.toArray(new Predicate[0]));
        };
    }
}
