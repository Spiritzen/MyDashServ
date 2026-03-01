// src/admin/formateurs/AdminFormateurs.jsx
import { useEffect, useMemo, useState, useCallback } from "react";
import axiosClient from "@/api/axiosClient";
import Modal from "@/components/modal/Modal";
import "@/styles/admin.css";

function Pill({ tone = "muted", children }) {
  const bg = {
    ok:    "var(--ok-800, #064e3b)",
    warn:  "var(--warn-800, #7c2d12)",
    info:  "var(--info-800, #1e3a8a)",
    muted: "rgba(255,255,255,0.06)",
  }[tone];
  return (
    <span style={{
      background: bg, color: "white", padding: "2px 8px", borderRadius: 999,
      fontSize: 12, fontWeight: 600
    }}>{children}</span>
  );
}

function renderStars(n) {
  if (n == null) return "—";
  const clamped = Math.max(0, Math.min(5, Number(n)));
  return "★".repeat(clamped) + "☆".repeat(5 - clamped);
}
function formatDate(d) {
  if (!d) return "—";
  try {
    return new Date(d).toLocaleDateString("fr-FR", {
      year: "numeric", month: "long", day: "2-digit",
    });
  } catch {
    return String(d);
  }
}

export default function AdminFormateurs() {
  const [tab, setTab] = useState("EN_ATTENTE");
  const [loading, setLoading] = useState(true);
  const [rows, setRows] = useState([]);
  const [err, setErr] = useState("");

  const [open, setOpen] = useState(false);
  const [detail, setDetail] = useState(null);

  const fetchList = useCallback(async (activeTab) => {
    const statut = activeTab ?? tab;
    setErr("");
    setLoading(true);
    try {
      const { data } = await axiosClient.get("/api/admin/formateurs", { params: { statut } });
      setRows(Array.isArray(data) ? data : (data?.content || []));
    } catch (e) {
      setErr(e?.response?.data?.message || "Impossible de charger la liste.");
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [tab]);

  useEffect(() => { fetchList("EN_ATTENTE"); }, [fetchList]);
  useEffect(() => { fetchList(tab); }, [tab, fetchList]);

  async function openDetail(row) {
    setErr("");
    const id = row?.id ?? row?.idUtilisateur;
    if (!id) { setErr("Identifiant introuvable pour ce formateur."); return; }
    try {
      const { data } = await axiosClient.get(`/api/admin/formateurs/${id}`);
      setDetail(data);
      setOpen(true);
    } catch (e) {
      setErr(e?.response?.data?.message || "Impossible de charger le détail.");
    }
  }

  async function validateCurrent(ok) {
    if (!detail) return;
    const id = detail?.id ?? detail?.idUtilisateur;
    if (!id) { alert("Identifiant introuvable pour ce formateur."); return; }
    try {
      await axiosClient.patch(`/api/admin/formateurs/${id}/valider`, null, {
        params: { value: ok, comment: "" }
      });
      setOpen(false);
      setDetail(null);
      fetchList(tab);
    } catch (e) {
      alert(e?.response?.data?.message || "Action impossible.");
    }
  }

  const title = useMemo(
    () => (tab === "EN_ATTENTE" ? "Formateurs en attente de validation" : "Formateurs validés"),
    [tab]
  );

  // Construit les chemins relatifs (utilisés via axiosClient → inclut l’Authorization)
  function buildDocPaths(d) {
    const id = d?.idDoc ?? d?.id;
    const download = d?.downloadUrl || (id != null ? `/api/admin/evidences/${id}/download` : undefined);
    const view     = d?.viewUrl     || (id != null ? `/api/admin/evidences/${id}/view`     : undefined);
    return { downloadPath: download, viewPath: view };
  }

  // Utilitaires blob
function filenameFromContentDisposition(cd, fallback) {
  if (!cd) return fallback;
  try {
    const m = /filename\*?=(?:UTF-8''|")?([^";]+)/i.exec(cd);
    if (m && m[1]) {
      const name = decodeURIComponent(m[1].replace(/"/g, ""));
      return name || fallback;
    }
  } catch {
    // Pas de variable catch → plus d’avertissement "no-unused-vars"
    return fallback;
  }
  return fallback;
}

  async function handlePreview(doc) {
    const { viewPath } = buildDocPaths(doc);
    if (!viewPath) return;
    try {
      const res = await axiosClient.get(viewPath, { responseType: "blob" });
      const blobUrl = URL.createObjectURL(res.data);
      window.open(blobUrl, "_blank", "noopener,noreferrer");
      setTimeout(() => URL.revokeObjectURL(blobUrl), 30_000);
    } catch (err) {
      console.error(err);
      alert("Aperçu impossible.");
    }
  }

  async function handleDownload(doc) {
    const { downloadPath, viewPath } = buildDocPaths(doc);
    const path = downloadPath || viewPath;
    if (!path) return;
    try {
      const res = await axiosClient.get(path, { responseType: "blob" });
      const cd = res.headers?.["content-disposition"];
      const suggested = filenameFromContentDisposition(cd, doc.filename || "document");
      const blobUrl = URL.createObjectURL(res.data);
      const a = document.createElement("a");
      a.href = blobUrl;
      a.download = suggested;
      document.body.appendChild(a);
      a.click();
      a.remove();
      setTimeout(() => URL.revokeObjectURL(blobUrl), 1000);
    } catch (err) {
      console.error(err);
      alert("Téléchargement impossible.");
    }
  }

  return (
    <div className="admin-dash">
      <header className="admin-head">
        <div className="admin-head__left">
          <h1>Formateurs</h1>
          <div className="muted">Validation & gestion des profils formateur</div>
        </div>
      </header>

      <div className="tabs" style={{ display: "flex", gap: 8, marginBottom: 12 }}>
        <button
          className={`btn btn--ghost ${tab === "EN_ATTENTE" ? "is-active" : ""}`}
          onClick={() => setTab("EN_ATTENTE")}
        >
          En attente
        </button>
        <button
          className={`btn btn--ghost ${tab === "VALIDE" ? "is-active" : ""}`}
          onClick={() => setTab("VALIDE")}
        >
          Validés
        </button>
      </div>

      {err && <div className="rg-alert rg-alert--error">{err}</div>}

      <div className="panel">
        <div className="panel__head">
          <h2>{title}</h2>
        </div>

        {loading ? (
          <div className="muted">Chargement…</div>
        ) : rows.length === 0 ? (
          <div className="muted">Aucun résultat.</div>
        ) : (
          <div className="tbl-wrap">
            <table className="tbl">
              <thead>
                <tr>
                  <th>Nom</th>
                  <th>Email</th>
                  <th>État email</th>
                  <th>Candidature</th>
                  <th>Compétences</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r) => {
                  const rowId = r.id ?? r.idUtilisateur;
                  return (
                    <tr key={rowId}>
                      <td>{[r.prenom, r.nom].filter(Boolean).join(" ") || "—"}</td>
                      <td>{r.email}</td>
                      <td>{r.emailVerifie ? <Pill tone="ok">Vérifié</Pill> : <Pill tone="warn">Non vérifié</Pill>}</td>
                      <td>
                        {r.compteValide
                          ? <Pill tone="ok">Validé</Pill>
                          : (r.profilSoumisLe ? <Pill tone="info">En attente</Pill> : <Pill tone="warn">Brouillon</Pill>)}
                      </td>
                      <td>{r.nbCompetences ?? 0}</td>
                      <td style={{ textAlign: "right" }}>
                        <button className="btn btn--ghost" onClick={() => openDetail(r)}>
                          Ouvrir
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal détail */}
      <Modal open={open} onClose={() => { setOpen(false); setDetail(null); }}>
        {!detail ? (
          <div className="muted">Chargement…</div>
        ) : (
          <div className="afci-admin-formateurs-modal">
            <h3 className="afci-af-title">
              {detail.prenom} {detail.nom} <span className="muted">· {detail.email}</span>
            </h3>

            <div className="afci-af-stack">
              {/* Profil */}
              <section className="card">
                <div className="panel__head"><h4 style={{ margin: 0 }}>Profil</h4></div>
                <ul className="kv muted">
                  <li className="kv__row"><dt>Téléphone</dt><dd>{detail.telephone || "—"}</dd></li>
                  <li className="kv__row"><dt>Adresse</dt><dd>{detail.adresse || "—"}</dd></li>
                  <li className="kv__row"><dt>Ville</dt><dd>{detail.ville || "—"}</dd></li>
                  <li className="kv__row"><dt>Code postal</dt><dd>{detail.codePostal || "—"}</dd></li>
                  <li className="kv__row"><dt>Email</dt><dd>{detail.emailVerifie ? "Vérifié" : "Non vérifié"}</dd></li>
                  <li className="kv__row"><dt>Candidature</dt>
                    <dd>{detail.compteValide ? "Validée" : (detail.profilSoumisLe ? "En attente" : "Brouillon")}</dd>
                  </li>
                </ul>
              </section>

              {/* Compétences */}
              <section className="card">
                <div className="panel__head"><h4 style={{ margin: 0 }}>Compétences</h4></div>

                {Array.isArray(detail.competences) && detail.competences.length > 0 ? (
                  <ul className="afci-af-competences">
                    {detail.competences.map((c, idx) => {
                      const cid = c.idCompetence ?? c.id ?? idx;
                      const label = c.label ?? c.libelle ?? c.nom ?? c.intitule ?? "Sans libellé";
                      return (
                        <li key={cid} className="afci-af-comp-card">
                          <div className="afci-af-comp-head">
                            <div className="afci-af-comp-title">{label}</div>
                            <div className="afci-af-comp-stars">
                              Niveau : <strong>{renderStars(c.niveau)}</strong> (5)
                            </div>
                          </div>

                          <div className="afci-af-grid">
                            <div>
                              <span className="muted">Titre</span>
                              <div className="wrap">{c.title || "—"}</div>
                            </div>
                            <div>
                              <span className="muted">Dernière utilisation</span>
                              <div className="wrap">{formatDate(c.lastUsed)}</div>
                            </div>
                            <div className="full">
                              <span className="muted">Description</span>
                              <div className="wrap">{c.description || "—"}</div>
                            </div>
                            <div>
                              <span className="muted">Années d’expérience</span>
                              <div>{c.experienceYears ?? "—"}</div>
                            </div>
                            <div>
                              <span className="muted">Statut</span>
                              <div>{c.status ?? "—"}</div>
                            </div>
                            <div>
                              <span className="muted">Visibilité</span>
                              <div>{c.visible ? "Visible" : "Masquée"}</div>
                            </div>
                          </div>

                          {/* Pièces jointes */}
                          <div className="afci-af-docs">
                            <span className="muted">Pièces jointes</span>
                            {Array.isArray(c.docs) && c.docs.length > 0 ? (
                              <ul className="afci-af-docs-list">
                                {c.docs.map((d) => {
                                  const size = d.sizeBytes ?? d.size ?? null;
                                  const when = d.uploadedAt ?? null;
                                  return (
                                    <li key={d.idDoc ?? d.id} className="afci-af-doc-row">
                                      <div className="afci-af-doc-main">
                                        <button
                                          type="button"
                                          className="doc-name btn--link"
                                          title="Aperçu"
                                          onClick={() => handlePreview(d)}
                                        >
                                          {d.filename ?? d.name ?? "document"}
                                        </button>
                                        <span className="doc-meta">
                                          {size ? ` · ${Math.round(Number(size)/1024)} Ko` : ""}{when ? ` · ${formatDate(when)}` : ""}
                                        </span>
                                      </div>
                                      <div className="afci-af-doc-actions">
                                        <button
                                          type="button"
                                          className="btn btn--ghost btn--xs"
                                          onClick={() => handlePreview(d)}
                                          title="Aperçu"
                                        >
                                          Aperçu
                                        </button>
                                        <button
                                          type="button"
                                          className="btn btn--primary btn--xs"
                                          onClick={() => handleDownload(d)}
                                          title="Télécharger"
                                        >
                                          Télécharger
                                        </button>
                                      </div>
                                    </li>
                                  );
                                })}
                              </ul>
                            ) : <div>Aucune pièce</div>}
                          </div>
                        </li>
                      );
                    })}
                  </ul>
                ) : <div className="muted">Aucune compétence</div>}
              </section>
            </div>

            {!detail.compteValide && (
              <div className="panel__actions" style={{ marginTop: 12 }}>
                <button className="btn btn--primary" onClick={() => validateCurrent(true)}>Valider</button>
                {detail.profilSoumisLe && (
                  <button className="btn btn--ghost" onClick={() => validateCurrent(false)}>Refuser</button>
                )}
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
}
