package com.afci.training.planning.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.training.planning.dto.CreateDispoDTO;
import com.afci.training.planning.dto.DisponibiliteDTO;
import com.afci.training.planning.dto.UpdateDispoDTO;
import com.afci.training.planning.entity.Conge;
import com.afci.training.planning.entity.Disponibilite;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.exception.ConflictException;
import com.afci.training.planning.exception.NotFoundException;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.CongeRepository;
import com.afci.training.planning.repository.DisponibiliteRepository;
import com.afci.training.planning.repository.FormateurRepository;
import com.afci.training.planning.util.DisponibiliteMapper;
import com.afci.training.planning.util.EntityAccessUtil;

@Service
@Transactional
public class DisponibiliteService {

    private final DisponibiliteRepository dispoRepository;
    private final CongeRepository congeRepository;
    private final FormateurRepository formateurRepository;
    private final AffectationRepository affectationRepository;

    public DisponibiliteService(DisponibiliteRepository dispoRepository,
                                CongeRepository congeRepository,
                                FormateurRepository formateurRepository,
                                AffectationRepository affectationRepository) {
        this.dispoRepository = dispoRepository;
        this.congeRepository = congeRepository;
        this.formateurRepository = formateurRepository;
        this.affectationRepository = affectationRepository;
    }

    public DisponibiliteDTO create(CreateDispoDTO dto) {
        validateRange(dto.getStartAt(), dto.getEndAt());
        Integer formateurId = dto.getFormateurId();
        if (formateurId == null) throw new IllegalArgumentException("formateurId obligatoire.");

        // ✅ sessions affectées : on passe par AffectationRepository (source de vérité)
        if (affectationRepository.existsOverlap(formateurId, dto.getStartAt(), dto.getEndAt())) {
            throw new ConflictException("Chevauche une session affectée.");
        }
        // congés
        if (!congeRepository.findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                formateurId, dto.getEndAt(), dto.getStartAt()).isEmpty()) {
            throw new ConflictException("Chevauche un congé existant.");
        }
        // indispos
        if (!dispoRepository.findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                formateurId, dto.getEndAt(), dto.getStartAt()).isEmpty()) {
            throw new ConflictException("Chevauche une indisponibilité existante.");
        }

        Disponibilite entity = new Disponibilite();

        // Poser formateur (relation ou champ id selon ton mapping)
        Formateur f = formateurRepository.findById(formateurId)
            .orElseThrow(() -> new NotFoundException("Formateur introuvable: " + formateurId));
        EntityAccessUtil.setFormateur(entity, f, formateurId);

        entity.setCommentaire(dto.getCommentaire());
        entity.setStartAt(dto.getStartAt());
        entity.setEndAt(dto.getEndAt());

        // Récurrence (si tu l'utilises)
        EntityAccessUtil.setRecurrence(entity, dto.getRecurrence());

        return DisponibiliteMapper.toDTO(dispoRepository.save(entity));
    }

    public DisponibiliteDTO update(Integer id, UpdateDispoDTO dto) {
        Disponibilite entity = dispoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Indisponibilité introuvable: " + id));

        LocalDateTime start = dto.getStartAt() != null ? dto.getStartAt() : entity.getStartAt();
        LocalDateTime end   = dto.getEndAt()   != null ? dto.getEndAt()   : entity.getEndAt();
        validateRange(start, end);

        Integer formateurId = EntityAccessUtil.resolveFormateurId(entity);
        if (formateurId == null) throw new IllegalArgumentException("Indisponibilité sans formateur.");

        // ✅ sessions affectées via AffectationRepository
        if (affectationRepository.existsOverlap(formateurId, start, end)) {
            throw new ConflictException("Chevauche une session affectée.");
        }
        // congés
        for (Conge c : congeRepository.findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                formateurId, end, start)) {
            throw new ConflictException("Chevauche un congé existant.");
        }
        // autres indispos (exclure la courante)
        for (Disponibilite d : dispoRepository.findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                formateurId, end, start)) {
            Integer did = null;
            try { did = d.getIdDisponibilite(); } catch (Exception ignore) {}
            if (did != null && !did.equals(id)) {
                throw new ConflictException("Chevauche une indisponibilité existante.");
            }
        }

        if (dto.getCommentaire() != null) entity.setCommentaire(dto.getCommentaire());
        EntityAccessUtil.setRecurrence(entity, dto.getRecurrence());
        entity.setStartAt(start);
        entity.setEndAt(end);

        return DisponibiliteMapper.toDTO(dispoRepository.save(entity));
    }

    public void delete(Integer id) {
        Disponibilite entity = dispoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Indisponibilité introuvable: " + id));
        dispoRepository.delete(entity);
    }

    public List<DisponibiliteDTO> listByFormateur(Integer formateurId, LocalDateTime from, LocalDateTime to) {
        return dispoRepository
            .findByFormateurIdAndStartAtBetween(formateurId, from, to)
            .stream().map(DisponibiliteMapper::toDTO).collect(Collectors.toList());
    }

    /**
     * Utilisé par le MatchingService : pour l’instant on valide tout.
     * Tu pourras l’enrichir (patterns hebdo L-V / 9-17, exceptions, etc.)
     */
    public boolean matchesWeeklyPattern(Integer formateurId, LocalDateTime start, LocalDateTime end) {
        return true;
    }

    private void validateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("startAt et endAt obligatoires.");
        if (end.isBefore(start)) throw new IllegalArgumentException("endAt doit être après startAt.");
    }
}
