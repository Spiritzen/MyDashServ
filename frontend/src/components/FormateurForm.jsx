import { useState } from "react";
import axios from "../api/axiosClient";

export default function FormateurForm() {
  const [form, setForm] = useState({
    nom: "",
    prenom: "",
    email: "",
    codePostal: "",
    ville: "",
    telephone: "",
  });
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState(null);

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    if (loading) return;

    setLoading(true);
    setMsg(null);

    try {
      const payload = {
        // champs nouvellement gérés côté back
        nom: form.nom?.trim() || null,
        prenom: form.prenom?.trim() || null,

        // requis
        email: form.email?.trim(),
        password: "Temp#12345",

        // on crée un utilisateur de rôle FORMATEUR
        role: "FORMATEUR",

        // optionnels
        codePostal: form.codePostal?.trim() || null,
        ville: form.ville?.trim() || null,
        telephone: form.telephone?.trim() || null,
        // adresse / photoPath laissés vides pour l’instant
      };

      const res = await axios.post("/api/users", payload);
      setMsg({ type: "success", text: `Formateur créé (id=${res.data.id})` });

      // reset du formulaire
      setForm({
        nom: "",
        prenom: "",
        email: "",
        codePostal: "",
        ville: "",
        telephone: "",
      });
    } catch (err) {
      const apiErr =
        err?.response?.data?.error ||
        err?.response?.data?.message ||
        err?.message ||
        "Erreur lors de la création";
      setMsg({ type: "error", text: apiErr });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 720, margin: "0 auto" }}>
      <h2 style={{ textAlign: "center", margin: "24px 0" }}>
        Ajouter un nouveau Formateur
      </h2>

      <form onSubmit={onSubmit} style={{ display: "grid", gap: 12 }}>
        <div style={{ display: "grid", gridTemplateColumns: "180px 1fr", gap: 8 }}>
          <label>Nom</label>
          <input
            name="nom"
            value={form.nom}
            onChange={onChange}
            placeholder="Nom"
          />

          <label>Prénom</label>
          <input
            name="prenom"
            value={form.prenom}
            onChange={onChange}
            placeholder="Prénom"
          />

          <label>Email*</label>
          <input
            name="email"
            type="email"
            required
            value={form.email}
            onChange={onChange}
            placeholder="email@domaine.com"
          />

          <label>Code Postal</label>
          <input
            name="codePostal"
            value={form.codePostal}
            onChange={onChange}
            placeholder="69000"
          />

          <label>Ville</label>
          <input
            name="ville"
            value={form.ville}
            onChange={onChange}
            placeholder="Lyon"
          />

          <label>Téléphone</label>
          <input
            name="telephone"
            value={form.telephone}
            onChange={onChange}
            placeholder="06 12 34 56 78"
          />
        </div>

        <button disabled={loading || !form.email} style={{ padding: "10px 18px", borderRadius: 8 }}>
          {loading ? "Ajout..." : "Ajouter"}
        </button>

        {msg && (
          <p style={{ color: msg.type === "success" ? "green" : "crimson", marginTop: 8 }}>
            {msg.text}
          </p>
        )}
        <p style={{ fontSize: 12, color: "#666" }}>
          * un mot de passe temporaire est posé : <code>Temp#12345</code>
        </p>
      </form>
    </div>
  );
}
