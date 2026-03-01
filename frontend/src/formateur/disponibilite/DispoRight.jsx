// src/formateur/disponibilite/DispoRight.jsx
import { useCallback, useEffect, useMemo, useState } from "react";

/* ---------- UI helpers ---------- */
function Section({ title, actions, children }) {
  return (
    <div className="card panel mb-16">
      <div className="section-head">
        <h2 className="section-title">{title}</h2>
        <div>{actions}</div>
      </div>
      <div className="mt-12">{children}</div>
    </div>
  );
}

function Tabs({ value, onChange, items }) {
  return (
    <div className="tabs">
      {items.map((t) => (
        <button
          key={t.value}
          className={`btn btn--light ${value === t.value ? "is-active" : ""}`}
          onClick={() => onChange(t.value)}
          type="button"
        >
          {t.label}
        </button>
      ))}
    </div>
  );
}

function DateRangePicker({ from, to, onChange }) {
  return (
    <div className="range">
      <label className="muted">
        De&nbsp;
        <input
          className="input input--light"
          type="datetime-local"
          value={from}
          onChange={(e) => onChange({ from: e.target.value, to })}
        />
      </label>
      <label className="muted">
        À&nbsp;
        <input
          className="input input--light"
          type="datetime-local"
          value={to}
          onChange={(e) => onChange({ from, to: e.target.value })}
        />
      </label>
    </div>
  );
}

function Legend() {
  return (
    <div className="legend">
      <span><span className="dot dot--session" /> Session</span>
      <span><span className="dot dot--conge" /> Congé</span>
      <span><span className="dot dot--indispo" /> Indisponibilité</span>
    </div>
  );
}

/* ---------- API helpers ---------- */
const API_PREFIX = (import.meta.env && import.meta.env.VITE_API_URL) || "http://localhost:8080";

function useAuthHeaders() {
  return useMemo(() => {
    // essaie plusieurs noms de clés possibles, dont 'auth_token'
    const keys = ["auth_token", "token", "authToken", "jwt", "jwtToken", "access_token"];
    const raw = keys.map((k) => localStorage.getItem(k)).find(Boolean) || "";
    const Authorization = raw ? (raw.startsWith("Bearer ") ? raw : `Bearer ${raw}`) : "";
    return { "Content-Type": "application/json", Authorization };
  }, []);
}

