// src/components/admin/affectations/AffectationsFilter.jsx
import "./AffectationsFilter.css";

export default function AffectationsFilter({ q, setQ, from, setFrom, to, setTo, onReset }) {
  const has = !!(q?.trim() || from || to);

  return (
    <section className="af-filters card">
      <div className="af-filters__grid">
        <label className="af-field">
          <span className="af-label">Recherche</span>
          <input
            className="af-input"
            type="text"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Intitulé formation, ville…"
          />
        </label>

        <label className="af-field">
          <span className="af-label">Du</span>
          <input
            className="af-input"
            type="date"
            value={from}
            onChange={(e) => setFrom(e.target.value)}
          />
        </label>

        <label className="af-field">
          <span className="af-label">Au</span>
          <input
            className="af-input"
            type="date"
            value={to}
            onChange={(e) => setTo(e.target.value)}
          />
        </label>

        <div className="af-actions">
          {has && (
            <button className="btn btn--ghost af-reset" onClick={onReset}>
              Réinitialiser
            </button>
          )}
        </div>
      </div>
    </section>
  );
}
