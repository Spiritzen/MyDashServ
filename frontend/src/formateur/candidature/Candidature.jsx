// src/formateur/candidature/Candidature.jsx
import { useEffect, useMemo, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/styles/formateur-profil.css";

export default function Candidature() {
  const [state, setState] = useState(null);   // CandidatureStatusDTO
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState("");
  const [verifyLink, setVerifyLink] = useState(""); // lien de vérification renvoyé par le back

  async function load() {
    setErr("");
    try {
      const { data } = await axiosClient.get("/api/formateurs/me/candidature");
      setState(data);
      setVerifyLink(""); // reset éventuel
    } catch (e) {
      setErr(e?.response?.data?.message || "Impossible de charger l'état de candidature.");
    }
  }

  useEffect(() => { load(); }, []);

  async function soumettre() {
    setBusy(true); setErr("");
    try {
      const { data } = await axiosClient.put("/api/formateurs/me/candidature/soumettre", {
        // nom, prenom, adresse, ville, codePostal, telephone (si tu veux repasser)
      });
      setState(data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Soumission échouée.");
    } finally {
      setBusy(false);
    }
  }

  async function annuler() {
    setBusy(true); setErr("");
    try {
      const { data } = await axiosClient.put("/api/formateurs/me/candidature/annuler");
      setState(data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Annulation échouée.");
    } finally {
      setBusy(false);
    }
  }

  async function sendEmailVerification() {
    setBusy(true); setErr(""); setVerifyLink("");
    try {
      const { data } = await axiosClient.post("/api/auth/email/verify/send");
      if (data?.verifyUrl) {
        setVerifyLink(data.verifyUrl);
        window.open(data.verifyUrl, "_blank", "noopener,noreferrer");
      } else {
        alert("Un email de vérification vient d’être envoyé. Vérifie ta boîte mail.");
      }
    } catch (e) {
      setErr(e?.response?.data?.message || "Impossible d’envoyer l’email de vérification.");
    } finally {
      setBusy(false);
    }
  }

  // ========= Hooks et dérivées AVANT tout return (pas d’early return avant) =========
  const s = state ?? {};
  const {
    statut = "NON_ELIGIBLE",
    profilComplet = false,
    emailVerifie = false,
    compteValide = false,
    nbCompetences = 0,
  } = s;

  const hasProfile = !!profilComplet;
  const hasEmail = !!emailVerifie;
  const hasSkills = (nbCompetences ?? 0) > 0;

  const canSubmit =
    hasProfile && hasEmail && hasSkills &&
    !compteValide && statut !== "EN_ATTENTE";

  const headerMsg = useMemo(() => {
    if (compteValide) return "Votre candidature a déjà été validée ✅.";
    if (statut === "EN_ATTENTE") return "Candidature soumise : en attente de validation.";
    if (canSubmit) return "Tous les prérequis sont remplis : vous pouvez soumettre votre candidature.";

    const misses = [];
    if (!hasProfile) misses.push("compléter votre profil");
    if (!hasEmail) misses.push("vérifier votre adresse email");
    if (!hasSkills) misses.push("ajouter au moins une compétence");
    if (misses.length === 1) return `Pour candidater, vous devez ${misses[0]}.`;
    if (misses.length === 2) return `Pour candidater, vous devez ${misses[0]} et ${misses[1]}.`;
    return "Pour candidater, vous devez compléter votre profil, vérifier votre email et ajouter au moins une compétence.";
  }, [compteValide, statut, canSubmit, hasProfile, hasEmail, hasSkills]);

  // Petit composant badge check
  const CheckItem = ({ ok, label, actions }) => (
    <li className="kv__row" style={{ display: "flex", alignItems: "center", gap: 12 }}>
      <div style={{
        width: 22, height: 22, borderRadius: 6,
        display: "inline-flex", alignItems: "center", justifyContent: "center",
        fontWeight: 700, fontSize: 13,
        background: ok ? "var(--ok-50, #123)" : "var(--warn-50, #321)",
        color: ok ? "var(--ok-400, #7fd)" : "var(--warn-400, #fb9)"
      }}>
        {ok ? "✓" : "!"}
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ fontWeight: 600 }}>{label}</div>
        {actions}
      </div>
    </li>
  );

  // ========= Rendus =========
  if (!state) {
    return (
      <div className="card panel">
        <h2 style={{ margin: 0 }}>Candidature</h2>
        <p className="muted">Chargement…</p>
      </div>
    );
  }

  return (
    <div className="card panel">
      <div className="panel__head">
        <h2 style={{ margin: 0 }}>Candidature Formateur</h2>
      </div>

      {err && <div className="rg-alert rg-alert--error">{err}</div>}

      {/* Bandeau statut clair */}
      <div className="status" style={{ marginBottom: 12 }}>
        <strong>Statut :</strong> {statut}
        <span className="muted"> — {headerMsg}</span>
      </div>

      {/* Checklist des prérequis */}
      <section className="card" style={{ background: "rgba(255,255,255,0.02)" }}>
        <div className="panel__head">
          <h3 style={{ margin: 0 }}>Prérequis pour candidater</h3>
        </div>
        <ul className="kv" style={{ marginTop: 8 }}>
          <CheckItem
            ok={hasProfile}
            label={`Profil complet ${hasProfile ? "✅" : "❌"}`}
            actions={!hasProfile && (
              <div className="muted" style={{ marginTop: 6 }}>
                Renseignez vos informations d’identité et de contact.
                <div style={{ marginTop: 8, display: "flex", gap: 8, flexWrap: "wrap" }}>
                  <a className="btn btn--ghost" href="/formateur?tab=profil">Compléter mon profil</a>
                </div>
              </div>
            )}
          />

          <CheckItem
            ok={hasEmail}
            label={`Email vérifié ${hasEmail ? "✅" : "❌"}`}
            actions={!hasEmail && (
              <div className="muted" style={{ marginTop: 6 }}>
                Cliquez pour recevoir un lien de vérification par email.
                <div style={{ marginTop: 8, display: "flex", gap: 8, flexWrap: "wrap" }}>
                  <button className="btn btn--ghost" onClick={sendEmailVerification} disabled={busy}>
                    {busy ? "Envoi…" : "Envoyer le lien"}
                  </button>
                  {verifyLink && (
                    <>
                      <a className="btn btn--xs" href={verifyLink} target="_blank" rel="noreferrer">Ouvrir le lien</a>
                      <button className="btn btn--xs" onClick={() => navigator.clipboard.writeText(verifyLink)}>
                        Copier le lien
                      </button>
                    </>
                  )}
                </div>
              </div>
            )}
          />

          <CheckItem
            ok={hasSkills}
            label={`Compétences déclarées ${hasSkills ? `✅ (${nbCompetences})` : "❌ (0)"}`}
            actions={!hasSkills && (
              <div className="muted" style={{ marginTop: 6 }}>
                Ajoutez au moins une compétence à votre profil.
                <div style={{ marginTop: 8 }}>
                  <a className="btn btn--ghost" href="/formateur?tab=competences">Ajouter une compétence</a>
                </div>
              </div>
            )}
          />
        </ul>
      </section>

      {/* Récap rapide (info brutes) */}
      <ul className="muted" style={{ marginTop: 16 }}>
        <li>Profil complet : {hasProfile ? "Oui" : "Non"}</li>
        <li>Email vérifié : {hasEmail ? "Oui" : "Non"}</li>
        <li>Compétences déclarées : {nbCompetences}</li>
      </ul>

      {/* Actions globales */}
      <div style={{ marginTop: 16, display: "flex", gap: 8, flexWrap: "wrap" }}>
        {/* Soumettre visible uniquement si toutes les conditions sont remplies */}
        {canSubmit && (
          <button className="btn btn--primary" onClick={soumettre} disabled={busy}>
            {busy ? "Soumission…" : "Soumettre ma candidature"}
          </button>
        )}

        {/* Annuler visible si en attente */}
        {statut === "EN_ATTENTE" && !compteValide && (
          <button className="btn btn--ghost" onClick={annuler} disabled={busy}>
            {busy ? "Annulation…" : "Annuler la soumission"}
          </button>
        )}

        {/* Refresh manuel */}
        <button className="btn btn--ghost" onClick={load} disabled={busy}>
          Recharger l’état
        </button>
      </div>
    </div>
  );
}
