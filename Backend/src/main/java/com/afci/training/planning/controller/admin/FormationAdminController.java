// src/main/java/com/afci/training/planning/controller/admin/FormationAdminController.java
package com.afci.training.planning.controller.admin;

import com.afci.training.planning.dto.FormationOptionDTO;
import com.afci.training.planning.entity.Formation;
import com.afci.training.planning.repository.FormationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

@RestController
@RequestMapping("/api/admin/formations")
@PreAuthorize("hasAnyRole('ADMIN','GESTIONNAIRE')")
public class FormationAdminController {

    private final FormationRepository repo;

    public FormationAdminController(FormationRepository repo) {
        this.repo = repo;
    }

    @GetMapping(value = "/options", produces = "application/json")
    public ResponseEntity<List<FormationOptionDTO>> listOptions() {
        List<Formation> list = hasOrderByIntitule(repo)
                ? repo.findAllByOrderByIntituleAsc()
                : repo.findAll();

        List<FormationOptionDTO> dto = list.stream()
            .map(f -> new FormationOptionDTO(
                    getIdFormation(f),
                    ns(getIntitule(f)),
                    ns(getModeDefaut(f)),
                    ns(getVilleDefaut(f)),
                    ns(getSalleDefaut(f))
            ))
            .toList();

        return ResponseEntity.ok(dto);
    }

    // ---------- Helpers réfléchis & sûrs ----------

    private static Integer getIdFormation(Formation f) {
        Object id = callIfExists(f, "getIdFormation");
        if (id == null) id = callIfExists(f, "getId");
        if (id instanceof Number n) return n.intValue();
        return null;
    }

    private static String getIntitule(Formation f) {
        return str(callIfExists(f, "getIntitule"));
    }

    // format peut être String ou Enum -> str()
    private static String getModeDefaut(Formation f) {
        Object val = callIfExists(f, "getModeDefaut");
        if (val == null) val = callIfExists(f, "getMode");
        if (val == null) val = callIfExists(f, "getFormat"); // fallback
        return str(val);
    }

    // ville peut s’appeler ville ou lieu
    private static String getVilleDefaut(Formation f) {
        Object val = callIfExists(f, "getVilleDefaut");
        if (val == null) val = callIfExists(f, "getVille");
        if (val == null) val = callIfExists(f, "getLieu");   // fallback
        return str(val);
    }

    private static String getSalleDefaut(Formation f) {
        Object val = callIfExists(f, "getSalleDefaut");
        if (val == null) val = callIfExists(f, "getSalle");
        return str(val);
    }

    private static boolean hasOrderByIntitule(FormationRepository repo) {
        try { repo.getClass().getMethod("findAllByOrderByIntituleAsc"); return true; }
        catch (NoSuchMethodException e) { return false; }
    }

    /** Convertit tout en String proprement (gère Enum/Number/etc.). */
    private static String str(Object v) {
        if (v == null) return null;
        if (v instanceof Enum<?> e) return e.name();
        return String.valueOf(v);
    }

    private static String ns(String v) { return v == null ? "" : v; }

    private static Object callIfExists(Object target, String getter) {
        try {
            Method m = target.getClass().getMethod(getter);
            return m.invoke(target);
        } catch (NoSuchMethodException ex) {
            return null; // getter absent -> null
        } catch (Exception ex) {
            // on ne jette pas d’exception pour éviter les 500 : on renvoie null
            return null;
        }
    }
}
