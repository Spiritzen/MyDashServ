export default function HomeRight() {
  return (
    <section className="home__content">
      <Hero />
      <Features />
      <CTA />
    </section>
  );
}

function Hero() {
  return (
    <header className="hero">
      <div className="hero__copy">

        {/* Bouton au-dessus du titre */}
     

        <h2 className="hero__title">Planifiez mieux, assignez plus vite.</h2>
        <p className="hero__subtitle">
          Créez des formations, associez les formateurs adéquats et évitez les conflits d’agenda.
        </p>
           <div className="hero__lead">
          <a className="btn btn--primary" href="/contact">
            Âme de gestionnaire ? Contactez-nous !
          </a>
        </div>
        <div className="hero__actions">
          <a className="btn btn--primary" href="/register">Je suis formateur — Créer un compte</a>
          <a className="btn btn--ghost" href="/login">Se connecter</a>
        </div>
      </div>
      <div className="hero__art">
        <img src="/img/hero-abstract.png" alt="" />
      </div>
    </header>
  );
}


function Features() {
  const items = [
    {
      title: "Pilotez vos sessions",
      badge: "Gestionnaire",
      text: "Créez, planifiez et ajustez vos formations en un clin d’œil. Laissez l’algorithme repérer les meilleurs formateurs et prévenir les conflits d’agenda.",
      bg: "/img/feat-planif.png",
    },
    {
      title: "Valorisez votre expertise",
      badge: "Formateur",
      text: "Mettez en avant vos compétences, indiquez vos disponibilités et suivez vos heures. Gagnez en visibilité et soyez affecté aux missions qui vous ressemblent.",
      bg: "/img/feat-affectation.png",
    },
    {
      title: "Planification intelligente",
      badge: "Pro",
      text: "Un moteur d’affectation qui analyse compétences, localisation et disponibilités pour construire la meilleure combinaison possible.",
      bg: "/img/feat-heures.png",
    },
  ];

  return (
    <section className="features">
      {items.map((f) => (
        <article
          key={f.title}
          className="feature feature--withbg"
          style={{ "--feature-bg": `url(${f.bg})` }}
          aria-label={f.title}
        >
          <div className="feature__body">
            {f.badge && <span className="feature__badge">{f.badge}</span>}
            <h3 className="feature__title">{f.title}</h3>
            <p className="feature__text">{f.text}</p>
          </div>
        </article>
      ))}
    </section>
  );
}

function CTA() {
  return (
    <section className="cta">
      <h3>Prêt à essayer ?</h3>
      <p>Créez votre compte formateur et commencez à planifier.</p>
      <div className="cta__actions">
        <a className="btn btn--primary" href="/register">Créer un compte</a>
        <a className="btn btn--ghost" href="/contact">Nous contacter</a>
      </div>
    </section>
  );
}
