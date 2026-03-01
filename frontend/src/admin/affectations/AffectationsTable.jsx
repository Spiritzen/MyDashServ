import "./AffectationsRight.css";

const fmtDateTime = (isoOrDate) => {
  if (!isoOrDate) return "";
  // si tu reçois déjà "2025-11-03T16:00:00", ça marche
  const d = new Date(isoOrDate);
  if (Number.isNaN(d.getTime())) return String(isoOrDate);
  return d.toLocaleString("fr-FR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

export default function AffectationsTable({ rows, loading, onMatch }) {
  if (loading) {
    return (
      <div className="aff-empty">
        <div className="aff-spinner" />
        <p>Chargement des sessions…</p>
      </div>
    );
  }

  if (!rows?.length) {
    return (
      <div className="aff-empty">
        <p>Aucune session à affecter pour les filtres actuels.</p>
      </div>
    );
  }

  return (
    <div className="aff-grid">
      {rows.map((s) => {
        // adapte si tes noms de champs diffèrent
        const debut = fmtDateTime(s.dateDebut);
        const fin = fmtDateTime(s.dateFin);
        const formation = s.formationIntitule ?? s.formationLabel ?? s.formation ?? "—";
        const ville = s.ville ?? "—";
        const salle = s.salle ?? "";
        const lieu = salle ? `${ville} • ${salle}` : ville;

        return (
          <article key={s.idSession ?? `${s.dateDebut}-${formation}`} className="aff-card">
            <div className="aff-card-top">
              <div className="aff-badge">À affecter</div>
              <div className="aff-dates">
                <div><span>Début</span> {debut}</div>
                <div><span>Fin</span> {fin}</div>
              </div>
            </div>

            <div className="aff-card-body">
              <h3 className="aff-formation">{formation}</h3>
              <p className="aff-lieu">{lieu}</p>
            </div>

            <div className="aff-card-actions">
              <button
                className="aff-btn"
                onClick={() => onMatch?.(s)}
                type="button"
              >
                Affecter un formateur
              </button>
            </div>
          </article>
        );
      })}
    </div>
  );
}
