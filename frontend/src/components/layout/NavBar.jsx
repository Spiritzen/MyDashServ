import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "@/context/authContext";
import { useState } from "react";

function normalizeRole(user) {
  const raw = (user?.role || (Array.isArray(user?.roles) ? user.roles[0] : "") || "").toString().toUpperCase();
  return raw.startsWith("ROLE_") ? raw.slice(5) : raw; // "ROLE_FORMATEUR" -> "FORMATEUR"
}

export default function NavBar() {
  const { user, isAdmin, logout } = useAuth();
  const nav = useNavigate();
  const [open, setOpen] = useState(false);

  const role = normalizeRole(user);
  const isAdminLike = isAdmin || role === "ADMIN" || role === "GESTIONNAIRE";
  const isFormateur = role === "FORMATEUR";

  const onLogout = () => {
    logout();
    nav("/", { replace: true });
    setOpen(false);
  };

  const closeMenu = () => setOpen(false);

  return (
    <header className={`nav ${open ? "is-open" : ""}`}>
      <div className="nav__inner">
        <Link className="nav__brand" to="/" onClick={closeMenu}>
          <img src="/img/logo.png" alt="" />
          <span>MyDashServ</span>
        </Link>

        {/* Liens desktop */}
        <nav className="nav__links" aria-label="Primary">
          <NavLink to="/">Accueil</NavLink>
          <NavLink to="/formations">Formations</NavLink>
          <NavLink to="/formateurs">Formateurs</NavLink>
          <NavLink to="/features">Fonctionnalités</NavLink>
          <NavLink to="/contact">Contact</NavLink>
        </nav>

        {/* Actions desktop */}
        {user ? (
          <div className="nav__actions">
            {isAdminLike && (
              <NavLink className="nav__cta nav__cta--ghost" to="/admin">
                Admin
              </NavLink>
            )}
            {isFormateur && !isAdminLike && (
              <NavLink className="nav__cta nav__cta--ghost" to="/formateur">
                Mon compte
              </NavLink>
            )}
            <button className="nav__cta" onClick={onLogout}>
              Se déconnecter
            </button>
          </div>
        ) : (
          <Link className="nav__cta" to="/login">
            Se connecter
          </Link>
        )}

        {/* Burger mobile */}
        <button
          className="nav__burger"
          aria-label="Ouvrir le menu"
          aria-expanded={open}
          aria-controls="nav-drawer"
          onClick={() => setOpen((v) => !v)}
        >
          {!open ? (
            <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
              <path d="M3 6h18v2H3zM3 11h18v2H3zM3 16h18v2H3z" fill="currentColor" />
            </svg>
          ) : (
            <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
              <path d="M18.3 5.71L12 12.01 5.7 5.7 4.3 7.11l6.3 6.29-6.3 6.3 1.41 1.41L12 14.83l6.29 6.3 1.41-1.41-6.3-6.3 6.3-6.29z" fill="currentColor"/>
            </svg>
          )}
        </button>
      </div>

      {/* Drawer mobile */}
      <div id="nav-drawer" className="nav__drawer" role="dialog" aria-modal="true">
        <nav className="nav__drawer-links">
          <NavLink to="/" onClick={closeMenu}>Accueil</NavLink>
          <NavLink to="/formations" onClick={closeMenu}>Formations</NavLink>
          <NavLink to="/formateurs" onClick={closeMenu}>Formateurs</NavLink>
          <NavLink to="/features" onClick={closeMenu}>Fonctionnalités</NavLink>
          <NavLink to="/contact" onClick={closeMenu}>Contact</NavLink>
        </nav>

        <div className="nav__drawer-actions">
          {user ? (
            <>
              {isAdminLike && (
                <NavLink
                  className="nav__cta nav__cta--ghost nav__cta--sm"
                  to="/admin"
                  onClick={closeMenu}
                >
                  Admin
                </NavLink>
              )}
              {isFormateur && !isAdminLike && (
                <NavLink
                  className="nav__cta nav__cta--ghost nav__cta--sm"
                  to="/formateur"
                  onClick={closeMenu}
                >
                  Mon compte
                </NavLink>
              )}
              <button className="nav__cta nav__cta--sm" onClick={onLogout}>
                Se déconnecter
              </button>
            </>
          ) : (
            <Link className="nav__cta nav__cta--sm" to="/login" onClick={closeMenu}>
              Se connecter
            </Link>
          )}
        </div>
      </div>

      {/* Overlay clicable */}
      <button
        className="nav__overlay"
        aria-hidden={!open}
        tabIndex={-1}
        onClick={closeMenu}
      />
    </header>
  );
}
