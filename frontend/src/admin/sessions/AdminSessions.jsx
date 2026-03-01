import { useEffect, useMemo, useState, useCallback } from "react";
import axiosClient from "@/api/axiosClient";
import SessionFilters from "./SessionFilters";
import SessionTable from "./SessionTable";
import SessionCreateModal from "@/components/modal/SessionCreateModal";
import SessionDetailModal from "@/components/modal/SessionDetailModal";

export default function AdminSessions() {
  const [rows, setRows] = useState([]);

  // Pagination
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Filtres UI
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("");
  const [formationId, setFormationId] = useState("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  // Modales
  const [openCreate, setOpenCreate] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [detailId, setDetailId] = useState(null);

  // clé de rafraîchissement
  const [refreshKey, setRefreshKey] = useState(0);

  // ⚠️ L’API attend search, statut, formationId, from, to
  const params = useMemo(() => {
    const p = { page, size, sort: "dateDebut,asc" };
    if (query?.trim()) p.search = query.trim();
    if (status)        p.statut = status;
    if (formationId)   p.formationId = Number(formationId);
    if (fromDate)      p.from = fromDate;
    if (toDate)        p.to = toDate;
    return p;
  }, [page, size, query, status, formationId, fromDate, toDate]);

  const load = useCallback(async () => {
    const { data } = await axiosClient.get("/api/admin/sessions", { params });
    setRows(data?.content ?? []);
    setTotalPages(Math.max(1, data?.totalPages ?? 1));
    setTotalElements(data?.totalElements ?? 0);
  }, [params]);

  useEffect(() => { load(); }, [load, refreshKey]);

  const onSaved = () => {
    setOpenCreate(false);
    setPage(0);
    setRefreshKey(k => k + 1);
  };

  const openDetail = (id) => {
    setDetailId(id);
    setDetailOpen(true);
  };

  const handleCancel = async (id) => {
    if (!id) return;
    const ok = window.confirm("Confirmer l’annulation/suppression de cette session ?");
    if (!ok) return;
    await axiosClient.delete(`/api/admin/sessions/${id}`);

    const isLastOnPage = rows.length === 1 && page > 0;
    if (isLastOnPage) setPage(p => p - 1);

    setRefreshKey(k => k + 1);
    setDetailOpen(false);
  };

  const prevPage = () => setPage(p => Math.max(0, p - 1));
  const nextPage = () => setPage(p => Math.min(totalPages - 1, p + 1));
  const changeSize = (e) => { setSize(Number(e.target.value)); setPage(0); };

 return (
  <div className="admin-dash">
    {/* Header cohérent avec Formations */}
    <header className="admin-head">
      <div className="admin-head__left">
        <h1>Sessions / Planning</h1>
        <div className="muted">Gestion des sessions</div>
      </div>

      <div className="admin-head__right">
        <button className="btn btn--primary" onClick={() => setOpenCreate(true)}>
          Nouvelle session
        </button>
      </div>
    </header>

    <SessionFilters
      query={query} onQueryChange={setQuery}
      statut={status} onStatutChange={setStatus}
      formationId={formationId} onFormationChange={setFormationId}
      fromDate={fromDate} onFromChange={setFromDate}
      toDate={toDate} onToChange={setToDate}
    />

    <SessionTable
      rows={rows}
      onRowClick={(id) => openDetail(id)}
      onEdit={(id) => openDetail(id)}
      onCancel={handleCancel}
    />

    {/* Pagination : sous le tableau */}
    <div className="sessions-pager">
      <button className="btn btn--ghost" onClick={prevPage} disabled={page === 0}>
        ◀ Précédent
      </button>

      <span className="muted">
        Page {page + 1} / {totalPages} ({totalElements} éléments)
      </span>

      <button className="btn btn--ghost" onClick={nextPage} disabled={page >= totalPages - 1}>
        Suivant ▶
      </button>

      <label className="muted sessions-pager__size">
        Taille{" "}
        <select value={size} onChange={changeSize}>
          <option value={5}>5</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
      </label>
    </div>

    <SessionCreateModal
      open={openCreate}
      onClose={() => setOpenCreate(false)}
      onSaved={onSaved}
    />

    <SessionDetailModal
      open={detailOpen}
      sessionId={detailId}
      onClose={() => setDetailOpen(false)}
      onDelete={handleCancel}
      onUpdated={() => setRefreshKey(k => k + 1)}
    />
  </div>
);
}
