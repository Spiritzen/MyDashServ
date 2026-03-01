import { useEffect, useRef } from "react";
import { createPortal } from "react-dom";
import "./Modal.css";

export default function Modal({ open, title, onClose, children, footer }) {
  const firstFocusRef = useRef(null);

  // lock scroll body
  useEffect(() => {
    if (open) document.body.classList.add("body--lock");
    return () => document.body.classList.remove("body--lock");
  }, [open]);

  // ESC pour fermer + focus initial
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => { if (e.key === "Escape") onClose?.(); };
    window.addEventListener("keydown", onKey);
    // focus premier élément focusable
    setTimeout(() => firstFocusRef.current?.focus(), 0);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, onClose]);

  if (!open) return null;

  return createPortal(
    <div className="modal-root" role="dialog" aria-modal="true" aria-label={title}>
      <div className="modal-overlay" onClick={onClose} />
      <div className="modal-sheet" onClick={(e)=>{ if(e.target===e.currentTarget) onClose?.(); }}>
        <section className="modal-window">
          <header className="modal-header">
            <div className="modal-title">{title}</div>
            <button className="modal-close" onClick={onClose} aria-label="Fermer">
              <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                <path d="M18.3 5.7 12 12l-6.3-6.3-1.4 1.4L10.6 13l-6.3 6.3 1.4 1.4L12 14.4l6.3 6.3 1.4-1.4-6.3-6.3 6.3-6.3-1.4-1.4z" fill="currentColor"/>
              </svg>
            </button>
          </header>

          <div className="modal-body">
            {/* ref placé sur le premier champ fourni */}
            <div ref={firstFocusRef} tabIndex={-1} style={{outline:"none"}} />
            {children}
          </div>

          <footer className="modal-footer">
            {footer}
          </footer>
        </section>
      </div>
    </div>,
    document.body
  );
}
