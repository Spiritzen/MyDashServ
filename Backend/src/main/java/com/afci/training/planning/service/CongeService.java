package com.afci.training.planning.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.training.planning.dto.CongeDTO;
import com.afci.training.planning.dto.CreateCongeDTO;
import com.afci.training.planning.dto.UpdateCongeDTO;
import com.afci.training.planning.entity.Conge;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.exception.ConflictException;
import com.afci.training.planning.exception.NotFoundException;
import com.afci.training.planning.repository.AffectationRepository;
import com.afci.training.planning.repository.CongeRepository;
import com.afci.training.planning.repository.DisponibiliteRepository;
import com.afci.training.planning.repository.FormateurRepository;
import com.afci.training.planning.util.CongeMapper;
import com.afci.training.planning.util.EntityAccessUtil;

@Service
@Transactional
public class CongeService {

    private final CongeRepository congeRepository;
    private final DisponibiliteRepository dispoRepository;
    private final FormateurRepository formateurRepository;
    private final AffectationRepository affectationRepository;

    public CongeService(CongeRepository congeRepository,
                        DisponibiliteRepository dispoRepository,
                        FormateurRepository formateurRepository,
                        AffectationRepository affectationRepository) {
        this.congeRepository = congeRepository;
        this.dispoRepository = dispoRepository;
        this.formateurRepository = formateurRepository;
        this.affectationRepository = affectationRepository;
    }

    public CongeDTO create(CreateCongeDTO dto) {
        validateRange(dto.getStartAt(), dto.getEndAt());

        Integer formateurId = dto.getFormateurId();
        if (formateurId == null) throw new IllegalArgumentException("formateurId obligatoire.");

        // ✅ Sessions affectées via AffectationRepository
        if (affectationRepository.existsOverlap(formateurId, dto.getStartAt(), dto.getEndAt())) {
            throw new ConflictException("Chevauche une session affectée.");
        }
        // Congés
        if (!congeRepository.findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                formateurId, dto.getEndAt(), dto.getStartAt()).isEmpty()) {
            throw new ConflictException("Chevauche un congé existant.");
        }
        // Indispos
        if (!dispoRepository.findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                formateurId, dto.getEndAt(), dto.getStartAt()).isEmpty()) {
            throw new ConflictException("Chevauche une indisponibilité existante.");
        }

        Conge entity = new Conge();
        Formateur f = formateurRepository.findById(formateurId)
            .orElseThrow(() -> new NotFoundException("Formateur introuvable: " + formateurId));
        EntityAccessUtil.setFormateur(entity, f, formateurId);

        entity.setMotif(dto.getMotif());
        entity.setStartAt(dto.getStartAt());
        entity.setEndAt(dto.getEndAt());

        return CongeMapper.toDTO(congeRepository.save(entity));
    }

    public CongeDTO update(Integer idConge, UpdateCongeDTO dto) {
        Conge entity = congeRepository.findById(idConge)
            .orElseThrow(() -> new NotFoundException("Congé introuvable: " + idConge));

        LocalDateTime start = dto.getStartAt() != null ? dto.getStartAt() : entity.getStartAt();
        LocalDateTime end   = dto.getEndAt()   != null ? dto.getEndAt()   : entity.getEndAt();
        validateRange(start, end);

        Integer formateurId = EntityAccessUtil.resolveFormateurId(entity);
        if (formateurId == null) throw new IllegalArgumentException("Congé sans formateur.");

        if (affectationRepository.existsOverlap(formateurId, start, end)) {
            throw new ConflictException("Chevauche une session affectée.");
        }
        // Exclure lui-même
        for (Conge c : congeRepository
                .findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(formateurId, end, start)) {
            Integer cid = c.getIdConge();
            if (cid != null && !cid.equals(idConge)) {
                throw new ConflictException("Chevauche un congé existant.");
            }
        }
        if (!dispoRepository.findByFormateurIdAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                formateurId, end, start).isEmpty()) {
            throw new ConflictException("Chevauche une indisponibilité existante.");
        }

        if (dto.getMotif() != null) entity.setMotif(dto.getMotif());
        entity.setStartAt(start);
        entity.setEndAt(end);

        return CongeMapper.toDTO(congeRepository.save(entity));
    }

    public void delete(Integer idConge) {
        Conge entity = congeRepository.findById(idConge)
            .orElseThrow(() -> new NotFoundException("Congé introuvable: " + idConge));
        congeRepository.delete(entity);
    }

    public List<CongeDTO> listByFormateur(Integer formateurId, LocalDateTime from, LocalDateTime to) {
        return congeRepository
            .findByFormateurIdAndStartAtBetween(formateurId, from, to)
            .stream().map(CongeMapper::toDTO).collect(Collectors.toList());
    }

    private void validateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("startAt et endAt obligatoires.");
        if (end.isBefore(start)) throw new IllegalArgumentException("endAt doit être après startAt.");
    }
}
