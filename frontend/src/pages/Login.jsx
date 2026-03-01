import NavBar from "@/components/layout/NavBar";
import Footer from "@/components/layout/Footer";
import HomeLeft from "@/components/home/HomeLeft";
import LoginRight from "@/components/login/LoginRight";

import "@/styles/layout.css";
import "@/styles/home.css";

export default function Login() {
  return (
    <>
      <NavBar />
      <main className="home home--auth">
        {/* Colonne gauche = HomeLeft comme sur la Home */}
        <aside >
          <HomeLeft />
        </aside>

        {/* Colonne droite = carte de login centrée */}
        <section className="home__content">
          <LoginRight />
        </section>
      </main>
      <Footer />
    </>
  );
}