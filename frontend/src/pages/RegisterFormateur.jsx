import NavBar from "@/components/layout/NavBar";
import Footer from "@/components/layout/Footer";
import HomeLeft from "@/components/home/HomeLeft";
import RegisterFormateurRight from "@/components/register/RegisterFormateurRight";

import "@/styles/layout.css";
import "@/styles/home.css";
import "@/styles/register.css"; 

export default function RegisterFormateur() {
  return (
    <>
      <NavBar />
      <main className="home home--auth">
        <aside>
          <HomeLeft />
        </aside>

        <section className="home__content">
          <RegisterFormateurRight />
        </section>
      </main>
      <Footer />
    </>
  );
}
