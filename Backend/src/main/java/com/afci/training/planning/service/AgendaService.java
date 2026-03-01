package com.afci.training.planning.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.training.planning.dto.AgendaEventDTO;
import com.afci.training.planning.entity.Conge;
import com.afci.training.planning.entity.Disponibilite;
import com.afci.training.planning.entity.Session;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.CongeRepository;
import com.afci.training.planning.repository.DisponibiliteRepository;
import com.afci.training.planning.util.AgendaEventMapper;

@Service
@Transactional(readOnly = true)
public class AgendaService {

    private final AffectationRepository affectationRepository;
    private final CongeRepository congeRepository;
    private final DisponibiliteRepository dispoRepository;

    public AgendaService(AffectationRepository affectationRepository,
                         CongeRepository congeRepository,
                         DisponibiliteRepository dispoRepository) {
        this.affectationRepository = affectationRepository;
        this.congeRepository = congeRepository;
        this.dispoRepository = dispoRepository;
    }

    public List<AgendaEventDTO> getAgenda(Integer formateurId, LocalDateTime from, LocalDateTime to, boolean withFree) {
        List<AgendaEventDTO> out = new ArrayList<>();

        // 1️⃣ Sessions confirmées
        List<Session> sessions = affectationRepository.findConfirmedSessionsByFormateurBetween(formateurId, from, to);
        for (Session s : sessions) {
            String title = "Session";
            out.add(AgendaEventMapper.build("SESS-" + s.getIdSession(), "SESSION", title,
                    s.getDateDebut(), s.getDateFin(), "#3b82f6", null, s.getIdSession()));
        }

        // 2️⃣ Congés
        for (Conge c : congeRepository
                .findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(formateurId, to, from)) {
            String motif = c.getMotif() != null ? c.getMotif() : "Congé";
            out.add(AgendaEventMapper.build("CONGE-" + c.getIdConge(), "CONGE", "Congé (" + motif + ")",
                    c.getStartAt(), c.getEndAt(), "#ef4444", null, c.getIdConge()));
        }

        // 3️⃣ Indispos
        for (Disponibilite d : dispoRepository
                .findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(formateurId, to, from)) {
            if (d.getRecurrence() == null || "AUCUNE".equals(d.getRecurrence().name())) {
                out.add(AgendaEventMapper.build("DISPO-" + d.getIdDisponibilite(), "INDISPO",
                        "Indispo: " + (d.getCommentaire() == null ? "" : d.getCommentaire()),
                        d.getStartAt(), d.getEndAt(), "#ef4444", "AUCUNE", d.getIdDisponibilite()));
            } else {
                out.addAll(expandRecurrence(d, from, to));
            }
        }

        // Tri
        out.sort(Comparator.comparing(AgendaEventDTO::getStart));

        // 4️⃣ Créneaux libres
        if (withFree) {
            out.addAll(computeFreeSlots(out, from, to));
        }

        return out;
    }

    /** Expansion des récurrences hebdo/mensuelles */
    private List<AgendaEventDTO> expandRecurrence(Disponibilite d, LocalDateTime from, LocalDateTime to) {
        List<AgendaEventDTO> list = new ArrayList<>();
        LocalDateTime baseStart = d.getStartAt();
        LocalDateTime baseEnd = d.getEndAt();
        String title = "Indispo: " + (d.getCommentaire() == null ? "" : d.getCommentaire());
        String rec = d.getRecurrence().name();

        LocalDateTime curStart = alignStart(baseStart, from, rec);
        while (curStart.isBefore(to) || curStart.isEqual(to)) {
            LocalDateTime curEnd = curStart.plus(ChronoUnit.MILLIS.between(baseStart, baseEnd), ChronoUnit.MILLIS);
            if (!(curEnd.isBefore(from) || curStart.isAfter(to))) {
                String id = "DISPO-" + d.getIdDisponibilite() + "#" + curStart;
                list.add(AgendaEventMapper.build(id, "INDISPO", title, curStart, curEnd, "#ef4444", rec,
                        d.getIdDisponibilite()));
            }
            curStart = increment(curStart, rec);
        }
        return list;
    }

    private LocalDateTime alignStart(LocalDateTime baseStart, LocalDateTime from, String rec) {
        LocalDateTime cur = baseStart;
        if ("HEBDO".equals(rec)) {
            while (cur.isBefore(from)) cur = cur.plusWeeks(1);
        } else if ("MENSUELLE".equals(rec)) {
            while (cur.isBefore(from)) cur = cur.plusMonths(1);
        }
        return cur;
    }

    private LocalDateTime increment(LocalDateTime dt, String rec) {
        if ("HEBDO".equals(rec)) return dt.plusWeeks(1);
        if ("MENSUELLE".equals(rec)) return dt.plusMonths(1);
        return dt;
    }

    /** Calcul des créneaux libres entre événements bloquants */
    private List<AgendaEventDTO> computeFreeSlots(List<AgendaEventDTO> events, LocalDateTime from, LocalDateTime to) {
        List<AgendaEventDTO> free = new ArrayList<>();
        LocalDateTime cursor = from;

        for (AgendaEventDTO e : events) {
            if (!List.of("SESSION", "CONGE", "INDISPO").contains(e.getType())) continue;

            if (e.getStart().isAfter(cursor)) {
                free.add(AgendaEventMapper.build("FREE-" + cursor, "FREE", "Créneau libre",
                        cursor, e.getStart(), "#22c55e", null, null));
            }
            if (e.getEnd().isAfter(cursor)) cursor = e.getEnd();
        }

        if (cursor.isBefore(to)) {
            free.add(AgendaEventMapper.build("FREE-" + cursor, "FREE", "Créneau libre",
                    cursor, to, "#22c55e", null, null));
        }
        return free;
    }
}
