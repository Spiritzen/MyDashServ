import { useCallback, useEffect, useMemo, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "./FormateurFormations.css";

function fmt(dt) {
  if (!dt) return "—";
  const d = new Date(dt);
  return d.toLocaleString("fr-FR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function sessionStatutLabel(statut) {
  switch (statut) {
    case "EN_COURS":
      return "Proposée";
    case "PLANIFIEE":
      return "Planifiée";
    case "ANNULEE":
      return "Annulée";
    case "TERMINEE":
      return "Terminée";
    case "AFFECTEE":
      return "Affectée";
    default:
      return statut || "—";
  }
}

function sessionBadgeClass(statut) {
  switch (statut) {
    case "EN_COURS":
      return "ff-badge ff-badge--warn";
    case "PLANIFIEE":
      return "ff-badge ff-badge--ok";
    case "ANNULEE":
      return "ff-badge ff-badge--ko";
    default:
      return "ff-badge";
  }
}

function affectationLabel(statut) {
  // affichage “bonus” (mais on ne filtre plus dessus)
  switch (statut) {
    case "PROPOSEE":
      return "Proposée";
    case "CONFIRMEE":
      return "Confirmée";
    case "REFUSEE":
      return "Refusée";
    default:
      return statut || "—";
  }
}

export default function FormateurFormations() {
  // Tabs UI basés sur le statut de SESSION
  const [tab, setTab] = useState("PROPOSEES"); // PROPOSEES | ACCEPTEES | REFUSEES
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Endpoints (ceux que tu as)
  const LIST_URL = "/api/formateur/affectations";
  const ACTION_URL = "/api/formateur/affectations";

  const params = useMemo(() => {
    // IMPORTANT: pas de filtre "statut" ici, on récupère tout et on filtre par session.statut côté React
    return { page: 0, size: 100 };
  }, []);

  const normalizeRows = (data) => {
    if (!data) return [];
    if (Array.isArray(data)) return data;
    if (Array.isArray(data.content)) return data.content;
    if (Array.isArray(data.data)) return data.data;
    return [];
  };

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await axiosClient.get(LIST_URL, { params });
      setRows(normalizeRows(res?.data));
    } catch (e) {
      console.error("[formateur/affectations] load failed:", e);
      const msg = e?.response?.data?.message || e?.message || "Erreur de chargement";
      setError(msg);
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [LIST_URL, params]);

  useEffect(() => {
    load();
  }, [load]);

  // --- mapping tolérant (projection peut exposer des noms différents) ---
  const mapped = useMemo(() => {
    return rows.map((r) => {
      const affectationId =
        r?.idAffectation ?? r?.id_affectation ?? r?.id ?? r?.affectationId;

      const sessionId =
        r?.sessionId ?? r?.idSession ?? r?.id_session ?? r?.session_id;

      const dateDebut = r?.dateDebut ?? r?.date_debut;
      const dateFin = r?.dateFin ?? r?.date_fin;

      const formation =
        r?.formationIntitule ??
        r?.formationLabel ??
        r?.intitule ??
        r?.formation ??
        "—";

      const mode = r?.mode ?? "—";
      const ville = r?.ville ?? "—";
      const salle = r?.salle ? ` / ${r.salle}` : "";

      // ✅ on veut le statut de SESSION pour les onglets
      const sessionStatut =
        r?.sessionStatut ?? r?.session_statut ?? r?.statut_session ?? r?.sessionStatutCode;

      // (optionnel) statut affectation affiché à l’écran
      const affectationStatut =
        r?.affectationStatut ?? r?.statutAffectation ?? r?.statut_affectation ?? r?.statut;

      return {
        raw: r,
        affectationId,
        sessionId,
        dateDebut,
        dateFin,
        formation,
        mode,
        lieu: `${ville}${salle}`,
        sessionStatut,
        affectationStatut,
      };
    });
  }, [rows]);

  // ✅ FILTRAGE UNIQUEMENT SUR sessionStatut
  const filtered = useMemo(() => {
    return mapped.filter((x) => {
      if (tab === "PROPOSEES") return x.sessionStatut === "EN_COURS";
      if (tab === "ACCEPTEES") return x.sessionStatut === "PLANIFIEE";
      if (tab === "REFUSEES") return x.sessionStatut === "ANNULEE";
      return true;
    });
  }, [mapped, tab]);

  const accept = async (affectationId, sessionStatut) => {
    if (!affectationId) return alert("Affectation introuvable (id manquant).");
    if (sessionStatut !== "EN_COURS") return; // ✅ règle demandée
    if (!window.confirm("Accepter cette proposition ?")) return;

    try {
      await axiosClient.post(`${ACTION_URL}/${affectationId}/accept`);
      await load();
      setTab("ACCEPTEES"); // optionnel: te bascule direct
    } catch (e) {
      console.error("[accept] failed:", e);
      alert(e?.response?.data?.message || "Impossible d’accepter.");
    }
  };

  const refuse = async (affectationId, sessionStatut) => {
    if (!affectationId) return alert("Affectation introuvable (id manquant).");
    if (sessionStatut !== "EN_COURS") return; // ✅ règle demandée
    if (!window.confirm("Refuser cette proposition ?")) return;

    try {
      await axiosClient.post(`${ACTION_URL}/${affectationId}/refuse`);
      await load();
      setTab("REFUSEES"); // optionnel
    } catch (e) {
      console.error("[refuse] failed:", e);
      alert(e?.response?.data?.message || "Impossible de refuser.");
    }
  };

  return (
    <section className="ff-wrap">
      <header className="ff-head">
        <div>
          <h1>Mes formations</h1>
          <div className="ff-muted">
            Onglets basés sur <b>le statut de la session</b> : EN_COURS / PLANIFIEE / ANNULEE.
          </div>
        </div>
      </header>

      <div className="ff-tabs">
        <button
          className={`ff-tab ${tab === "PROPOSEES" ? "is-active" : ""}`}
          onClick={() => setTab("PROPOSEES")}
        >
          Proposées
        </button>

        <button
          className={`ff-tab ${tab === "ACCEPTEES" ? "is-active" : ""}`}
          onClick={() => setTab("ACCEPTEES")}
        >
          Acceptées
        </button>

        <button
          className={`ff-tab ${tab === "REFUSEES" ? "is-active" : ""}`}
          onClick={() => setTab("REFUSEES")}
        >
          Refusées
        </button>
      </div>

      {error && <div className="ff-alert">{error}</div>}
      {loading && <div className="ff-loading">Chargement…</div>}

      {!loading && !error && (
        <div className="ff-card">
          <table className="ff-table">
            <thead>
              <tr>
                <th>Début</th>
                <th>Fin</th>
                <th>Formation</th>
                <th>Mode</th>
                <th>Lieu</th>
                <th>Affectation</th>
                <th>Session</th>
                <th className="ff-actions-col">Action</th>
              </tr>
            </thead>

            <tbody>
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={8} className="ff-empty">
                    Aucune session dans cet onglet.
                  </td>
                </tr>
              )}

              {filtered.map((r, i) => {
                const canAnswer = r.sessionStatut === "EN_COURS";

                return (
                  <tr key={r.affectationId ?? r.sessionId ?? i}>
                    <td>{fmt(r.dateDebut)}</td>
                    <td>{fmt(r.dateFin)}</td>
                    <td className="ff-strong">{r.formation}</td>
                    <td>{r.mode}</td>
                    <td>{r.lieu}</td>

                    <td>
                      <span className="ff-badge">
                        {affectationLabel(r.affectationStatut)}
                      </span>
                    </td>

                    <td>
                      <span className={sessionBadgeClass(r.sessionStatut)}>
                        {sessionStatutLabel(r.sessionStatut)}
                      </span>
                    </td>

                    <td className="ff-actions">
                      {canAnswer ? (
                        <>
                          <button
                            className="ff-btn ff-btn--ok"
                            onClick={() => accept(r.affectationId, r.sessionStatut)}
                          >
                            Accepter
                          </button>
                          <button
                            className="ff-btn ff-btn--ko"
                            onClick={() => refuse(r.affectationId, r.sessionStatut)}
                          >
                            Refuser
                          </button>
                        </>
                      ) : (
                        <span className="ff-muted">—</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
