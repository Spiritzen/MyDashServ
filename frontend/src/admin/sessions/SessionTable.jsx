function fmt(dt) {
  if (!dt) return "—";
  const d = new Date(dt);
  return d.toLocaleString();
}

function Badge({ statut }) {
  const map = {
    PLANIFIEE: { label: "Planifiée", cls: "bdg--plan" },
    AFFECTEE:  { label: "Affectée",  cls: "bdg--ok" },
    ANNULEE:   { label: "Annulée",   cls: "bdg--ko" },
  };
  const it = map[statut] || { label: statut || "—", cls: "" };
  return <span className={`bdg ${it.cls}`}>{it.label}</span>;
}

export default function SessionTable({ rows, onRowClick, onEdit, onCancel }) {
  const handleRow = (id) => {
    onRowClick?.(id);
  };
  const handleEdit = (e, id) => {
    e.stopPropagation();
    onEdit?.(id);
  };
  const handleCancel = (e, id) => {
    e.stopPropagation();
    onCancel?.(id);
  };

  return (
    <div className="table-wrap">
      <table className="table">
        <thead>
          <tr>
            <th>Début</th>
            <th>Fin</th>
            <th>Formation</th>
            <th>Mode</th>
            <th>Statut</th>
            <th>Formateur</th>
            <th>Ville/Salle</th>
            <th style={{width:160}}></th>
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 && (
            <tr><td colSpan={8} className="muted">Aucune session</td></tr>
          )}
          {rows.map(r => (
            <tr key={r.id} className="row-clickable" onClick={() => handleRow(r.id)}>
              <td>{fmt(r.dateDebut)}</td>
              <td>{fmt(r.dateFin)}</td>
              <td>{r.formationLabel || "—"}</td>
              <td>{r.mode || "—"}</td>
              <td><Badge statut={r.statut} /></td>
              <td>{r.formateurLabel || "—"}</td>
              <td>{r.ville || "—"}{r.salle ? ` / ${r.salle}` : ""}</td>
              <td className="tbl-actions">
                <button className="btn btn--ghost" onClick={(e) => handleEdit(e, r.id)}>Éditer</button>
                <button className="btn btn--ghost" onClick={(e) => handleCancel(e, r.id)}>Annuler</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <style>{`
        .row-clickable { cursor: pointer; }
        .row-clickable:hover { background: rgba(255,255,255,0.03); }
      `}</style>
    </div>
  );
}