const qs = (obj) =>
  Object.entries(obj)
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`)
    .join("&");

/* ---------- Dates helpers ---------- */
function toInputLocal(d) {
  // convertit une Date en "YYYY-MM-DDTHH:mm" en gardant l'heure locale
  const local = new Date(d.getTime() - d.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 16);
}

function emptyConge(formateurId) {
  const now = new Date();
  const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 9, 0, 0);
  const end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 18, 0, 0);
  return { idConge: null, formateurId, motif: "", startAt: toInputLocal(start), endAt: toInputLocal(end) };
}

function emptyDispo(formateurId) {
  const now = new Date();
  const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 9, 0, 0);
  const end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 12, 0, 0);
  return { idDisponibilite: null, formateurId, commentaire: "", recurrence: "AUCUNE", startAt: toInputLocal(start), endAt: toInputLocal(end) };
}

export default function DispoRight() {
  const headers = useAuthHeaders();

  // Formatter FR/Paris pour affichage local
  const dtf = useMemo(
    () =>
      new Intl.DateTimeFormat("fr-FR", {
        dateStyle: "short",
        timeStyle: "short",
        timeZone: "Europe/Paris",
      }),
    []
  );
  const fmtLocal = useCallback(
    (dt) => {
      if (dt == null || dt === "") return "";
      const d = new Date(dt);
      if (Number.isNaN(d.getTime())) return String(dt);
      return dtf.format(d);
    },
    [dtf]
  );

  const [tab, setTab] = useState("agenda"); // agenda | conges | indispos
  const [me, setMe] = useState(null);
  const [range, setRange] = useState(() => {
    const d = new Date();
    const from = new Date(d.getFullYear(), d.getMonth(), 1, 0, 0, 0);
    const to = new Date(d.getFullYear(), d.getMonth() + 1, 0, 23, 59, 59);
    return { from: toInputLocal(from), to: toInputLocal(to) };
  });

  const [agenda, setAgenda] = useState([]);
  const [conges, setConges] = useState([]);
  const [dispos, setDispos] = useState([]);

  const [formConge, setFormConge] = useState(null);
  const [formDispo, setFormDispo] = useState(null);

  // utilitaire query ISO (ajoute :00 si besoin)
  const toQueryIso = useCallback((dtLocal) => (dtLocal && dtLocal.length === 16 ? `${dtLocal}:00` : dtLocal), []);

  // récupère le formateur courant
  useEffect(() => {
    fetch(`${API_PREFIX}/api/formateurs/me`, { headers })
      .then((r) => (r.ok ? r.json() : Promise.reject()))
      .then((data) => {
        setMe(data);
        const fid = data?.idFormateur || data?.id || 0;
        setFormConge(emptyConge(fid));
        setFormDispo(emptyDispo(fid));
      })
      .catch(() => {
        // laisser la page s'afficher ; il faudra un /me valide pour charger les données
      });
  }, [headers]);

  const loadAll = useCallback(() => {
    if (!me) return;
    const fid = me.idFormateur || me.id || me.formateurId;
    const from = toQueryIso(range.from);
    const to = toQueryIso(range.to);

    fetch(`${API_PREFIX}/api/formateurs/${fid}/agenda?${qs({ from, to, withFree: false })}`, { headers })
      .then((r) => (r.ok ? r.json() : Promise.reject()))
      .then(setAgenda)
      .catch(() => setAgenda([]));

    fetch(`${API_PREFIX}/api/conges/formateur/${fid}?${qs({ from, to })}`, { headers })
      .then((r) => (r.ok ? r.json() : Promise.reject()))
      .then(setConges)
      .catch(() => setConges([]));

    fetch(`${API_PREFIX}/api/disponibilites/formateur/${fid}?${qs({ from, to })}`, { headers })
      .then((r) => (r.ok ? r.json() : Promise.reject()))
      .then(setDispos)
      .catch(() => setDispos([]));
  }, [headers, me, range.from, range.to, toQueryIso]);

  useEffect(() => {
    loadAll();
  }, [loadAll]);

  /* ---------- CRUD Congés ---------- */
  const submitConge = useCallback(
    (e) => {
      e.preventDefault();
      if (!formConge) return;
      const payload = {
        formateurId: formConge.formateurId,
        motif: formConge.motif,
        startAt: toQueryIso(formConge.startAt),
        endAt: toQueryIso(formConge.endAt),
      };
      const isEdit = !!formConge.idConge;
      const url = isEdit ? `${API_PREFIX}/api/conges/${formConge.idConge}` : `${API_PREFIX}/api/conges`;
      const method = isEdit ? "PUT" : "POST";

      fetch(url, { method, headers, body: JSON.stringify(payload) })
        .then(async (r) => {
          if (!r.ok) throw await r.json().catch(() => ({}));
          return r.json();
        })
        .then(() => {
          setFormConge(emptyConge(formConge.formateurId));
          loadAll();
          alert(isEdit ? "Congé mis à jour." : "Congé créé.");
        })
        .catch((err) => alert(err?.error || "Erreur lors de l'enregistrement du congé"));
    },
    [formConge, headers, loadAll, toQueryIso]
  );

  const editConge = useCallback((c) => {
    const toInput = (s) => (s ? s.slice(0, 16) : "");
    setFormConge({
      idConge: c.idConge,
      formateurId: c.formateurId,
      motif: c.motif || "",
      startAt: toInput(c.startAt),
      endAt: toInput(c.endAt),
    });
  }, []);

  const deleteConge = useCallback(
    (id) => {
      if (!window.confirm("Supprimer ce congé ?")) return;
      fetch(`${API_PREFIX}/api/conges/${id}`, { method: "DELETE", headers })
        .then((r) => {
          if (!r.ok) throw new Error();
          loadAll();
        })
        .catch(() => alert("Erreur lors de la suppression"));
    },
    [headers, loadAll]
  );

  /* ---------- CRUD Indispos ---------- */
  const submitDispo = useCallback(
    (e) => {
      e.preventDefault();
      if (!formDispo) return;

      const payload = {
        formateurId: formDispo.formateurId,
        commentaire: formDispo.commentaire,
        recurrence: formDispo.recurrence || "AUCUNE",
        startAt: toQueryIso(formDispo.startAt),
        endAt: toQueryIso(formDispo.endAt),
      };

      const isEdit = !!formDispo.idDisponibilite;
      const url = isEdit
        ? `${API_PREFIX}/api/disponibilites/${formDispo.idDisponibilite}`
        : `${API_PREFIX}/api/disponibilites`;
      const method = isEdit ? "PUT" : "POST";

      fetch(url, { method, headers, body: JSON.stringify(payload) })
        .then(async (r) => {
          if (!r.ok) throw await r.json().catch(() => ({}));
          return r.json();
        })
        .then(() => {
          setFormDispo(emptyDispo(formDispo.formateurId));
          loadAll();
          alert(isEdit ? "Indisponibilité mise à jour." : "Indisponibilité créée.");
        })
        .catch((err) => alert(err?.error || "Erreur lors de l'enregistrement de l'indisponibilité"));
    },
    [formDispo, headers, loadAll, toQueryIso]
  );

  const editDispo = useCallback((d) => {
    const toInput = (s) => (s ? s.slice(0, 16) : "");
    setFormDispo({
      idDisponibilite: d.idDisponibilite,
      formateurId: d.formateurId,
      commentaire: d.commentaire || "",
      recurrence: d.recurrence || "AUCUNE",
      startAt: toInput(d.startAt),
      endAt: toInput(d.endAt),
    });
  }, []);

  const deleteDispo = useCallback(
    (id) => {
      if (!window.confirm("Supprimer cette indisponibilité ?")) return;
      fetch(`${API_PREFIX}/api/disponibilites/${id}`, { method: "DELETE", headers })
        .then((r) => {
          if (!r.ok) throw new Error();
          loadAll();
        })
        .catch(() => alert("Erreur lors de la suppression"));
    },
    [headers, loadAll]
  );

  /* ---------- Agenda groupé par jour ---------- */
  const agendaByDay = useMemo(() => {
    const map = {};
    (agenda || []).forEach((e) => {
      const dayKey = (e.startAt || "").slice(0, 10);
      if (!map[dayKey]) map[dayKey] = [];
      map[dayKey].push(e);
    });
    return map;
  }, [agenda]);

  return (
    <div>
      <Section title="Disponibilités & Congés" actions={<Legend />}>
        <Tabs
          value={tab}
          onChange={setTab}
          items={[
            { value: "agenda", label: "Agenda" },
            { value: "conges", label: "Congés" },
            { value: "indispos", label: "Indisponibilités" },
          ]}
        />

        <div className="mb-12">
          <DateRangePicker from={range.from} to={range.to} onChange={setRange} />
        </div>

        {tab === "agenda" && (
          <div>
            {Object.keys(agendaByDay).length === 0 && (
              <p className="muted">Aucun événement sur la période.</p>
            )}
            {Object.entries(agendaByDay)
              .sort(([a], [b]) => a.localeCompare(b))
              .map(([day, events]) => (
                <div key={day} className="card card--day">
                  <strong>{fmtLocal(`${day}T00:00:00`)}</strong>
                  <ul className="day-list">
                    {events.map((e, idx) => (
                      <li key={`${day}-${idx}`} className="day-item">
                        <span
                          className={`dot ${
                            e.type === "SESSION"
                              ? "dot--session"
                              : e.type === "CONGE"
                              ? "dot--conge"
                              : "dot--indispo"
                          }`}
                        />
                        <strong>{e.title || e.type}</strong>{" "}
                        <span className="muted">
                          ({fmtLocal(e.startAt)} → {fmtLocal(e.endAt)})
                        </span>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
          </div>
        )}

        {tab === "conges" && (
          <div className="grid-v-16">
            <form className="card p-12" onSubmit={submitConge}>
              <h3 className="mt-0">{formConge?.idConge ? "Modifier le congé" : "Nouveau congé"}</h3>
              <div className="grid-v-8">
                <label>
                  Motif
                  <input
                    className="input input--light"
                    type="text"
                    value={formConge?.motif || ""}
                    onChange={(e) => setFormConge({ ...formConge, motif: e.target.value })}
                    required
                  />
                </label>
                <label>
                  Début
                  <input
                    className="input input--light"
                    type="datetime-local"
                    value={formConge?.startAt || ""}
                    onChange={(e) => setFormConge({ ...formConge, startAt: e.target.value })}
                    required
                  />
                </label>
                <label>
                  Fin
                  <input
                    className="input input--light"
                    type="datetime-local"
                    value={formConge?.endAt || ""}
                    onChange={(e) => setFormConge({ ...formConge, endAt: e.target.value })}
                    required
                  />
                </label>
              </div>
              <div className="btn-row mt-12">
                <button className="btn btn--primary" type="submit">
                  {formConge?.idConge ? "Mettre à jour" : "Créer"}
                </button>
                {formConge?.idConge && (
                  <button
                    type="button"
                    className="btn"
                    onClick={() => setFormConge(emptyConge(formConge.formateurId))}
                  >
                    Annuler
                  </button>
                )}
              </div>
            </form>

            <div className="card p-12">
              <h3 className="mt-0">Mes congés</h3>
              <div className="table-wrap">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Motif</th>
                      <th>Début</th>
                      <th>Fin</th>
                      <th style={{ width: 120 }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {conges?.length ? (
                      conges.map((c) => (
                        <tr key={c.idConge}>
                          <td>{c.motif}</td>
                          <td>{fmtLocal(c.startAt)}</td>
                          <td>{fmtLocal(c.endAt)}</td>
                          <td>
                            <div className="tbl-actions">
                              <button className="btn btn--light" type="button" onClick={() => editConge(c)}>
                                Éditer
                              </button>
                              <button className="btn btn--danger" type="button" onClick={() => deleteConge(c.idConge)}>
                                Supprimer
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={4} className="muted">
                          Aucun congé.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {tab === "indispos" && (
          <div className="grid-v-16">
            <form className="card p-12" onSubmit={submitDispo}>
              <h3 className="mt-0">{formDispo?.idDisponibilite ? "Modifier l’indisponibilité" : "Nouvelle indisponibilité"}</h3>
              <div className="grid-v-8">
                <label>
                  Commentaire
                  <input
                    className="input input--light"
                    type="text"
                    value={formDispo?.commentaire || ""}
                    onChange={(e) => setFormDispo({ ...formDispo, commentaire: e.target.value })}
                    required
                  />
                </label>
                <label>
                  Récurrence
                  <select
                    className="input input--light"
                    value={formDispo?.recurrence || "AUCUNE"}
                    onChange={(e) => setFormDispo({ ...formDispo, recurrence: e.target.value })}
                  >
                    <option value="AUCUNE">Aucune</option>
                    <option value="HEBDO">Hebdomadaire</option>
                    <option value="MENSUELLE">Mensuelle</option>
                  </select>
                </label>
                <label>
                  Début
                  <input
                    className="input input--light"
                    type="datetime-local"
                    value={formDispo?.startAt || ""}
                    onChange={(e) => setFormDispo({ ...formDispo, startAt: e.target.value })}
                    required
                  />
                </label>
                <label>
                  Fin
                  <input
                    className="input input--light"
                    type="datetime-local"
                    value={formDispo?.endAt || ""}
                    onChange={(e) => setFormDispo({ ...formDispo, endAt: e.target.value })}
                    required
                  />
                </label>
              </div>
              <div className="btn-row mt-12">
                <button className="btn btn--primary" type="submit">
                  {formDispo?.idDisponibilite ? "Mettre à jour" : "Créer"}
                </button>
                {formDispo?.idDisponibilite && (
                  <button
                    type="button"
                    className="btn"
                    onClick={() => setFormDispo(emptyDispo(formDispo.formateurId))}
                  >
                    Annuler
                  </button>
                )}
              </div>
            </form>

            <div className="card p-12">
              <h3 className="mt-0">Mes indisponibilités</h3>
              <div className="table-wrap">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Commentaire</th>
                      <th>Récurrence</th>
                      <th>Début</th>
                      <th>Fin</th>
                      <th style={{ width: 120 }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dispos?.length ? (
                      dispos.map((d) => (
                        <tr key={d.idDisponibilite}>
                          <td>{d.commentaire}</td>
                          <td>{d.recurrence || "AUCUNE"}</td>
                          <td>{fmtLocal(d.startAt)}</td>
                          <td>{fmtLocal(d.endAt)}</td>
                          <td>
                            <div className="tbl-actions">
                              <button className="btn btn--light" type="button" onClick={() => editDispo(d)}>
                                Éditer
                              </button>
                              <button className="btn btn--danger" type="button" onClick={() => deleteDispo(d.idDisponibilite)}>
                                Supprimer
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={5} className="muted">
                          Aucune indisponibilité.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </Section>
    </div>
  );
}
