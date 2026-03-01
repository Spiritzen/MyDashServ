import { useState } from "react";
import axiosClient from "@/api/axiosClient";              // ✅ chemin correct
import Modal from "@/components/modal/Modal";             // ✅ dossier 'modal' en minuscule

const INIT = {
  intitule: "",
  objectifs: "",
  dureeHeures: "",
  nbParticipantsMax: "",
  format: "PRESENTIEL",
  lieu: "",
  theme: "",
  actif: true
};

export default function NewFormationModal({ open, onClose, onCreated }) {
  const [v, setV] = useState(INIT);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState(null);

  const set = (k) => (e) =>
    setV((s) => ({
      ...s,
      [k]: e.target.type === "checkbox" ? e.target.checked : e.target.value,
    }));

  const submit = async (e) => {
    e.preventDefault();
    setErr(null);

    // mini validation
    if (!v.intitule?.trim()) { setErr("L’intitulé est requis."); return; }
    if (v.dureeHeures && Number(v.dureeHeures) < 0) { setErr("Durée invalide."); return; }
    if (v.nbParticipantsMax && Number(v.nbParticipantsMax) < 0) { setErr("Capacité invalide."); return; }

    try {
      setLoading(true);
      const payload = {
        intitule: v.intitule.trim(),
        objectifs: v.objectifs?.trim() || null,
        dureeHeures: v.dureeHeures ? Number(v.dureeHeures) : null,
        nbParticipantsMax: v.nbParticipantsMax ? Number(v.nbParticipantsMax) : null,
        format: v.format || null, // PRESENTIEL / DISTANCIEL
        lieu: v.lieu?.trim() || null,
        theme: v.theme?.trim() || null,
        actif: v.actif,
      };
      const { data } = await axiosClient.post("/api/admin/formations", payload);
      onCreated?.(data);
      setV(INIT);
      onClose?.();
    } catch (e) {
      console.error(e);
      setErr(e?.response?.data?.message || "Échec de la création.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={loading ? undefined : onClose}
      title="Nouvelle formation"
      footer={
        <>
          <button className="btn btn--ghost" onClick={onClose} disabled={loading}>Annuler</button>
          <button className="btn btn--primary" onClick={submit} disabled={loading}>
            {loading ? "Enregistrement..." : "Créer"}
          </button>
        </>
      }
    >
      <form className="form-grid" onSubmit={submit}>
        <div className="full">
          <label>Intitulé</label>
          <input
            className="input"
            value={v.intitule}
            onChange={set("intitule")}
            placeholder="ex: Excel – Niveau Intermédiaire"
            required
          />
        </div>

        <div>
          <label>Durée (heures)</label>
          <input
            className="input"
            type="number" min="0"
            value={v.dureeHeures}
            onChange={set("dureeHeures")}
            placeholder="ex: 14"
          />
        </div>

        <div>
          <label>Capacité max</label>
          <input
            className="input"
            type="number" min="0"
            value={v.nbParticipantsMax}
            onChange={set("nbParticipantsMax")}
            placeholder="ex: 12"
          />
        </div>

        <div>
          <label>Format</label>
          <select className="input" value={v.format} onChange={set("format")}>
            <option value="PRESENTIEL">Présentiel</option>
            <option value="DISTANCIEL">Distanciel</option>
          </select>
          
        </div>

        <div>
          <label>Lieu</label>
          <input className="input" value={v.lieu} onChange={set("lieu")} placeholder="ex: Paris 15e" />
        </div>

        <div>
          <label>Thème</label>
          <input className="input" value={v.theme} onChange={set("theme")} placeholder="ex: Bureautique" />
        </div>

        <div className="full">
          <label>Objectifs</label>
          <textarea className="input" rows={4} value={v.objectifs} onChange={set("objectifs")} placeholder="Objectifs pédagogiques..." />
        </div>

        <div className="full" style={{display:"flex",alignItems:"center",gap:10}}>
          <input id="actif" type="checkbox" checked={v.actif} onChange={set("actif")} />
          <label htmlFor="actif">Activer la formation dès maintenant</label>
        </div>

        {err && <div className="auth-error full">{err}</div>}
      </form>
    </Modal>
  );
}
