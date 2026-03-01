// src/formateur/FormateurSidebar.jsx
import { NavLink } from "react-router-dom";
import { useAuth } from "@/context/authContext";
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";

const items = [
  { key: "dashboard",  label: "Tableau de bord",         to: "/formateur" },
  { key: "profil",     label: "Mon profil",              to: "/formateur/profil" },
  { key: "competences",label: "Mes compétences",         to: "/formateur/competences" },
  { key: "dispos",     label: "Disponibilités & Congés", to: "/formateur/dispos-conges" },
  { key: "formations", label: "Mes formations",          to: "/formateur/formations" },
  { key: "candidature",label: "Candidature",             to: "/formateur/candidature" },
  { key: "documents",  label: "Documents",               to: "/formateur/documents" },
  { key: "securite",   label: "Sécurité",                to: "/formateur/securite" },
];

// Préfixe les chemins /files/** avec l’origine API (évite de taper le port Vite)
function getFileUrl(u) {
  if (!u) return null;
  if (/^https?:\/\//i.test(u)) return u; // déjà absolu
  const api = axiosClient?.defaults?.baseURL?.replace(/\/+$/, "") || "";
  return api + u; // ex: http://localhost:8080 + /files/users/abc.jpg
}

export default function FormateurSidebar() {
  const { user } = useAuth();
  const firstName = (user?.fullName || user?.email || "Formateur").trim().split(/\s+/)[0];

  const [photoUrl, setPhotoUrl] = useState(null);

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        const { data } = await axiosClient.get("/api/users/me");
        if (!alive) return;
        if (data?.photoUrl) setPhotoUrl(getFileUrl(data.photoUrl));
      } catch (e) {
        console.debug("Sidebar avatar fetch skipped:", e?.message || e);
      }
    })();
    return () => { alive = false; };
  }, []);

  return (
    <nav className="form-side">
      <div className="form-side__brand">
        <div className="form-side__logo">🎓</div>
        <div className="form-side__title">Espace Formateur</div>
      </div>

      <div className="form-side__who">
        <small>Connecté :</small>
        <span className="form-side__name">
          {firstName}
          {photoUrl && (
            <img
              className="form-side__avatar"
              src={`${photoUrl}?v=${Date.now()}`} // cache-busting après upload
              alt=""
            />
          )}
        </span>
      </div>

      <ul className="form-side__menu">
        {items.map((it) => (
          <li key={it.key}>
            <NavLink
              to={it.to}
              className={({ isActive }) => "form-side__link" + (isActive ? " is-active" : "")}
              end={it.to === "/formateur"}
            >
              {it.label}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
