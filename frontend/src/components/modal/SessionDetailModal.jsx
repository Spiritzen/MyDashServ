import { useEffect, useMemo, useState } from "react";
import Modal from "@/components/modal/Modal";
import axiosClient from "@/api/axiosClient";

function fmt(dt) {
  if (!dt) return "—";
  const d = new Date(dt);
  return d.toLocaleString();
}

// Convertit une date ISO (ou LocalDateTime sérialisé) en valeur <input type="datetime-local">
function toLocalInput(dt) {
  if (!dt) return "";
  const d = new Date(dt);
  // yyyy-MM-ddTHH:mm sans secondes
  const pad = (n) => String(n).padStart(2, "0");
  const v = `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  return v;
}

export default function SessionDetailModal({ open, sessionId, onClose, onUpdated }) {
  const [loading, setLoading] = useState(false);
  const [session, setSession] = useState(null);
  const [err, setErr] = useState("");
  const [edit, setEdit] = useState(false);

  // Form local pour l’édition de dates
  const [dateDebut, setDateDebut] = useState("");
  const [dateFin, setDateFin] = useState("");

  useEffect(() => {
    if (!open || !sessionId) return;
    (async () => {
      setLoading(true);
      setErr("");
      setEdit(false);
      try {
        const { data } = await axiosClient.get(`/api/admin/sessions/${sessionId}`);
        setSession(data);
        setDateDebut(toLocalInput(data?.dateDebut));
        setDateFin(toLocalInput(data?.dateFin));
      } catch (e) {
        console.error("[SessionDetailModal] load error:", e);
        setErr(e?.response?.data?.message || e.message || "Erreur lors du chargement.");
      } finally {
        setLoading(false);
      }
    })();
  }, [open, sessionId]);

  const canEdit = useMemo(() => {
    const st = session?.statut;
    return st !== "AFFECTEE" && st !== "TERMINEE" && st !== "ANNULEE";
  }, [session]);

  const canSave = useMemo(() => {
    if (!edit) return false;
    if (!dateDebut || !dateFin) return false;
    const sd = new Date(dateDebut).getTime();
    const ed = new Date(dateFin).getTime();
    return !Number.isNaN(sd) && !Number.isNaN(ed) && ed > sd;
  }, [edit, dateDebut, dateFin]);

  const onSave = async () => {
    if (!session) return;
    try {
      setLoading(true);
      setErr("");

      // Le back attend un SessionSaveDTO complet : on renvoie les valeurs inchangées + dates modifiées.
      await axiosClient.put(`/api/admin/sessions/${sessionId}`, {
        formationId: session.formationId ?? session.formation?.id ?? session.formation_id, // garde l’id formation
        dateDebut, // IMPORTANT: garder le format "yyyy-MM-dd'T'HH:mm"
        dateFin,
        mode: session.mode,
        ville: session.ville ?? null,
        salle: session.salle ?? null
      });

      setEdit(false);
      // recharge la session affichée
      const { data } = await axiosClient.get(`/api/admin/sessions/${sessionId}`);
      setSession(data);
      setDateDebut(toLocalInput(data?.dateDebut));
      setDateFin(toLocalInput(data?.dateFin));
      onUpdated?.();
    } catch (e) {
      console.error("[SessionDetailModal] save error:", e);
      setErr(e?.response?.data?.message || e.message || "Échec de l’enregistrement.");
    } finally {
      setLoading(false);
    }
  };

  const handleCancelSession = async () => {
    if (!sessionId) return;
    const ok = window.confirm("Confirmer l’annulation de cette session ?");
    if (!ok) return;
    setLoading(true);
    try {
      await axiosClient.post(`/api/admin/sessions/${sessionId}/cancel`);
      onUpdated?.();
      // on recharge l’état local
      const { data } = await axiosClient.get(`/api/admin/sessions/${sessionId}`);
      setSession(data);
      setEdit(false);
    } catch (e) {
      console.error("[SessionDetailModal] cancel error:", e);
      setErr(e?.response?.data?.message || e.message || "Échec de l’annulation.");
    } finally {
      setLoading(false);
    }
  };

  const s = session || {};
  const formationName = s.formationLabel || s.formationNom || s.formation?.nom || "—";
  const formateurName =
    s.formateurLabel ||
    s.formateurNomComplet ||
    (s.formateur ? `${s.formateur.prenom || ""} ${s.formateur.nom || ""}`.trim() : "") ||
    "—";

  return (
    <Modal
      open={!!open}
      onClose={onClose}
      title={`Détails de la session #${sessionId ?? ""}`}
      width={820}
      footer={
        <div style={{ display: "flex", gap: 8, justifyContent: "space-between", alignItems: "center" }}>
          <span className="muted">{s.statut ? `Statut : ${s.statut}` : ""}</span>
          <div>
            {!edit && canEdit && (
              <button className="btn btn--ghost" onClick={() => setEdit(true)} disabled={loading}>
                Modifier
              </button>
            )}
            {edit && (
              <>
                <button className="btn btn--ghost" onClick={() => { setEdit(false); setErr(""); setDateDebut(toLocalInput(s.dateDebut)); setDateFin(toLocalInput(s.dateFin)); }} disabled={loading}>
                  Annuler
                </button>
                <button className="btn" onClick={onSave} disabled={loading || !canSave}>
                  Enregistrer
                </button>
              </>
            )}
            <button className="btn btn--ghost" onClick={onClose} disabled={loading} style={{ marginLeft: 6 }}>
              Fermer
            </button>
            <button
              className="btn btn--ghost"
              onClick={handleCancelSession}
              disabled={loading || s.statut === "ANNULEE"}
              style={{ marginLeft: 6 }}
            >
              Annuler la session
            </button>
          </div>
        </div>
      }
    >
      {loading && <div className="muted" style={{ padding: 8 }}>Chargement…</div>}
      {err && <div className="alert alert--error">{err}</div>}

      {!loading && !err && (
        <div className="vstack" style={{ gap: 12 }}>
          <section className="card">
            <h4>Informations générales</h4>
            <div className="grid" style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
              <div><strong>Formation :</strong> {formationName}</div>
              <div><strong>Mode :</strong> {s.mode || "—"}</div>

              {!edit ? (
                <>
                  <div><strong>Début :</strong> {fmt(s.dateDebut)}</div>
                  <div><strong>Fin :</strong> {fmt(s.dateFin)}</div>
                </>
              ) : (
                <>
                  <label style={{ display: "flex", flexDirection: "column", gap: 4 }}>
                    <strong>Début :</strong>
                    <input type="datetime-local" value={dateDebut} onChange={(e)=>setDateDebut(e.target.value)} />
                  </label>
                  <label style={{ display: "flex", flexDirection: "column", gap: 4 }}>
                    <strong>Fin :</strong>
                    <input type="datetime-local" value={dateFin} onChange={(e)=>setDateFin(e.target.value)} />
                  </label>
                </>
              )}

              <div><strong>Ville :</strong> {s.ville || "—"}</div>
              <div><strong>Salle :</strong> {s.salle || "—"}</div>
            </div>
          </section>

          <section className="card">
            <h4>Formateur</h4>
            <div><strong>Nom :</strong> {formateurName}</div>
            {s.formateur?.email && <div><strong>Email :</strong> {s.formateur.email}</div>}
            {s.formateur?.telephone && <div><strong>Téléphone :</strong> {s.formateur.telephone}</div>}
          </section>

          {s.formation?.description && (
            <section className="card">
              <h4>Description de la formation</h4>
              <p className="muted" style={{ whiteSpace: "pre-wrap" }}>{s.formation.description}</p>
            </section>
          )}
        </div>
      )}
    </Modal>
  );
}
