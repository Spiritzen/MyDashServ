// src/components/login/LoginRight.jsx
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axiosClient from "@/api/axiosClient";
import { useAuth } from "@/context/authContext";

function normalizeRole(role) {
  if (!role) return "";
  const r = role.toUpperCase();
  return r.startsWith("ROLE_") ? r.slice(5) : r; // "ROLE_FORMATEUR" -> "FORMATEUR"
}

function landingPathFor(user) {
  const role = normalizeRole(user?.role || (Array.isArray(user?.roles) ? user.roles[0] : ""));
  if (role === "ADMIN" || role === "GESTIONNAIRE") return "/admin";
  // défaut = formateur
  return "/formateur";
}

export default function LoginRight() {
  const nav = useNavigate();
  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  async function onSubmit(e) {
    e.preventDefault();
    setErr("");
    setLoading(true);
    try {
      const { data } = await axiosClient.post("/api/auth/login", { email, password });
      // { token, user }
      login(data.user, data.token);
      nav(landingPathFor(data.user));
    } catch (e) {
      setErr(e?.response?.data?.message || "Échec de connexion");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-card">
      <header className="auth-card__header">
        <h1>Connexion</h1>
        <p className="muted">Accédez à votre espace sécurisé</p>
      </header>

      <form onSubmit={onSubmit} className="auth-form" noValidate>
        <label className="field">
          <span>Email</span>
          <input
            className="input"
            type="email"
            autoComplete="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="vous@exemple.com"
          />
        </label>

        <label className="field">
          <span>Mot de passe</span>
          <div className="input input--with-btn">
            <input
              type={showPwd ? "text" : "password"}
              autoComplete="current-password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
            />
            <button
              type="button"
              onClick={() => setShowPwd((v) => !v)}
              aria-label={showPwd ? "Masquer le mot de passe" : "Afficher le mot de passe"}
            >
              {showPwd ? "Masquer" : "Afficher"}
            </button>
          </div>
        </label>

        {err && <div className="auth-error">{err}</div>}

        <button className="btn btn--primary" type="submit" disabled={loading}>
          {loading ? "Connexion..." : "Se connecter"}
        </button>

        <div className="auth-actions">
          <Link to="/register">Pas encore inscrit ?</Link>
          <span className="sep">·</span>
          <Link to="/">Mot de passe perdu</Link>
        </div>
      </form>

      <footer className="auth-card__footer muted">
        <small>
          En vous connectant, vous acceptez nos{" "}
          <Link to="/mentions">mentions légales</Link> et notre{" "}
          <Link to="/confidentialite">politique de confidentialité</Link>.
        </small>
      </footer>
    </div>
  );
}
