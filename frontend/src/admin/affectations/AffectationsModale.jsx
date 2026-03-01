import { useEffect, useMemo, useState } from "react";
import axiosClient from "@/api/axiosClient";
import Modal from "@/components/modal/Modal";
import "./AffectationsModale.css";

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

function fullName(c) {
  const n = [c?.prenom, c?.nom].filter(Boolean).join(" ").trim();
  return n || c?.email || (c?.formateurId ? `#${c.formateurId}` : "—");
}

function prettyLine(c) {
  const dispo = c?.enConge ? "En congé" : c?.dispoOK ? "Disponible" : "Indisponible";
  const conflits = Number(c?.nbConflits || 0);
  return `${fullName(c)} — ${dispo} — conflits:${conflits}`;
}

export default function AffectationsModale({ open, session, onClose, onAssigned }) {
  const [loading, setLoading] = useState(false);
  const [cands, setCands] = useState([]);
  const [error, setError] = useState("");
  const [quickIndex, setQuickIndex] = useState("");

  const sessionId = session?.id ?? session?.idSession ?? session?.sessionId;

  useEffect(() => {
    if (!open || !sessionId) return;

    let cancelled = false;

    (async () => {
      setLoading(true);
      setError("");
      setQuickIndex("");
      try {
        const { data } = await axiosClient.get("/api/affectation/candidates", {
          params: { sessionId },
        });
        if (!cancelled) setCands(Array.isArray(data) ? data : []);
      } catch (e) {
        console.error("[candidates] load error:", e);
        if (!cancelled) setError("Impossible de charger les candidats.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
      setCands([]);
    };
  }, [open, sessionId]);

  // candidats réellement affectables
  const candsDispo = useMemo(() => {
    return (Array.isArray(cands) ? cands : []).filter((c) => {
      const conflicts = Number(c?.nbConflits || 0);
      return c?.dispoOK === true && !c?.enConge && conflicts === 0;
    });
  }, [cands]);

  const best = useMemo(
    () => (candsDispo.length ? candsDispo[0] : null),
    [candsDispo]
  );

  const assign = async (formateurId) => {
    if (!sessionId || !formateurId) return;
    if (!window.confirm("Confirmer l’affectation de ce formateur ?")) return;

    try {
      await axiosClient.post("/api/affectation/assign", { sessionId, formateurId });
      onAssigned?.();
      onClose?.();
    } catch (e) {
      console.error("[assign] failed:", e);
      alert(e?.response?.data?.message || "Affectation impossible.");
    }
  };

  const title = session
    ? `Matching formateurs — ${session.formationLabel || session.formationIntitule || "Formation"}`
    : "Matching formateurs";

  return (
    <Modal open={!!open} onClose={onClose} width={980} title={title}>
      {/* Bandeau session */}
      {session && (
        <div className="affm-session">
          <div className="affm-session__row">
            <span className="affm-label">Session</span>
            <span className="affm-value">
              {fmt(session.dateDebut)} <span className="affm-arrow">→</span>{" "}
              {fmt(session.dateFin)}
            </span>
          </div>

          <div className="affm-session__row">
            <span className="affm-label">Lieu</span>
            <span className="affm-value">
              {session.ville || "—"}
              {session.salle ? ` • ${session.salle}` : ""}
            </span>
          </div>
        </div>
      )}

      {error && <div className="affm-alert">{error}</div>}
      {loading && <div className="affm-loading">Calcul du matching…</div>}

      {!loading && !error && (
        <>
          {/* Sélection rapide */}
          <div className="affm-quick">
            <div className="affm-quick__info">
              <div className="affm-count">
                {candsDispo.length} candidat{candsDispo.length > 1 ? "s" : ""} disponible
                {candsDispo.length > 1 ? "s" : ""}.
              </div>
              <div className="affm-hint">
                Astuce : le premier candidat est généralement le meilleur score.
              </div>
            </div>

            <div className="affm-quick__actions">
              <select
                className="affm-select"
                value={quickIndex}
                onChange={(e) => setQuickIndex(e.target.value)}
                disabled={!candsDispo.length}
              >
                <option value="" disabled>
                  — Sélection rapide —
                </option>

                {candsDispo.map((c, i) => (
                  <option key={c.formateurId ?? i} value={String(i)}>
                    {prettyLine(c)}
                  </option>
                ))}
              </select>

              <button
                className="affm-btn"
                disabled={!candsDispo.length}
                onClick={() => {
                  if (quickIndex === "" && best) return assign(best.formateurId);
                  const idx = Number(quickIndex);
                  const c = candsDispo?.[idx];
                  if (c) assign(c.formateurId);
                }}
              >
                Affecter
              </button>
            </div>
          </div>

          {/* Liste candidats */}
          <div className="affm-list">
            {(!cands || cands.length === 0) && (
              <div className="affm-empty">
                Aucun candidat compatible pour cette session.
              </div>
            )}

            {cands?.map((c) => {
              const skillsOK = Array.isArray(c.skillsOK) ? c.skillsOK : [];
              const skillsKO = Array.isArray(c.skillsKO) ? c.skillsKO : [];
              const conflicts = Number(c.nbConflits || 0);

              const status = c.enConge ? "conge" : c.dispoOK ? "ok" : "ko";
              const rowDisabled = !!c.enConge || conflicts > 0;

              return (
                <article
                  key={c.formateurId ?? `${c.nom}-${c.email}`}
                  className={`affm-card affm-card--${status}`}
                >
                  <div className="affm-card__top">
                    <div className="affm-score">
                      <div className="affm-score__value">
                        {Math.round(Number(c.score) || 0)}
                      </div>
                      <div className="affm-score__label">Score</div>
                    </div>

                    <div className="affm-id">
                      <div className="affm-name">{fullName(c)}</div>
                      {c.email && <div className="affm-email">{c.email}</div>}
                      <div className="affm-badges">
                        {c.enConge ? (
                          <span className="affm-badge affm-badge--warn">En congé</span>
                        ) : c.dispoOK ? (
                          <span className="affm-badge affm-badge--ok">Disponible</span>
                        ) : (
                          <span className="affm-badge affm-badge--warn">Indisponible</span>
                        )}

                        <span
                          className={`affm-badge ${
                            conflicts > 0 ? "affm-badge--warn" : "affm-badge--ok"
                          }`}
                        >
                          {conflicts} conflit{conflicts > 1 ? "s" : ""}
                        </span>
                      </div>
                    </div>

                    <div className="affm-actions">
                      <button
                        className="affm-btn affm-btn--ghost"
                        disabled={rowDisabled}
                        onClick={() => assign(c.formateurId)}
                      >
                        Affecter
                      </button>

                      {c.commentaires && (
                        <button
                          type="button"
                          className="affm-btn affm-btn--ghost2"
                          onClick={() => alert(c.commentaires)}
                        >
                          Détails
                        </button>
                      )}
                    </div>
                  </div>

                  <div className="affm-card__skills">
                    <div className="affm-skillrow">
                      <span className="affm-skilltag">✅ OK</span>
                      <span className="affm-skilltext">
                        {skillsOK.length
                          ? skillsOK.join(", ")
                          : "Aucune compétence détectée"}
                      </span>
                    </div>

                    {skillsKO.length > 0 && (
                      <div className="affm-skillrow">
                        <span className="affm-skilltag">⚠️ Manquantes</span>
                        <span className="affm-skilltext">
                          {skillsKO.join(", ")}
                        </span>
                      </div>
                    )}
                  </div>
                </article>
              );
            })}
          </div>
        </>
      )}
    </Modal>
  );
}
