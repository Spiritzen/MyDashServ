// src/formateur/FormateurHomeRight.jsx
import { useMemo } from "react";

export default function FormateurHomeRight() {
  // placeholders (à remplacer par fetchs /api/formateurs/me et /api/users/me)
  const kpis = useMemo(() => ([
    { label: "Prochaines sessions", value: "—", hint: "7 prochains jours" },
    { label: "Heures (ce mois)", value: "— h", hint: "planifiées" },
    { label: "État candidature", value: "—", hint: "à valider" },
    { label: "Alertes", value: "—", hint: "conflits / indispos" },
  ]), []);

  return (
    <div className="form-dash">
      <header className="form-dash__head">
        <h1>Mon tableau de bord</h1>
        <p className="muted">Aperçu rapide de votre activité et de votre candidature.</p>
      </header>

      <div className="form-kpi-grid">
        {kpis.map((k) => (
          <article key={k.label} className="card kpi">
            <div className="kpi__label">{k.label}</div>
            <div className="kpi__value">{k.value}</div>
            <div className="kpi__hint muted">{k.hint}</div>
          </article>
        ))}
      </div>

      <div className="card panel">
        <h2 style={{ marginTop: 0 }}>À faire</h2>
        <ul className="list--striped muted">
          <li>Compléter mon profil et mes compétences</li>
          <li>Renseigner mes disponibilités et congés</li>
          <li>Soumettre ma candidature pour validation</li>
        </ul>
      </div>
    </div>
  );
}
