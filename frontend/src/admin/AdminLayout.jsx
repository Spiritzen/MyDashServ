import NavBar from "@/components/layout/NavBar";
import Footer from "@/components/layout/Footer";
import AdminSidebar from "@/admin/AdminSidebar";
import AdminHomeRight from "@/admin/AdminHomeRight";
import AdminSessions from "@/admin/sessions/AdminSessions"; // ⇐ AJOUT
import AdminFormations from "@/admin/formations/AdminFormations"; // ⇐ AJOUT
import AdminFormateurs from "@/admin/formateurs/AdminFormateurs";
import AdminAffectationsRight from "@/admin/affectations/AdminAffectationsRight"; // + import

import "@/styles/layout.css";
import "@/styles/home.css";
import "@/styles/admin.css";

export default function AdminLayout({ page }) {
  return (
    <>
      <NavBar />
      <main className="home home--admin">
        <aside className="home__aside">
          <AdminSidebar />
        </aside>

        <section className="home__content">
  {page === "sessions"      ? <AdminSessions />          :
   page === "formations"    ? <AdminFormations />        :
   page === "formateurs"    ? <AdminFormateurs />        :
   page === "affectations"  ? <AdminAffectationsRight /> :
                               <AdminHomeRight />}
        </section>
        
      </main>
      <Footer />
    </>
  );
}
