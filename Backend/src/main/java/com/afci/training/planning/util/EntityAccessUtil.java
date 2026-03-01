package com.afci.training.planning.util;

import java.lang.reflect.Method;

public final class EntityAccessUtil {

    private EntityAccessUtil() {}

    // Lecture d'un Integer id via plusieurs méthodes candidates
    public static Integer getIdGeneric(Object target, String... methodNames) {
        if (target == null) return null;
        for (String m : methodNames) {
            try {
                Method mm = target.getClass().getMethod(m);
                Object val = mm.invoke(target);
                if (val instanceof Integer) return (Integer) val;
            } catch (Exception ignore) {}
        }
        return null;
    }

    // Récupérer le formateurId depuis une entité (Conge/Disponibilite)
    public static Integer resolveFormateurId(Object entity) {
        if (entity == null) return null;

        // 1) champ direct getFormateurId()
        Integer id = getIdGeneric(entity, "getFormateurId");
        if (id != null) return id;

        // 2) relation getFormateur() puis id*()
        try {
            Method gm = entity.getClass().getMethod("getFormateur");
            Object formateur = gm.invoke(entity);
            if (formateur != null) {
                id = getIdGeneric(formateur, "getId", "getIdFormateur", "getIdUtilisateur");
                if (id != null) return id;
            }
        } catch (Exception ignore) {}

        return null;
    }

    // Setter générique pour poser un formateur sur l'entité (relation ou id)
    public static void setFormateur(Object entity, Object formateur, Integer formateurId) {
        if (entity == null) return;

        // 1) relation setFormateur(Formateur)
        if (formateur != null) {
            try {
                Method sm = entity.getClass().getMethod("setFormateur", formateur.getClass());
                sm.invoke(entity, formateur);
                return;
            } catch (Exception ignore) {}
        }
        // 2) champ id : setFormateurId(Integer)
        if (formateurId != null) {
            try {
                Method sm = entity.getClass().getMethod("setFormateurId", Integer.class);
                sm.invoke(entity, formateurId);
            } catch (Exception ignore) {}
        }
    }

 // Récurrence: AUCUNE | HEBDO | MENSUELLE
 // - Si String: on stocke toujours une valeur non nulle (par défaut "AUCUNE")
 // - Si Enum: on mappe vers l'enum, en privilégiant la constante AUCUNE si value est vide
 public static void setRecurrence(Object entity, String value) {
     if (entity == null) return;

     // normalisation
     String norm = (value == null || value.trim().isEmpty())
             ? "AUCUNE"
             : value.trim().toUpperCase();

     // Chercher setRecurrence(...)
     Method[] methods = entity.getClass().getMethods();
     for (Method m : methods) {
         if (!"setRecurrence".equals(m.getName()) || m.getParameterCount() != 1) continue;
         Class<?> p = m.getParameterTypes()[0];

         try {
             if (p == String.class) {
                 // Toujours une String non nulle (respecte @NotNull)
                 m.invoke(entity, norm);
                 return;
             }
             if (p.isEnum()) {
                 @SuppressWarnings({ "rawtypes", "unchecked" })
                 Class<Enum> enumType = (Class<Enum>) p;
                 // Tente la valeur fournie; sinon fallback "AUCUNE" si existante
                 try {
                     Object enumVal = Enum.valueOf(enumType, norm);
                     m.invoke(entity, enumVal);
                     return;
                 } catch (IllegalArgumentException e) {
                     try {
                         Object enumAucune = Enum.valueOf(enumType, "AUCUNE");
                         m.invoke(entity, enumAucune);
                         return;
                     } catch (IllegalArgumentException e2) {
                         // dernier recours : première constante de l'enum (évite null si @NotNull)
                         Object[] constants = enumType.getEnumConstants();
                         if (constants != null && constants.length > 0) {
                             m.invoke(entity, constants[0]);
                             return;
                         }
                     }
                 }
             }
         } catch (Exception ignore) {}
     }
     // Si aucun setter trouvé, on ne fait rien (pas de champ de récurrence)
 }
}