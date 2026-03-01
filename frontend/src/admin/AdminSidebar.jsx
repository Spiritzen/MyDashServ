// src/admin/AdminSidebar.jsx
import { NavLink } from "react-router-dom";
import { useAuth } from "@/context/authContext";
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";

const items = [
  { key: "dashboard",   label: "Tableau de bord", to: "/admin" },
  { key: "sessions",    label: "Sessions / Planning", to: "/admin/sessions" },
  { key: "formations",  label: "Formations", to: "/admin/formations" },
  { key: "formateurs",  label: "Formateurs", to: "/admin/formateurs" }, // pastille ici
  { key: "gestionnaires", label: "Gestionnaires", to: "/admin/gestionnaires" },
  { key: "competences", label: "Compétences", to: "/admin/competences" },
  { key: "dispos",      label: "Disponibilités & Congés", to: "/admin/dispos-conges" },
  { key: "affectations",label: "Affectations (matching & conflits)", to: "/admin/affectations" },
  { key: "rapports",    label: "Rapports / Heures", to: "/admin/rapports" },
  { key: "profil",      label: "Mon compte / Sécurité", to: "/admin/profil" },
];

function parseCountPayload(data) {
  if (typeof data === "number") return data;
  if (data && typeof data.count === "number") return data.count;
  if (data && typeof data.value === "number") return data.value;
  // certains backends renvoient { data: { count: n } }
  if (data && data.data && typeof data.data.count === "number") return data.data.count;
  return 0;
}

export default function AdminSidebar() {
  const { user } = useAuth();
  const firstName = (user?.fullName || user?.email || "Admin").trim().split(/\s+/)[0];

  const [pendingCount, setPendingCount] = useState(0);

  async function refreshBadge() {
    try {
      const { data } = await axiosClient.get("/api/admin/formateurs/count-pending");
      setPendingCount(parseCountPayload(data));
    } catch {
      setPendingCount(0);
    }
  }

  useEffect(() => {
    let alive = true;

    // premier fetch
    refreshBadge();

    // rafraîchit toutes les 30s
    const t = setInterval(() => { if (alive) refreshBadge(); }, 30000);

    // rafraîchit au retour sur l’onglet
    const onFocus = () => { if (alive) refreshBadge(); };
    window.addEventListener("focus", onFocus);

    return () => { alive = false; clearInterval(t); window.removeEventListener("focus", onFocus); };
  }, []);

  return (
    <nav className="admin-side">
      <div className="admin-side__brand">
        <div className="admin-side__logo">🛡️</div>
        <div className="admin-side__title">Espace Admin</div>
      </div>

      <div className="admin-side__who">
        <small>Connecté :</small>
        <span className="admin-side__name">{firstName}</span>
      </div>

      <ul className="admin-side__menu">
        {items.map((it) => (
          <li key={it.key}>
            <NavLink
              to={it.to}
              className={({ isActive }) => "admin-side__link" + (isActive ? " is-active" : "")}
              end={it.to === "/admin"}
            >
              {it.label}
              {it.key === "formateurs" && pendingCount > 0 && (
                <span
                  style={{
                    marginLeft: 8,
                    minWidth: 20,
                    height: 20,
                    padding: "0 6px",
                    borderRadius: 999,
                    background: "#E11D48",
                    color: "white",
                    display: "inline-flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontSize: 12,
                    fontWeight: 700,
                  }}
                  aria-label={`${pendingCount} candidatures en attente`}
                  title={`${pendingCount} candidatures en attente`}
                >
                  {pendingCount}
                </span>
              )}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
