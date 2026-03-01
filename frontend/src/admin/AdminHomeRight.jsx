export default function AdminHomeRight() {
  return (
    <div className="admin-dash">
      <header className="admin-dash__head">
        <h1>Tableau de bord</h1>
        <p className="muted">Aperçu global de l’activité (placeholders — à brancher plus tard).</p>
      </header>

      <div className="admin-kpi-grid">
        <article className="card kpi">
          <div className="kpi__label">Sessions à venir</div>
          <div className="kpi__value">—</div>
          <div className="kpi__hint muted">7 prochains jours</div>
        </article>

        <article className="card kpi">
          <div className="kpi__label">Affectations en attente</div>
          <div className="kpi__value">—</div>
          <div className="kpi__hint muted">à valider</div>
        </article>

        <article className="card kpi">
          <div className="kpi__label">Alertes actives</div>
          <div className="kpi__value">—</div>
          <div className="kpi__hint muted">conflits / indispos</div>
        </article>

        <article className="card kpi">
          <div className="kpi__label">Heures planifiées (mois)</div>
          <div className="kpi__value">— h</div>
          <div className="kpi__hint muted">tous formateurs</div>
        </article>
      </div>

      <div className="card admin-dash__panel">
        <h2>Dernières alertes</h2>
        <ul className="list--striped muted">
          <li>(Aucune alerte pour l’instant)</li>
        </ul>
      </div>
    </div>
  );
}
