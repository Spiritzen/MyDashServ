// src/pages/Home.jsx
import NavBar from "../components/layout/NavBar";
import Footer from "../components/layout/Footer";
import HomeLeft from "../components/home/HomeLeft";
import HomeRight from "../components/home/HomeRight";

import "../styles/layout.css";
import "../styles/home.css";

export default function Home() {
  return (
    <>
      <NavBar />
      {/* 👇 nouvelle classe pour inverser Right/Left en responsive */}
      <main className="home home--right-first">
        <HomeLeft />
        <HomeRight />
      </main>
      <Footer />
    </>
  );
}
