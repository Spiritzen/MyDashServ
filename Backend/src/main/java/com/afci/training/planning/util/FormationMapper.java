package com.afci.training.planning.util;

import com.afci.training.planning.dto.FormationDTO;
import com.afci.training.planning.entity.Formation;

public final class FormationMapper {
    private FormationMapper(){}

    public static FormationDTO toDTO(Formation f){
        if (f == null) return null;
        return new FormationDTO(
            f.getIdFormation(),
            f.getIntitule(),
            f.getObjectifs(),
            f.getDureeHeures(),
            f.getNbParticipantsMax(),
            (f.getFormat()!=null? f.getFormat().name() : null),
            f.getLieu(),
            f.getTheme(),
            f.getActif()
        );
    }

    public static Formation fromDTO(FormationDTO d){
        if (d == null) return null;
        Formation f = new Formation();
        f.setIdFormation(d.getId());
        f.setIntitule(d.getIntitule());
        f.setObjectifs(d.getObjectifs());
        f.setDureeHeures(d.getDureeHeures());
        f.setNbParticipantsMax(d.getNbParticipantsMax());
        if (d.getFormat()!=null) {
            f.setFormat(Formation.Format.valueOf(d.getFormat())); // veille à envoyer PRESENTIEL/DISTANCIEL
        }
        f.setLieu(d.getLieu());
        f.setTheme(d.getTheme());
        f.setActif(d.getActif()!=null ? d.getActif() : Boolean.TRUE);
        return f;
    }

    public static void copyToEntity(FormationDTO d, Formation f){
        if (d.getIntitule()!=null) f.setIntitule(d.getIntitule());
        if (d.getObjectifs()!=null) f.setObjectifs(d.getObjectifs());
        if (d.getDureeHeures()!=null) f.setDureeHeures(d.getDureeHeures());
        if (d.getNbParticipantsMax()!=null) f.setNbParticipantsMax(d.getNbParticipantsMax());
        if (d.getFormat()!=null) f.setFormat(Formation.Format.valueOf(d.getFormat()));
        if (d.getLieu()!=null) f.setLieu(d.getLieu());
        if (d.getTheme()!=null) f.setTheme(d.getTheme());
        if (d.getActif()!=null) f.setActif(d.getActif());
    }
}
