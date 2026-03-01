import { useEffect, useMemo, useState } from "react";
import axiosClient from "@/api/axiosClient";
import FormationFilters from "./FormationFilters";
import FormationTable from "./FormationTable";

import NewFormationModal from "@/components/modal/NewFormationModal";
import EditFormationModal from "@/components/modal/EditFormationModal";

import "@/styles/adminFormations.css";

/** 🧭 Page container : gère requêtes, filtres, tri, pagination */
export default function AdminFormations() {
  const [loading, setLoading] = useState(true);
  const [pageData, setPageData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });

  // État des filtres & liste
  const [q, setQ] = useState({
    search: "",
    actif: "",                // '' | 'true' | 'false'
    format: "",               // '' | 'PRESENTIEL' | 'DISTANCIEL'
    theme: "",
    lieu: "",
    page: 0,
    size: 10,
    sort: "intitule,asc",     // champ,dir
  });

  // tick pour relancer le fetch après création/édition
  const [reloadTick, setReloadTick] = useState(0);

  // Modales
  const [openNew, setOpenNew] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);
  const [editRow, setEditRow] = useState(null);

  // fetch (server-side : q, page, size, sort)
  useEffect(() => {
    let ignore = false;
    (async () => {
      setLoading(true);
      try {
        const resp = await axiosClient.get("/api/admin/formations", {
          params: {
            q: q.search || undefined,
            page: q.page,
            size: q.size,
            sort: q.sort,
            // Ces filtres sont passés pour future compat serveur ; ignorés si non gérés
            actif: q.actif || undefined,
            format: q.format || undefined,
            theme: q.theme || undefined,
            lieu: q.lieu || undefined,
          },
        });
        if (!ignore) {
          setPageData({
            content: resp.data.content ?? [],
            totalElements: resp.data.totalElements ?? 0,
            totalPages: resp.data.totalPages ?? 0,
            number: resp.data.number ?? 0,
            size: resp.data.size ?? q.size,
          });
        }
      } catch (e) {
        console.error(e);
        if (!ignore) {
          setPageData((p) => ({ ...p, content: [], totalElements: 0, totalPages: 0 }));
        }
      } finally {
        !ignore && setLoading(false);
      }
    })();
    return () => { ignore = true; };
  }, [q.search, q.page, q.size, q.sort, q.actif, q.format, q.theme, q.lieu, reloadTick]);

  // Filtrage client léger si le back n’implémente pas encore actif/format/theme/lieu
  const filteredRows = useMemo(() => {
    let rows = pageData.content || [];
    if (q.actif) rows = rows.filter(r => String(r.actif) === q.actif);
    if (q.format) rows = rows.filter(r => r.format === q.format);
    if (q.theme)  rows = rows.filter(r => (r.theme || "").toLowerCase().includes(q.theme.toLowerCase()));
    if (q.lieu)   rows = rows.filter(r => (r.lieu || "").toLowerCase().includes(q.lieu.toLowerCase()));
    return rows;
  }, [pageData.content, q.actif, q.format, q.theme, q.lieu]);

  function onChangeFilters(next) {
    // reset page à 0 à chaque changement de filtre/tri
    setQ((prev) => ({ ...prev, ...next, page: 0 }));
  }

  function onSort(field) {
    const [currentField, currentDir] = q.sort.split(",");
    const dir = currentField === field && currentDir === "asc" ? "desc" : "asc";
    setQ((prev) => ({ ...prev, sort: `${field},${dir}`, page: 0 }));
  }

  async function toggleActif(row) {
    const next = !row.actif;
    // Optimistic UI
    const prevContent = pageData.content;
    setPageData((p) => ({
      ...p,
      content: p.content.map((it) => (it.id === row.id ? { ...it, actif: next } : it)),
    }));
    try {
      await axiosClient.put(`/api/admin/formations/${row.id}`, { actif: next });
    } catch (e) {
      console.error(e);
      // rollback si erreur
      setPageData((p) => ({ ...p, content: prevContent }));
    }
  }

  function goPage(deltaOrIndex) {
    if (typeof deltaOrIndex === "number") {
      const target = Number.isInteger(deltaOrIndex) && deltaOrIndex >= 0
        ? deltaOrIndex
        : Math.max(0, Math.min(pageData.number + deltaOrIndex, pageData.totalPages - 1));
      setQ((prev) => ({ ...prev, page: target }));
    }
  }

  // Handlers modales
  const onOpenNew = () => setOpenNew(true);
  const onCreated = () => {
    setOpenNew(false);
    setReloadTick(t => t + 1); // refetch
  };

  const onEdit = (row) => {
    setEditRow(row);
    setOpenEdit(true);
  };
  const onUpdated = () => {
    setOpenEdit(false);
    setEditRow(null);
    setReloadTick(t => t + 1); // refetch
  };

  return (
    <div className="admin-dash">
      <header className="admin-head">
        <div className="admin-head__left">
          <h1>Formations</h1>
          <div className="muted">Gestion des fiches formation</div>
        </div>
        <div className="admin-head__right">
          <button className="btn btn--primary" onClick={onOpenNew}>
            Nouvelle formation
          </button>
        </div>
      </header>

      {/* Filtres (inchangés) */}
      <FormationFilters value={q} onChange={onChangeFilters} />

      <div className="panel">
        {loading ? (
          <div className="muted">Chargement…</div>
        ) : (
          <FormationTable
            rows={filteredRows}
            sort={q.sort}
            onSort={onSort}
            onEdit={onEdit}
            onToggleActif={toggleActif}
          />
        )}

        {/* Pagination */}
        {!loading && (
          <div className="pager">
            <button className="btn btn--ghost" disabled={q.page === 0} onClick={() => goPage(-1)}>« Préc.</button>
            <span className="muted">
              Page {pageData.number + 1} / {Math.max(1, pageData.totalPages || 1)}
            </span>
            <button
              className="btn btn--ghost"
              disabled={pageData.totalPages === 0 || q.page >= pageData.totalPages - 1}
              onClick={() => goPage(+1)}
            >
              Suiv. »
            </button>
            <select
              className="input pager__size"
              value={q.size}
              onChange={(e) => setQ((prev) => ({ ...prev, size: Number(e.target.value), page: 0 }))}
              style={{ marginLeft: 8 }}
            >
              {[5, 10, 20, 50].map((s) => <option key={s} value={s}>{s} / page</option>)}
            </select>
          </div>
        )}
      </div>

      {/* Modales */}
      <NewFormationModal
        open={openNew}
        onClose={() => setOpenNew(false)}
        onCreated={onCreated}
      />

      <EditFormationModal
        open={openEdit}
        row={editRow}
        onClose={() => { setOpenEdit(false); setEditRow(null); }}
        onUpdated={onUpdated}
      />
    </div>
  );
}
