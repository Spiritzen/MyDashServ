// src/formateur/ProfilRight.jsx
import { useEffect, useMemo, useRef, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/styles/formateur-profil.css";

const emptyUser = {
  idUtilisateur: null,
  email: "",
  nom: "",
  prenom: "",
  telephone: "",
  adresse: "",
  ville: "",
  codePostal: "",
  photoUrl: null,
  compteValide: undefined,
  emailVerifie: undefined,
};

// ---- helper: make absolute file URL (http://localhost:8080/files/...) ----
const API_BASE =
  (import.meta.env?.VITE_API_BASE || axiosClient?.defaults?.baseURL || "").replace(/\/+$/, "");
function absFileUrl(url) {
  if (!url) return null;
  if (/^https?:\/\//i.test(url)) return url;        // already absolute
  if (url.startsWith("/files/")) return `${API_BASE}${url}`;
  return `${API_BASE}/${url.replace(/^\/+/, "")}`;  // fallback
}

export default function ProfilRight() {
  const [me, setMe] = useState(emptyUser);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState("");

  // upload
  const [uploading, setUploading] = useState(false);
  const [localPreview, setLocalPreview] = useState(null);
  const fileInputRef = useRef(null);

  // états d'édition par section
  const [edit, setEdit] = useState({ identite: false, contact: false });

  // brouillons locaux
  const [draft, setDraft] = useState({
    nom: "",
    prenom: "",
    telephone: "",
    adresse: "",
    ville: "",
    codePostal: "",
  });

  // charge /api/users/me
  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        setLoading(true);
        const { data } = await axiosClient.get("/api/users/me");
        if (!alive) return;
        setMe({
          idUtilisateur: data.idUtilisateur ?? null,
          email: data.email || "",
          nom: data.nom || "",
          prenom: data.prenom || "",
          telephone: data.telephone || "",
          adresse: data.adresse || "",
          ville: data.ville || "",
          codePostal: data.codePostal || "",
          photoUrl: data.photoUrl || null,  // <= backend returns "/files/users/..."
          compteValide: data.compteValide,
          emailVerifie: data.emailVerifie,
        });
      } catch (e) {
        setErr(e?.response?.data?.message || "Impossible de charger votre profil.");
      } finally {
        setLoading(false);
      }
    })();
    return () => { alive = false; };
  }, []);

  const startEdit = (section) => {
    if (section === "identite") {
      setDraft((d) => ({ ...d, nom: me.nom || "", prenom: me.prenom || "" }));
      setEdit((e) => ({ ...e, identite: true }));
    }
    if (section === "contact") {
      setDraft((d) => ({
        ...d,
        telephone: me.telephone || "",
        adresse: me.adresse || "",
        ville: me.ville || "",
        codePostal: me.codePostal || "",
      }));
      setEdit((e) => ({ ...e, contact: true }));
    }
  };

  const cancelEdit = (section) => {
    if (section === "identite") setEdit((e) => ({ ...e, identite: false }));
    if (section === "contact") setEdit((e) => ({ ...e, contact: false }));
    setErr("");
  };

  const onChange = (k) => (e) => setDraft((d) => ({ ...d, [k]: e.target.value }));

  const saveSection = async (section) => {
    setErr("");
    setSaving(true);
    try {
      let payload = {};
      if (section === "identite") {
        payload = { nom: draft.nom?.trim(), prenom: draft.prenom?.trim() };
      }
      if (section === "contact") {
        payload = {
          telephone: (draft.telephone || "").trim() || null,
          adresse: (draft.adresse || "").trim() || null,
          ville: (draft.ville || "").trim() || null,
          codePostal: (draft.codePostal || "").trim() || null,
        };
      }
      const { data } = await axiosClient.put("/api/users/me", payload);
      setMe((prev) => ({
        ...prev,
        email: data.email ?? prev.email,
        nom: data.nom ?? prev.nom,
        prenom: data.prenom ?? prev.prenom,
        telephone: data.telephone ?? prev.telephone,
        adresse: data.adresse ?? prev.adresse,
        ville: data.ville ?? prev.ville,
        codePostal: data.codePostal ?? prev.codePostal,
        compteValide: data.compteValide ?? prev.compteValide,
        emailVerifie: data.emailVerifie ?? prev.emailVerifie,
      }));
      cancelEdit(section);
    } catch (e) {
      setErr(e?.response?.data?.message || "Échec de l’enregistrement.");
    } finally {
      setSaving(false);
    }
  };

  // --- Upload photo (self) ---
  const pickFile = () => fileInputRef.current?.click();
  const onFileSelected = async (e) => {
    const f = e.target.files?.[0];
    if (!f) return;

    const blobUrl = URL.createObjectURL(f);
    setLocalPreview(blobUrl);

    setUploading(true);
    setErr("");
    try {
      const fd = new FormData();
      fd.append("file", f);
      const { data } = await axiosClient.post("/api/users/me/photo", fd, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setMe((prev) => ({ ...prev, photoUrl: data.photoUrl || null }));
    } catch (e2) {
      setErr(e2?.response?.data?.message || "Téléversement de la photo échoué.");
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = "";
      setTimeout(() => { URL.revokeObjectURL(blobUrl); setLocalPreview(null); }, 250);
    }
  };

  const badges = useMemo(() => {
    const arr = [];
    if (me.emailVerifie === true) arr.push({ label: "Email vérifié", tone: "ok" });
    if (me.compteValide === true) arr.push({ label: "Compte validé", tone: "ok" });
    if (me.compteValide === false) arr.push({ label: "Candidature en attente", tone: "warn" });
    return arr;
  }, [me.emailVerifie, me.compteValide]);

  if (loading) {
    return (
      <div className="card panel">
        <h2 style={{ margin: 0 }}>Mon profil</h2>
        <p className="muted">Chargement…</p>
      </div>
    );
  }

  // Image affichée : preview locale sinon URL serveur (avec cache-busting)
  const avatarSrc =
    localPreview || (me.photoUrl ? `${absFileUrl(me.photoUrl)}?v=${Date.now()}` : null);

  return (
    <div className="profil">
      <header className="profil__head">
        <h1>Mon profil</h1>
        <div className="badge-row">
          {badges.map((b) => (
            <span key={b.label} className={`badge badge--${b.tone}`}>{b.label}</span>
          ))}
        </div>
      </header>

      {err && <div className="rg-alert rg-alert--error">{err}</div>}

      {/* Identité */}
      <section className="card panel">
        <div className="panel__head">
          <h2>Identité</h2>

          <div className="avatar-actions">
            <button className="btn btn--ghost" onClick={pickFile} disabled={uploading}>
              {me.photoUrl ? "Changer de photo" : "Téléverser une photo"}
            </button>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/png,image/jpeg,image/webp"
              style={{ display: "none" }}
              onChange={onFileSelected}
            />
          </div>

          {!edit.identite ? (
            <button className="icon-btn" onClick={() => startEdit("identite")} aria-label="Modifier identité">
              ✏️ Editer
            </button>
          ) : (
            <div className="panel__actions">
              <button className="btn btn--ghost" onClick={() => cancelEdit("identite")} disabled={saving}>Annuler</button>
              <button className="btn btn--primary" onClick={() => saveSection("identite")} disabled={saving}>
                {saving ? "Enregistrement…" : "Enregistrer"}
              </button>
            </div>
          )}
        </div>

        {/* Contenu identité : infos à gauche / avatar à droite */}
        <div className="identite-grid">
          <div className="identite-grid__left">
            {!edit.identite ? (
              <dl className="kv">
                <div className="kv__row"><dt>Nom</dt><dd>{me.nom || "—"}</dd></div>
                <div className="kv__row"><dt>Prénom</dt><dd>{me.prenom || "—"}</dd></div>
                <div className="kv__row"><dt>Email</dt><dd>{me.email || "—"}</dd></div>
              </dl>
            ) : (
              <div className="grid2">
                <label className="field">
                  <span>Nom *</span>
                  <input className="input" value={draft.nom} onChange={onChange("nom")} required />
                </label>
                <label className="field">
                  <span>Prénom *</span>
                  <input className="input" value={draft.prenom} onChange={onChange("prenom")} required />
                </label>
                <div className="help full muted">L’email n’est pas modifiable ici.</div>
              </div>
            )}
          </div>

          <div className="identite-grid__right">
            {!!avatarSrc && (
              <div className="avatar avatar--lg" aria-label="Votre photo de profil">
                <img src={avatarSrc} alt="" />
                {uploading && <div className="avatar__busy">…</div>}
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Coordonnées */}
      <section className="card panel">
        <div className="panel__head">
          <h2>Coordonnées</h2>
          {!edit.contact ? (
            <button className="icon-btn" onClick={() => startEdit("contact")} aria-label="Modifier coordonnées">
              ✏️ Editer
            </button>
          ) : (
            <div className="panel__actions">
              <button className="btn btn--ghost" onClick={() => cancelEdit("contact")} disabled={saving}>Annuler</button>
              <button className="btn btn--primary" onClick={() => saveSection("contact")} disabled={saving}>
                {saving ? "Enregistrement…" : "Enregistrer"}
              </button>
            </div>
          )}
        </div>

        {!edit.contact ? (
          <dl className="kv">
            <div className="kv__row"><dt>Téléphone</dt><dd>{me.telephone || "—"}</dd></div>
            <div className="kv__row full"><dt>Adresse</dt><dd>{me.adresse || "—"}</dd></div>
            <div className="kv__row"><dt>Ville</dt><dd>{me.ville || "—"}</dd></div>
            <div className="kv__row"><dt>Code postal</dt><dd>{me.codePostal || "—"}</dd></div>
          </dl>
        ) : (
          <div className="grid2">
            <label className="field">
              <span>Téléphone</span>
              <input className="input" value={draft.telephone} onChange={onChange("telephone")} placeholder="+33 6 …" />
            </label>
            <label className="field full">
              <span>Adresse</span>
              <input className="input" value={draft.adresse} onChange={onChange("adresse")} placeholder="12 rue des Tulipes" />
            </label>
            <label className="field">
              <span>Ville</span>
              <input className="input" value={draft.ville} onChange={onChange("ville")} placeholder="Amiens" />
            </label>
            <label className="field">
              <span>Code postal</span>
              <input className="input" value={draft.codePostal} onChange={onChange("codePostal")} placeholder="80000" />
            </label>
          </div>
        )}
      </section>

      {/* Compte */}
      <section className="card panel">
        <div className="panel__head">
          <h2>Compte</h2>
        </div>
        <dl className="kv">
          <div className="kv__row"><dt>Email vérifié</dt><dd>{me.emailVerifie === true ? "Oui" : "Non"}</dd></div>
          <div className="kv__row"><dt>Candidature</dt><dd>{me.compteValide === true ? "Validée" : "En attente"}</dd></div>
        </dl>
        <div className="muted" style={{ marginTop: 8 }}>
          Besoin de changer d’email ou de mot de passe ? Rendez-vous dans <strong>Sécurité</strong>.
        </div>
      </section>
    </div>
  );
}
