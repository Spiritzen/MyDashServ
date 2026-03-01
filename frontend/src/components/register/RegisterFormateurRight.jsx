import { useState } from "react";
import axiosClient from "@/api/axiosClient";

const INIT = {
  email: "",
  password: "",
  nom: "",
  prenom: "",
  adresse: "",
  ville: "",
  codePostal: "",
  telephone: "",
};

export default function RegisterFormateurRight() {
  const [v, setV] = useState(INIT);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [showPwd, setShowPwd] = useState(false);

  const set = (k) => (e) => setV((s) => ({ ...s, [k]: e.target.value }));

  const canSubmit =
    v.email.trim() &&
    v.password.trim().length >= 6 &&
    v.nom.trim() &&
    v.prenom.trim();

  const submit = async (e) => {
    e.preventDefault();
    if (!canSubmit || loading) return;

    setLoading(true);
    setErr("");

    try {
      await axiosClient.post("/api/users", {
        email: v.email.trim(),
        password: v.password,
        nom: v.nom.trim(),
        prenom: v.prenom.trim(),
        adresse: v.adresse?.trim() || null,
        ville: v.ville?.trim() || null,
        codePostal: v.codePostal?.trim() || null,
        telephone: v.telephone?.trim() || null,
      });

      // redirection "classique" (tu préfères href)
      window.location.href = "/login";
    } catch (e) {
      const msg =
        e?.response?.data?.message ||
        e?.response?.data ||
        e?.message ||
        "Échec de l’inscription.";
      setErr(String(msg));
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="register-card">
      <header className="register-card__header">
        <h1 className="register-card__title">Créer mon compte formateur</h1>
        <p className="register-card__subtitle">
          Votre compte sera créé en tant que <strong>FORMATEUR</strong>. Vous pourrez compléter votre profil
          puis <em>soumettre</em> votre candidature pour validation.
        </p>
      </header>

      <form className="rg-grid" onSubmit={submit} noValidate>
        <div className="full">
          <label className="rg-label" htmlFor="email">Email *</label>
          <input
            id="email"
            className="rg-input"
            type="email"
            value={v.email}
            onChange={set("email")}
            required
            placeholder="vous@exemple.com"
            autoComplete="email"
          />
        </div>

        <div className="full rg-password">
          <label className="rg-label" htmlFor="password">Mot de passe *</label>
          <div className="rg-password__wrap">
            <input
              id="password"
              className="rg-input"
              type={showPwd ? "text" : "password"}
              value={v.password}
              onChange={set("password")}
              required
              minLength={6}
              placeholder="••••••••"
              autoComplete="new-password"
            />
            <button
              type="button"
              className="rg-password__toggle"
              onClick={() => setShowPwd((s) => !s)}
              aria-label={showPwd ? "Masquer le mot de passe" : "Afficher le mot de passe"}
            >
              {showPwd ? "Masquer" : "Afficher"}
            </button>
          </div>
          <small className="rg-hint">Au moins 6 caractères.</small>
        </div>

        <div>
          <label className="rg-label" htmlFor="nom">Nom *</label>
          <input id="nom" className="rg-input" value={v.nom} onChange={set("nom")} required placeholder="Dupont" />
        </div>

        <div>
          <label className="rg-label" htmlFor="prenom">Prénom *</label>
          <input id="prenom" className="rg-input" value={v.prenom} onChange={set("prenom")} required placeholder="Sophie" />
        </div>

        <div className="full">
          <label className="rg-label" htmlFor="adresse">Adresse</label>
          <input id="adresse" className="rg-input" value={v.adresse} onChange={set("adresse")} placeholder="12 rue des Tulipes" />
        </div>

        <div>
          <label className="rg-label" htmlFor="ville">Ville</label>
          <input id="ville" className="rg-input" value={v.ville} onChange={set("ville")} placeholder="Paris" />
        </div>

        <div>
          <label className="rg-label" htmlFor="codePostal">Code postal</label>
          <input id="codePostal" className="rg-input" value={v.codePostal} onChange={set("codePostal")} placeholder="75015" />
        </div>

        <div className="full">
          <label className="rg-label" htmlFor="telephone">Téléphone</label>
          <input id="telephone" className="rg-input" value={v.telephone} onChange={set("telephone")} placeholder="+33 6 12 34 56 78" />
        </div>

        {err && <div className="rg-alert rg-alert--error full">{err}</div>}

        <div className="register-card__actions full">
          <a className="btn btn--ghost" href="/login">J’ai déjà un compte</a>
          <button className="btn btn--primary" type="submit" disabled={!canSubmit || loading}>
            {loading ? "Création..." : "Créer mon compte"}
          </button>
        </div>
      </form>
    </section>
  );
}
