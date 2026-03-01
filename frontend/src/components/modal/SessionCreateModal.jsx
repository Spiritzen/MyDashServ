// src/components/modal/SessionCreateModal.jsx
import { useEffect, useMemo, useRef, useState } from "react";
import Modal from "@/components/modal/Modal";
import axiosClient from "@/api/axiosClient";

const ALLOWED_MODES = new Set(["PRESENTIEL", "DISTANCIEL", "HYBRIDE"]);
const norm = (s) => (typeof s === "string" ? s.trim() : s);
const normMode = (s, fallback = "PRESENTIEL") => {
  const v = norm(s)?.toUpperCase();
  return ALLOWED_MODES.has(v) ? v : fallback;
};

export default function SessionCreateModal({ open, onClose, onSaved }) {
  const INITIAL = useRef({
    formationId: "",
    dateDebut: "",
    dateFin: "",
    mode: "PRESENTIEL",
    ville: "",
    salle: "",
  });

  const [loading, setLoading] = useState(false);
  const [formations, setFormations] = useState([]);
  const [error, setError] = useState(null);
  const [form, setForm] = useState(INITIAL.current);

  useEffect(() => {
    if (!open) return;

    setForm(INITIAL.current);
    setError(null);

    (async () => {
      try {
        const { data } = await axiosClient.get("/api/admin/formations/options");
        setFormations(Array.isArray(data) ? data : []);
      } catch (e) {
        console.error("load formations failed", e);
        setFormations([]);
        setError("Impossible de charger les formations.");
      }
    })();
  }, [open]);

  const onChange = (e) => {
    const { name, value } = e.target;

    if (name === "formationId") {
      const fmeta = formations.find((x) => String(x.id) === String(value));
      if (fmeta) {
        setForm((prev) => ({
          ...prev,
          formationId: value,
          mode: normMode(fmeta.modeDefaut, prev.mode),
          ville: norm(fmeta.villeDefaut) || "",
          salle: norm(fmeta.salleDefaut) || "",
        }));
        return;
      }
    }

    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const canSubmit = useMemo(() => {
    if (!form.formationId || !form.dateDebut || !form.dateFin) return false;
    const sd = new Date(form.dateDebut).getTime();
    const ed = new Date(form.dateFin).getTime();
    return !Number.isNaN(sd) && !Number.isNaN(ed) && ed > sd;
  }, [form.formationId, form.dateDebut, form.dateFin]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!canSubmit) {
      setError("Vérifie la formation et les dates (fin après début).");
      return;
    }

    setLoading(true);
    try {
      await axiosClient.post("/api/admin/sessions", {
        formationId: Number(form.formationId),
        dateDebut: form.dateDebut,
        dateFin: form.dateFin,
        mode: form.mode,
        ville: form.ville || null,
        salle: form.salle || null,
      });
      setLoading(false);
      onSaved?.();
    } catch (err) {
      console.error("create session failed", err);
      setLoading(false);
      setError("Enregistrement impossible (voir console).");
    }
  };

  // non éditables ici, mais lisibles
  const readOnlyDefaults = { readOnly: true, "aria-readonly": true };

  return (
    <Modal open={open} onClose={onClose} title="Créer une nouvelle session">
      <form onSubmit={onSubmit} className="form-grid">
        {/* Message d’erreur */}
        {error && (
          <div className="alert alert--error full">
            {error}
          </div>
        )}

        {/* Formation (pleine largeur) */}
        <div className="full">
          <label>
            Formation
            <select
              className="input"
              name="formationId"
              value={form.formationId}
              onChange={onChange}
              required
            >
              <option value="">Sélectionner…</option>
              {formations.map((f) => (
                <option key={f.id} value={f.id}>
                  {f.intitule}
                </option>
              ))}
            </select>
          </label>
          <div className="help">
            La formation pré-remplit automatiquement le <b>mode</b>, le <b>lieu</b> et la <b>salle</b>.
          </div>
        </div>

        {/* Début / Fin (dans le bon ordre) */}
        <div>
          <label>
            Début
            <input
              className="input"
              type="datetime-local"
              name="dateDebut"
              value={form.dateDebut}
              onChange={onChange}
              required
            />
          </label>
        </div>

        <div>
          <label>
            Fin
            <input
              className="input"
              type="datetime-local"
              name="dateFin"
              value={form.dateFin}
              onChange={onChange}
              required
            />
          </label>
        </div>

        {/* Mode / Ville */}
        <div>
          <label>
            Mode (depuis formation)
            <select
              className="input"
              name="mode"
              value={form.mode}
              onChange={onChange}
              {...readOnlyDefaults}
            >
              <option value="PRESENTIEL">PRESENTIEL</option>
              <option value="DISTANCIEL">DISTANCIEL</option>
              <option value="HYBRIDE">HYBRIDE</option>
            </select>
          </label>
          <div className="help">Modifiable depuis la fiche formation.</div>
        </div>

        <div>
          <label>
            Lieu (depuis formation)
            <input
              className="input"
              type="text"
              name="ville"
              value={form.ville}
              onChange={onChange}
              placeholder="Paris…"
              {...readOnlyDefaults}
            />
          </label>
          <div className="help">Ex: ville par défaut de la formation.</div>
        </div>

        {/* Salle (pleine largeur, c’est plus clean visuellement) */}
        <div className="full">
          <label>
            Salle (depuis formation)
            <input
              className="input"
              type="text"
              name="salle"
              value={form.salle}
              onChange={onChange}
              placeholder="Salle 102…"
              {...readOnlyDefaults}
            />
          </label>
        </div>

        {/* Actions */}
        <div className="modal-actions full">
          <button type="button" className="btn btn--ghost" onClick={onClose}>
            Annuler
          </button>
          <button className="btn btn--primary" disabled={loading || !canSubmit}>
            {loading ? "Enregistrement…" : "Enregistrer"}
          </button>
        </div>

        {/* Note claire en bas */}
        <div className="full">
          <p className="muted" style={{ marginTop: 4 }}>
            Pour modifier <b>Mode / Lieu / Salle</b>, passe par <b>Formations</b> puis ré-ouvre cette modale.
          </p>
        </div>
      </form>
    </Modal>
  );
}
