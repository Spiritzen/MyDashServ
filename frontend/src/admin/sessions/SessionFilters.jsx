// src/admin/sessions/SessionFilters.jsx
import { useEffect, useMemo, useState, useRef } from "react";
import axiosClient from "@/api/axiosClient";
import "./SessionFilters.css";

export default function SessionFilters({
  query, onQueryChange,
  statut, onStatutChange,
  formationId, onFormationChange,
  fromDate, onFromChange,
  toDate, onToChange
}) {
  const [loading, setLoading] = useState(false);
  const [formations, setFormations] = useState([]);
  const [err, setErr] = useState(null);

  // ---- Charger les options de formations
  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      setErr(null);
      try {
        const { data } = await axiosClient.get("/api/admin/formations/options");
        if (!cancelled) setFormations(Array.isArray(data) ? data : []);
      } catch (e) {
        if (!cancelled) {
          console.error("[SessionFilters] load formations options failed", e);
          setFormations([]);
          setErr("Impossible de charger les formations.");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, []);

  // ---- Debounce pour la recherche texte
  const qTimer = useRef(null);
  const onQueryInput = (val) => {
    if (qTimer.current) clearTimeout(qTimer.current);
    qTimer.current = setTimeout(() => onQueryChange?.(val), 300);
  };

  // ---- Y a-t-il des filtres actifs ?
  const hasFilters = useMemo(() =>
    (query?.trim()?.length || 0) > 0 ||
    !!statut ||
    !!formationId ||
    !!fromDate ||
    !!toDate
  , [query, statut, formationId, fromDate, toDate]);

  // ---- Reset all
  const clearAll = () => {
    onQueryChange?.("");
    onStatutChange?.("");
    onFormationChange?.("");
    onFromChange?.("");
    onToChange?.("");
  };

  return (
    <section className="sf-card">
      <div className="sf-grid">
        {/* Recherche */}
        <label className="sf-field">
          <span className="sf-label">Recherche</span>
          <input
            className="sf-input"
            aria-label="Recherche"
            type="text"
            defaultValue={query ?? ""}
            onChange={(e) => onQueryInput(e.target.value)}
            placeholder="Intitulé formation, ville…"
            autoComplete="off"
            spellCheck={false}
          />
        </label>

        {/* Statut */}
        <label className="sf-field">
          <span className="sf-label">Statut</span>
          <select
            className="sf-input sf-select"
            aria-label="Statut"
            value={statut ?? ""}
            onChange={(e) => onStatutChange?.(e.target.value)}
          >
            <option value="">— Tous —</option>
            <option value="PLANIFIEE">Planifiée</option>
            <option value="AFFECTEE">Affectée</option>
            <option value="ANNULEE">Annulée</option>
          </select>
        </label>

        {/* Formation */}
        <label className="sf-field">
          <span className="sf-label">Formation</span>
          <select
            className="sf-input sf-select"
            aria-label="Formation"
            value={formationId ?? ""}
            onChange={(e) => onFormationChange?.(e.target.value)}
            disabled={loading || !!err}
          >
            <option value="">
              {loading ? "Chargement…" : (err ? "— Indispo —" : "— Toutes —")}
            </option>
            {formations.map((f) => (
              <option key={f.id} value={String(f.id)}>
                {f.intitule}
              </option>
            ))}
          </select>
        </label>

        {/* Du */}
        <label className="sf-field">
          <span className="sf-label">Du</span>
          <input
            className="sf-input"
            aria-label="Du"
            type="date"
            value={fromDate ?? ""}
            onChange={(e) => onFromChange?.(e.target.value)}
          />
        </label>

        {/* Au */}
        <label className="sf-field">
          <span className="sf-label">Au</span>
          <input
            className="sf-input"
            aria-label="Au"
            type="date"
            value={toDate ?? ""}
            onChange={(e) => onToChange?.(e.target.value)}
          />
        </label>

        {/* Bouton reset (aligné) */}
        <div className="sf-actions">
          {hasFilters ? (
            <button type="button" className="btn btn--ghost" onClick={clearAll}>
              Réinitialiser
            </button>
          ) : (
            <span />
          )}

          {err ? <span className="sf-error">{err}</span> : null}
        </div>
      </div>
    </section>
  );
}
