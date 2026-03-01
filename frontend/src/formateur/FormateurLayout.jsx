// src/formateur/FormateurLayout.jsx
import NavBar from "@/components/layout/NavBar";
import Footer from "@/components/layout/Footer";
import FormateurSidebar from "@/formateur/FormateurSidebar";
import FormateurHomeRight from "@/formateur/FormateurHomeRight";
import ProfilRight from "@/formateur/profil/ProfilRight";
import MesCompetencesRight from "@/formateur/competences/MesCompetencesRight";
import DispoRight from "@/formateur/disponibilite/DispoRight";
import Candidature from "@/formateur/candidature/Candidature";
import FormateurFormations from "@/formateur/formations/FormateurFormations";

import "@/styles/layout.css";
import "@/styles/home.css";
import "@/styles/formateur.css";

function Placeholder({ title }) {
  return (
    <div className="card panel">
      <h2 style={{ marginTop: 0 }}>{title}</h2>
      <p className="muted">Section à brancher (placeholders / UI seulement).</p>
    </div>
  );
}

export default function FormateurLayout({ page }) {
  return (
    <>
      <NavBar />

      <main className="home home--formateur">
        <aside className="home__aside">
          <FormateurSidebar />
        </aside>

        <section className="home__content">
          {page === "profil" ? (
            <ProfilRight />
          ) : page === "competences" ? (
            <MesCompetencesRight />
          ) : page === "dispos" ? (
            <DispoRight />
          ) : page === "formations" ? (
            <FormateurFormations />
          ) : page === "candidature" ? (
            <Candidature />
          ) : page === "documents" ? (
            <Placeholder title="Mes documents (RIB, SIRET…)" />
          ) : page === "securite" ? (
            <Placeholder title="Sécurité (mot de passe / 2FA)" />
          ) : (
            <FormateurHomeRight />
          )}
        </section>
      </main>

      <Footer />
    </>
  );
}
