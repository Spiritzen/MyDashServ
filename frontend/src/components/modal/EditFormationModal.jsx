import { useEffect, useMemo, useState } from "react";
import axiosClient from "@/api/axiosClient";
import Modal from "@/components/modal/Modal";

/**
 * Modale d'édition d'une formation.
 * Props:
 *  - open: bool
 *  - row:  FormationDTO (id, intitule, objectifs, dureeHeures, nbParticipantsMax, format, lieu, theme, actif)
 *  - onClose: fn
 *  - onUpdated: fn(updatedDto) -> callback après succès
 */
export default function EditFormationModal({ open, row, onClose, onUpdated }) {
  const original = useMemo(() => row ?? {}, [row]);

  const [v, setV] = useState({
    intitule: "",
    objectifs: "",
    dureeHeures: "",
    nbParticipantsMax: "",
    format: "PRESENTIEL",
    lieu: "",
    theme: "",
    actif: true,
  });
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState(null);

  useEffect(() => {
    if (!open || !row) return;
    // Pré-remplissage depuis la ligne reçue
    setV({
      intitule: row.intitule ?? "",
      objectifs: row.objectifs ?? "",
      dureeHeures: row.dureeHeures ?? "",
      nbParticipantsMax: row.nbParticipantsMax ?? "",
      format: row.format ?? "PRESENTIEL",
      lieu: row.lieu ?? "",
      theme: row.theme ?? "",
      actif: !!row.actif,
    });
    setErr(null);
  }, [open, row]);

  const set = (k) => (e) =>
    setV((s) => ({
      ...s,
      [k]: e.target.type === "checkbox" ? e.target.checked : e.target.value,
    }));

  // Ne renvoyer QUE les champs modifiés (DTO partiel)
  const buildUpdatePayload = () => {
    const payload = {};

    // strings : on envoie même "" si on veut explicitement vider la valeur
    const strKeys = ["intitule", "objectifs", "lieu", "theme", "format"];
    strKeys.forEach((k) => {
      const cur = v[k] ?? "";
      const prev = original[k] ?? "";
      if (cur !== prev) payload[k] = cur === "" ? "" : cur;
    });

    // nombres : on envoie uniquement si la valeur change réellement
    const numPairs = [
      ["dureeHeures", "dureeHeures"],
      ["nbParticipantsMax", "nbParticipantsMax"],
    ];
    numPairs.forEach(([k]) => {
      const cur = v[k] === "" ? "" : Number(v[k]);
      const prev = original[k] ?? "";
      // si champ vidé -> on envoie "" pour forcer le vidage, sinon nombre
      if (cur !== prev) payload[k] = v[k] === "" ? "" : Number(v[k]);
    });

    // bool
    if (Boolean(v.actif) !== Boolean(original.actif)) {
      payload.actif = Boolean(v.actif);
    }

    // IMPORTANT: côté service, seuls les champs !== null sont pris en compte.
    // Ici on n’envoie pas 'null' ; on omet pour "pas de changement".
    return payload;
  };

  const submit = async (e) => {
    e?.preventDefault?.();
    if (!original?.id) return;
    setErr(null);

    // mini validation
    if (!v.intitule?.trim()) { setErr("L’intitulé est requis."); return; }
    if (v.dureeHeures !== "" && Number(v.dureeHeures) < 0) { setErr("Durée invalide."); return; }
    if (v.nbParticipantsMax !== "" && Number(v.nbParticipantsMax) < 0) { setErr("Capacité invalide."); return; }

    const dto = buildUpdatePayload();
    if (Object.keys(dto).length === 0) { onClose?.(); return; } // rien à modifier

    try {
      setLoading(true);
      const { data } = await axiosClient.put(`/api/admin/formations/${original.id}`, dto);
      onUpdated?.(data);
      onClose?.();
    } catch (e2) {
      console.error(e2);
      setErr(e2?.response?.data?.message || "Échec de la mise à jour.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={loading ? undefined : onClose}
      title={`Éditer la formation${original?.intitule ? ` — ${original.intitule}` : ""}`}
      footer={
        <>
          <button className="btn btn--ghost" onClick={onClose} disabled={loading}>Annuler</button>
          <button className="btn btn--primary" onClick={submit} disabled={loading}>
            {loading ? "Enregistrement..." : "Enregistrer"}
          </button>
        </>
      }
    >
      <form className="form-grid" onSubmit={submit}>
        <div className="full">
          <label>Intitulé</label>
          <input className="input" value={v.intitule} onChange={set("intitule")} required />
        </div>

        <div>
          <label>Durée (heures)</label>
          <input className="input" type="number" min="0" value={v.dureeHeures} onChange={set("dureeHeures")} />
        </div>

        <div>
          <label>Capacité max</label>
          <input className="input" type="number" min="0" value={v.nbParticipantsMax} onChange={set("nbParticipantsMax")} />
        </div>

        <div>
          <label>Format</label>
          <select className="input" value={v.format} onChange={set("format")}>
            <option value="PRESENTIEL">Présentiel</option>
            <option value="DISTANCIEL">Distanciel</option>
          </select>
          <div className="help">Enum backend : PRESENTIEL / DISTANCIEL.</div>
        </div>

        <div>
          <label>Lieu</label>
          <input className="input" value={v.lieu} onChange={set("lieu")} />
        </div>

        <div>
          <label>Thème</label>
          <input className="input" value={v.theme} onChange={set("theme")} />
        </div>

        <div className="full">
          <label>Objectifs</label>
          <textarea className="input" rows={4} value={v.objectifs} onChange={set("objectifs")} />
        </div>

        <div className="full" style={{display:"flex",alignItems:"center",gap:10}}>
          <input id="actif_edit" type="checkbox" checked={v.actif} onChange={set("actif")} />
          <label htmlFor="actif_edit">Activer la formation</label>
        </div>

        {err && <div className="auth-error full">{err}</div>}
      </form>
    </Modal>
  );
}
