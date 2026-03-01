import { useState } from "react";

export default function Footer() {
  const year = new Date().getFullYear();
  const [open, setOpen] = useState(false);

  return (
    <footer className={`footer ${open ? "is-open" : ""}`}>
      <div className="footer__grid">
        {/* Colonne gauche */}
        <div className="footer__left">
          <span>© {year} MyDashServ</span>
        </div>

        {/* Colonne milieu — équipe + GitHub (desktop) */}
        <div className="footer__center">
          <picture>
            <source srcSet="/img/gitlogo.svg" type="image/svg+xml" />
            <img src="/img/gitlogo.png" alt="GitHub" className="gh-logo" />
          </picture>
          <span className="devs-title">Dev :</span>
      
          <a className="dev-link" href="https://github.com/Spiritzen" target="_blank" rel="noreferrer noopener">Seb</a>
        </div>

        {/* Colonne droite (desktop) */}
        <nav className="footer__links">
          <a href="/mentions">Mentions légales</a>
          <a href="/confidentialite">Confidentialité</a>
          <a href="/contact">Contact</a>
        </nav>

        {/* Bouton hamburger (mobile only – géré via CSS) */}
        <button
          className="footer__toggle"
          aria-label="Ouvrir le menu du footer"
          aria-controls="footer-drawer"
          aria-expanded={open}
          onClick={() => setOpen(v => !v)}
        >
          {!open ? (
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M3 6h18v2H3zm0 6h18v2H3zm0 6h18v2H3z" fill="currentColor"/>
            </svg>
          ) : (
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M18.3 5.7 12 12l-6.3-6.3-1.4 1.4L10.6 13l-6.3 6.3 1.4 1.4L12 14.4l6.3 6.3 1.4-1.4-6.3-6.3 6.3-6.3-1.4-1.4z" fill="currentColor"/>
            </svg>
          )}
        </button>
      </div>

      {/* Drawer mobile — se déploie vers le haut */}
      <div id="footer-drawer" className="footer__drawer" aria-hidden={!open}>
        <nav className="footer__drawer-links">
          <a href="/mentions">Mentions légales</a>
          <a href="/confidentialite">Confidentialité</a>
          <a href="/contact">Contact</a>
        </nav>
        <div className="footer__drawer-devs">
          <picture>
            <source srcSet="/img/gitlogo.svg" type="image/svg+xml" />
            <img src="/img/gitlogo.png" alt="GitHub" className="gh-logo" />
          </picture>
          <span className="devs-title">Dev :</span>
          <a className="dev-link" href="https://github.com/Laeti" target="_blank" rel="noreferrer noopener">Laeti</a>
          <span className="sep">·</span>
          <a className="dev-link" href="https://github.com/Doudou" target="_blank" rel="noreferrer noopener">Doudou</a>
          <span className="sep">·</span>
          <a className="dev-link" href="https://github.com/Spiritzen" target="_blank" rel="noreferrer noopener">Seb</a>
        </div>
      </div>
    </footer>
  );
}
