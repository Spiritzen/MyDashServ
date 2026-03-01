// src/public/VerifyEmailResult.jsx
import { useSearchParams, useNavigate } from "react-router-dom";
import { useEffect } from "react";

export default function VerifyEmailResult() {
  const [sp] = useSearchParams();
  const nav = useNavigate();
  const ok = sp.get("status") === "success";

  useEffect(() => {
    const t = setTimeout(() => nav("/formateur?tab=candidature"), 1200);
    return () => clearTimeout(t);
  }, [nav]);

  return (
    <div className="card panel">
      <h2>Vérification d’email</h2>
      {ok
        ? <div className="rg-alert rg-alert--success">Votre email est vérifié ✅</div>
        : <div className="rg-alert rg-alert--error">Lien invalide ou expiré ❌</div>}
    </div>
  );
}
