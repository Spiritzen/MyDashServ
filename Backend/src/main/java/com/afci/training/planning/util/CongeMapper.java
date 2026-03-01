package com.afci.training.planning.util;

import com.afci.training.planning.dto.CongeDTO;
import com.afci.training.planning.entity.Conge;

public class CongeMapper {

    public static CongeDTO toDTO(Conge entity) {
        if (entity == null) return null;

        // id congé : préfère getIdConge(), sinon tente getId()
        Integer idConge = EntityAccessUtil.getIdGeneric(entity, "getIdConge", "getId");

        // formateurId : via util (gère champ direct OU relation Formateur -> idXxx)
        Integer formateurId = EntityAccessUtil.resolveFormateurId(entity);

        CongeDTO dto = new CongeDTO(
            idConge,
            formateurId,
            entity.getMotif(),
            entity.getStartAt(),
            entity.getEndAt()
        );
        return dto;
    }
}
