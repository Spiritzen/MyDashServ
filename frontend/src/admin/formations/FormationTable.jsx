function Th({ field, label, sort, onSort }) {
  const [sf, sd] = (sort || "").split(",");
  const active = sf === field;
  const arrow = active ? (sd === "asc" ? "▲" : "▼") : "⇅";
  return (
    <th>
      <button className="th-sort" onClick={() => onSort(field)}>
        {label} <span className="muted">{arrow}</span>
      </button>
    </th>
  );
}

/* --- Icônes SVG (inline, pas de dépendance externe) --- */
function IconEdit() {
  return (
    <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
      <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1.003 1.003 0 0 0 0-1.42l-2.34-2.34a1.003 1.003 0 0 0-1.42 0l-1.83 1.83 3.75 3.75 1.84-1.82z" />
    </svg>
  );
}
function IconToggle({ on }) {
  return (
    <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
      <path d="M7 12a5 5 0 0 1 5-5h5a5 5 0 0 1 0 10h-5a5 5 0 0 1-5-5zm5-3a3 3 0 1 0 0 6h5a3 3 0 1 0 0-6h-5z" />
      {on ? <circle cx="17" cy="12" r="2" /> : <circle cx="10" cy="12" r="2" />}
    </svg>
  );
}

/* --- Bouton action compact : icône + libellé (label masqué en small) --- */
function ActionBtn({ title, onClick, children }) {
  return (
    <button className="btn btn--ghost btn--action" title={title} onClick={onClick}>
      {children}
      <span className="btn-label">{title}</span>
    </button>
  );
}

export default function FormationTable({ rows, sort, onSort, onEdit, onToggleActif }) {
  return (
    <div className="tbl-wrap">
      <table className="tbl">
        <thead>
          <tr>
            <Th field="intitule" label="Intitulé" sort={sort} onSort={onSort} />
            <Th field="dureeHeures" label="Durée (h)" sort={sort} onSort={onSort} />
            <Th field="format" label="Format" sort={sort} onSort={onSort} />
            <Th field="lieu" label="Lieu" sort={sort} onSort={onSort} />
            <Th field="theme" label="Thème" sort={sort} onSort={onSort} />
            <Th field="nbParticipantsMax" label="Capacité" sort={sort} onSort={onSort} />
            <th>Statut</th>
            <th className="tbl-actions">Actions</th>
          </tr>
        </thead>

        <tbody>
          {rows.length === 0 && (
            <tr>
              <td colSpan={8} className="muted" style={{ textAlign: "center" }}>
                Aucune formation trouvée.
              </td>
            </tr>
          )}

          {rows.map((r) => (
            <tr key={r.id}>
              <td data-label="Intitulé">{r.intitule}</td>
              <td data-label="Durée (h)">{r.dureeHeures ?? "—"}</td>
              <td data-label="Format">{r.format ?? "—"}</td>
              <td data-label="Lieu">{r.lieu ?? "—"}</td>
              <td data-label="Thème">{r.theme ?? "—"}</td>
              <td data-label="Capacité">{r.nbParticipantsMax ?? "—"}</td>
              <td data-label="Statut">
                {r.actif ? (
                  <span className="bdg bdg--ok">Active</span>
                ) : (
                  <span className="bdg bdg--ko">Désactivée</span>
                )}
              </td>
              <td className="tbl-actions" data-label="Actions">
                <div className="actions">
                  <ActionBtn title="Éditer" onClick={() => onEdit(r)}>
                    <IconEdit />
                  </ActionBtn>
                  <ActionBtn
                    title={r.actif ? "Désactiver" : "Activer"}
                    onClick={() => onToggleActif(r)}
                  >
                    <IconToggle on={r.actif} />
                  </ActionBtn>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
