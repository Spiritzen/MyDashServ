// src/formateur/competences/MesCompetencesRight.jsx
import { useEffect, useState, useCallback, useMemo } from "react";

function getAnyToken() {
  const keys = ["auth_token", "token", "authToken", "jwt", "accessToken"];
  for (const k of keys) {
    const v = localStorage.getItem(k) || sessionStorage.getItem(k);
    if (v && typeof v === "string") return v.startsWith("Bearer ") ? v.slice(7) : v;
  }
  return null;
}

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";
const authHeaders = (extra = {}) => {
  const t = getAnyToken();
  return t ? { ...extra, Authorization: `Bearer ${t}` } : extra;
};

// mapping FR pour les statuts
const STATUS_FR = { PENDING: "En attente", APPROVED: "Validée", REJECTED: "Refusée" };
const STATUS_CLASS = (s) =>
  s === "APPROVED" ? "bdg bdg--ok" : s === "REJECTED" ? "bdg bdg--ko" : "bdg bdg--plan";

export default function MesCompetencesRight() {
  // catalogue
  const [competences, setCompetences] = useState([]);
  const [competenceId, setCompetenceId] = useState("");

  // sélection courante
  const selectedComp = useMemo(
    () => competences.find((c) => String(c.idCompetence) === String(competenceId)),
    [competences, competenceId]
  );

  // déclarations
  const [mine, setMine] = useState([]);
  const [loadingMine, setLoadingMine] = useState(false);

  // états UI
  const [msg, setMsg] = useState("");
  const [authIssue, setAuthIssue] = useState(false);
  const [showForm, setShowForm] = useState(false);

  // formulaire
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    niveau: 3,
    title: "",
    description: "",
    experienceYears: "",
    lastUsed: "",
    visible: true,
  });

  // documents
  const [docs, setDocs] = useState([]);
  const [loadingDocs, setLoadingDocs] = useState(false);
  const [uploading, setUploading] = useState(false);

  // helpers http
  const getJson = async (url) => {
    const res = await fetch(url, { headers: authHeaders() });
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      const err = new Error(`${res.status} ${res.statusText} ${text}`.trim());
      err.status = res.status;
      throw err;
    }
    return res.json();
  };
  const postJson = async (url, body) => {
    const res = await fetch(url, {
      method: "POST",
      headers: authHeaders({ "Content-Type": "application/json" }),
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      const err = new Error(`${res.status} ${res.statusText} ${text}`.trim());
      err.status = res.status;
      throw err;
    }
    return res.json();
  };
  const uploadFiles = async (url, files) => {
    const fd = new FormData();
    for (const f of files) fd.append("files", f);
    const res = await fetch(url, { method: "POST", headers: authHeaders(), body: fd });
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      const err = new Error(`${res.status} ${res.statusText} ${text}`.trim());
      err.status = res.status;
      throw err;
    }
    return res.json();
  };
  const del = async (url) => {
    const res = await fetch(url, { method: "DELETE", headers: authHeaders() });
    if (!(res.ok || res.status === 204)) {
      const text = await res.text().catch(() => "");
      const err = new Error(`${res.status} ${res.statusText} ${text}`.trim());
      err.status = res.status;
      throw err;
    }
  };

  // charge catalogue
  useEffect(() => {
    (async () => {
      try {
        const co = await getJson(`${API_BASE}/api/competences`);
        setCompetences(co ?? []);
      } catch (e) {
        console.error("catalogue", e);
      }
    })();
  }, []);

  // mes déclarations
  const refreshMine = useCallback(async () => {
    try {
      setLoadingMine(true);
      setAuthIssue(false);
      const data = await getJson(`${API_BASE}/api/me/competences`);
      setMine(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("mine", e);
      setMine([]);
      if (e.status === 401 || e.status === 403) {
        setAuthIssue(true);
        setMsg("Accès refusé. Connecte-toi et vérifie que ton compte est lié à un profil formateur.");
      }
    } finally {
      setLoadingMine(false);
    }
  }, []);
  useEffect(() => { refreshMine(); }, [refreshMine]);

  // docs
  const refreshDocs = useCallback(async (cid) => {
    if (!cid) return setDocs([]);
    try {
      setLoadingDocs(true);
      const data = await getJson(`${API_BASE}/api/me/competences/${cid}/docs`);
      setDocs(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("docs", e);
      setDocs([]);
      if (e.status === 401 || e.status === 403) {
        setAuthIssue(true);
        setMsg("Accès refusé pour les documents. Vérifie l'authentification.");
      }
    } finally {
      setLoadingDocs(false);
    }
  }, []);
  useEffect(() => { refreshDocs(competenceId); }, [competenceId, refreshDocs]);

  // handlers
  const onChangeForm = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((f) => ({ ...f, [name]: type === "checkbox" ? !!checked : value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!competenceId) { setMsg("Choisis d’abord une compétence."); return; }
    const payload = {
      competenceId: Number(competenceId),
      niveau: form.niveau ? Number(form.niveau) : null,
      title: form.title || null,
      description: form.description || null,
      experienceYears: form.experienceYears !== "" ? Number(form.experienceYears) : null,
      lastUsed: form.lastUsed || null,
      visible: !!form.visible,
    };
    try {
      setSaving(true); setMsg("");
      await postJson(`${API_BASE}/api/me/competences`, payload);
      setMsg("Enregistré ✅");
      setShowForm(false);
      refreshMine();
    } catch (e) {
      console.error(e);
      setMsg(`Erreur: ${String(e.message || e)}`);
      if (e.status === 401 || e.status === 403) setAuthIssue(true);
    } finally {
      setSaving(false);
    }
  };

  const onUpload = async (e) => {
    const files = Array.from(e.target.files || []);
    if (!files.length || !competenceId) return;
    try {
      setUploading(true);
      await uploadFiles(`${API_BASE}/api/me/competences/${competenceId}/docs`, files);
      await refreshDocs(competenceId);
      e.target.value = "";
    } catch (e) {
      console.error(e);
      alert("Upload échoué: " + String(e.message || e));
      if (e.status === 401 || e.status === 403) setAuthIssue(true);
    } finally {
      setUploading(false);
    }
  };

  const onDeleteDoc = async (docId) => {
    if (!window.confirm("Supprimer ce document ?")) return;
    try {
      await del(`${API_BASE}/api/me/competences/${competenceId}/docs/${docId}`);
      refreshDocs(competenceId);
    } catch (e) {
      console.error(e);
      alert("Suppression échouée: " + String(e.message || e));
    }
  };

  // UI
  return (
    <div className="stack gap-32 competences-wrap">{/* davantage d'espace entre les cards */}
      <div className="card panel">
        <h2 style={{ marginTop: 0 }}>Mes compétences</h2>

        {authIssue && (
          <div className="item" style={{ borderColor: "#a33" }}>
            <strong>Authentification requise</strong>
            <div className="muted small">{msg}</div>
          </div>
        )}

        {/* Choix compétence (thème déjà visible dans le libellé) */}
        <div className="grid-1 gap-12" style={{ marginTop: 12 }}>
          <div>
            <label className="label">Compétence</label>
            <select
              className="input input--light"
              value={competenceId}
              onChange={(e) => setCompetenceId(e.target.value)}
            >
              <option value="">— Choisir —</option>
              {competences.map((c, idx) => (
                <option key={`c-${c?.idCompetence ?? idx}`} value={c.idCompetence}>
                  {c.label} {c.themeName ? `(${c.themeName})` : ""}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Bouton pour dévoiler/masquer le formulaire */}
        <div className="row gap-8" style={{ marginTop: 12 }}>
          <button className="btn" onClick={() => setShowForm(v => !v)} disabled={!competenceId}>
            {showForm ? "Masquer le formulaire" : "Ajouter une compétence"}
          </button>
          {!competenceId && <span className="muted">Choisis d’abord une compétence.</span>}
        </div>

        {/* Formulaire */}
        {showForm && (
          <form className="stack gap-12" onSubmit={onSubmit} style={{ marginTop: 16 }}>
            <div className="grid-3 gap-12">
              <div>
                <label className="label">Niveau (1..5)</label>
                <select name="niveau" className="input input--light" value={form.niveau} onChange={onChangeForm}>
                  {[1,2,3,4,5].map(n => <option key={n} value={n}>{n}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Expérience (années)</label>
                <input
                  name="experienceYears" className="input input--light" type="number" min="0" max="50"
                  value={form.experienceYears} onChange={onChangeForm}
                />
              </div>
              <div>
                <label className="label">Dernière utilisation</label>
                <input
                  name="lastUsed" className="input input--light" type="date"
                  value={form.lastUsed} onChange={onChangeForm}
                />
              </div>
            </div>

            <div className="grid-1 gap-12">
              <div>
                <label className="label">Titre</label>
                <input
                  name="title" className="input input--light" placeholder="Ex: Java avancé"
                  value={form.title} onChange={onChangeForm}
                />
              </div>
              <div>
                <label className="label">Description</label>
                <textarea
                  name="description" className="input input--light" rows={4}
                  placeholder="Détails / périmètre / exemples…"
                  value={form.description} onChange={onChangeForm}
                />
              </div>
            </div>

            <label className="check">
              <input type="checkbox" name="visible" checked={!!form.visible} onChange={onChangeForm}/>
              <span>Visible au planning</span>
            </label>

            <div className="row gap-8">
              <button className="btn" type="submit" disabled={saving || !competenceId}>
                {saving ? "Enregistrement…" : "Enregistrer"}
              </button>
              {msg && <span className="muted">{msg}</span>}
            </div>
          </form>
        )}
      </div>

      {/* Documents */}
      <div className="card panel">
        <h3 style={{ marginTop: 0 }}>
          Documents de preuve {selectedComp ? `(${selectedComp.label})` : ""}
        </h3>

        <div className="row gap-8" style={{ marginBottom: 8 }}>
          <input key={competenceId || "no"} type="file" multiple
                 disabled={!competenceId || uploading} onChange={onUpload}/>
          {uploading && <span className="muted">Upload…</span>}
        </div>

        {loadingDocs ? <p className="muted">Chargement…</p> : null}
        {!loadingDocs && docs.length === 0 ? <p className="muted">Aucun document.</p> : null}

        <ul className="list gap-8">
          {docs.map(d => (
            <li key={d.idDoc} className="item row between doc-item">
              <div className="stack">
                <strong>{d.filename}</strong>
                <span className="muted small">
                  {d.mimeType} • {(d.sizeBytes/1024).toFixed(1)} Ko • {new Date(d.uploadedAt).toLocaleString()}
                </span>
              </div>
              <div className="row gap-8 doc-actions">
                {d.url && (
                  <a className="btn btn--ghost" href={`${API_BASE}${d.url}`} target="_blank" rel="noreferrer">
                    Ouvrir
                  </a>
                )}
                <button className="btn danger" onClick={() => onDeleteDoc(d.idDoc)}>Supprimer</button>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {/* Mes déclarations — tableau */}
      <div className="card panel">
        <h3 style={{ marginTop: 0 }}>Mes déclarations</h3>

        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Compétence</th>
                <th>Niveau</th>
                <th>Titre</th>
                <th>Exp.</th>
                <th>Dernière utilisation</th>
                <th>Statut</th>
                <th>Visible</th>
                <th>Maj</th>
                <th style={{width:160}}></th>
              </tr>
            </thead>
            <tbody>
              {loadingMine && <tr><td colSpan={9} className="muted">Chargement…</td></tr>}
              {!loadingMine && mine.length === 0 && <tr><td colSpan={9} className="muted">—</td></tr>}
              {mine.map(m => (
                <tr key={`mine-${m.competenceId}`}>
                  <td>{m.competenceLabel}</td>
                  <td>{m.niveau ?? "—"}</td>
                  <td>{m.title || "—"}</td>
                  <td>{m.experienceYears ?? 0} an(s)</td>
                  <td>{m.lastUsed || "—"}</td>
                  <td><span className={STATUS_CLASS(m.status)}>{STATUS_FR[m.status] || m.status}</span></td>
                  <td>{m.visible ? "oui" : "non"}</td>
                  <td>{new Date(m.derniereMaj).toLocaleString()}</td>
                  <td className="tbl-actions">
                    <button
                      className="btn btn--ghost"
                      onClick={() => {
                        setCompetenceId(String(m.competenceId));
                        setShowForm(true);
                        setForm({
                          niveau: m.niveau ?? 3,
                          title: m.title ?? "",
                          description: m.description ?? "",
                          experienceYears: m.experienceYears ?? "",
                          lastUsed: m.lastUsed ?? "",
                          visible: !!m.visible,
                        });
                        refreshDocs(m.competenceId);
                        window.scrollTo({ top: 0, behavior: "smooth" });
                      }}
                    >
                      Éditer
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
}
