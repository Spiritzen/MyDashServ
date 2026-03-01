package com.afci.training.planning.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.afci.training.planning.dto.FormationDTO;
import com.afci.training.planning.dto.FormationUpdateDTO;
import com.afci.training.planning.entity.Formation;
import com.afci.training.planning.repository.FormationRepository;
import com.afci.training.planning.util.FormationMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
@Transactional
public class FormationService {

    private final FormationRepository repo;

    public FormationService(FormationRepository repo) {
        this.repo = repo;
    }

    public FormationDTO create(@Valid FormationDTO dto){
        Formation f = FormationMapper.fromDTO(dto);
        f.setIdFormation(null);
        f = repo.save(f);
        return FormationMapper.toDTO(f);
    }

    @Transactional(readOnly = true)
    public Page<FormationDTO> findAll(String q, Pageable pageable){
        Page<Formation> page = (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByIntituleContainingIgnoreCase(q.trim(), pageable);
        return page.map(FormationMapper::toDTO);
    }
    @Transactional(readOnly = true)
    public FormationDTO findById(Integer id){
        Formation f = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Formation "+id+" introuvable"));
        return FormationMapper.toDTO(f);
    }

    public FormationDTO update(Integer id, FormationUpdateDTO dto){
        var f = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Formation "+id+" introuvable"));
        // maj partielle
        if (dto.getIntitule()!=null) f.setIntitule(dto.getIntitule());
        if (dto.getObjectifs()!=null) f.setObjectifs(dto.getObjectifs());
        if (dto.getDureeHeures()!=null) f.setDureeHeures(dto.getDureeHeures());
        if (dto.getNbParticipantsMax()!=null) f.setNbParticipantsMax(dto.getNbParticipantsMax());
        if (dto.getFormat()!=null) f.setFormat(Formation.Format.valueOf(dto.getFormat()));
        if (dto.getLieu()!=null) f.setLieu(dto.getLieu());
        if (dto.getTheme()!=null) f.setTheme(dto.getTheme());
        if (dto.getActif()!=null) f.setActif(dto.getActif());
        return FormationMapper.toDTO(f);
    }
    public void delete(Integer id){
        if (!repo.existsById(id)) throw new EntityNotFoundException("Formation "+id+" introuvable");
        repo.deleteById(id);
    }
}
