export default function FormationFilters({ value, onChange }) {
  const v = value;
  const set = (k, val) => onChange({ [k]: val });

  return (
    <div className="filters">
      <input
        className="input"
        placeholder="Recherche (intitulé)…"
        value={v.search}
        onChange={(e) => set("search", e.target.value)}
      />

      <select className="input" value={v.actif} onChange={(e) => set("actif", e.target.value)}>
        <option value="">Tous statuts</option>
        <option value="true">Actives</option>
        <option value="false">Désactivées</option>
      </select>

      <select className="input" value={v.format} onChange={(e) => set("format", e.target.value)}>
        <option value="">Tous formats</option>
        <option value="PRESENTIEL">Présentiel</option>
        <option value="DISTANCIEL">Distanciel</option>
      </select>

      <input
        className="input"
        placeholder="Thème"
        value={v.theme}
        onChange={(e) => set("theme", e.target.value)}
      />
      <input
        className="input"
        placeholder="Lieu"
        value={v.lieu}
        onChange={(e) => set("lieu", e.target.value)}
      />

      <button
        className="btn btn--ghost"
        onClick={() =>
          onChange({
            search: "",
            actif: "",
            format: "",
            theme: "",
            lieu: "",
          })
        }
      >
        Réinitialiser
      </button>
    </div>
  );
}
