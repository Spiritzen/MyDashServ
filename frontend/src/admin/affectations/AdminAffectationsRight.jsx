import { useEffect, useMemo, useState, useCallback } from "react";
import axiosClient from "@/api/axiosClient";
import AffectationsFilter from "./AffectationsFilter";
import AffectationsTable from "./AffectationsTable";
import AffectationsModale from "./AffectationsModale";
import "./AffectationsRight.css";

export default function AdminAffectationsRight() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);

  // Filtres
  const [q, setQ] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");

  // Modale matching
  const [openMatch, setOpenMatch] = useState(false);
  const [selectedSession, setSelectedSession] = useState(null);

  const params = useMemo(() => {
    const p = { page: 0, size: 50, sort: "dateDebut,asc", statut: "PLANIFIEE" };
    if (q?.trim()) p.search = q.trim();
    if (from) p.from = from;
    if (to) p.to = to;
    return p;
  }, [q, from, to]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const { data } = await axiosClient.get("/api/admin/sessions", { params });
      setRows(data?.content ?? []);
    } catch (e) {
      console.error("[sessions] load failed:", e);
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [params]);

  useEffect(() => {
    load();
  }, [load]);

  const openCandidates = (session) => {
    setSelectedSession(session);
    setOpenMatch(true);
  };

  const onAssigned = () => {
    load();
  };

  const resetFilters = () => {
    setQ("");
    setFrom("");
    setTo("");
  };

  return (
    <section className="aff-right">
      <header className="aff-header">
      <div className="admin-head__left">
          <h1>Affectations</h1>
          <div className="muted">Matching & détection de conflits</div>
        </div>
      </header>

      <div className="aff-filter">
        <AffectationsFilter
          q={q} setQ={setQ}
          from={from} setFrom={setFrom}
          to={to} setTo={setTo}
          onReset={resetFilters}
        />
      </div>

      <div className="aff-content">
        <AffectationsTable
          rows={rows}
          loading={loading}
          onMatch={openCandidates}
        />
      </div>

      <AffectationsModale
        open={openMatch}
        session={selectedSession}
        onClose={() => setOpenMatch(false)}
        onAssigned={onAssigned}
      />
    </section>
  );
}
