package com.afci.training.planning.util;

import com.afci.training.planning.dto.DisponibiliteDTO;
import com.afci.training.planning.entity.Disponibilite;

public class DisponibiliteMapper {

    public static DisponibiliteDTO toDTO(Disponibilite entity) {
        if (entity == null) return null;

        // id dispo : préfère getIdDisponibilite(), sinon tente getId()
        Integer idDispo = EntityAccessUtil.getIdGeneric(entity, "getIdDisponibilite", "getId");

        // formateurId : via util (champ direct OU relation)
        Integer formateurId = EntityAccessUtil.resolveFormateurId(entity);

        // récurrence : si enum -> name(), si String -> la valeur, sinon "AUCUNE"
        String recurrence = "AUCUNE";
        try {
            Object r = entity.getRecurrence();
            if (r != null) {
                recurrence = (r instanceof Enum) ? ((Enum<?>) r).name() : r.toString();
            }
        } catch (Exception ignore) {}

        return new DisponibiliteDTO(
            idDispo,
            formateurId,
            entity.getCommentaire(),
            recurrence,
            entity.getStartAt(),
            entity.getEndAt()
        );
    }
}
