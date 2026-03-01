import { useMemo, useState, useEffect } from "react";

export default function HomeLeft() {
  return (
    <aside className="home__aside">
      <div className="aside__brand">
        <img src="/img/logo.png" alt="Logo" className="brand__logo" />
        <h1 className="brand__title">MyDashServ</h1>
        <p className="brand__tag">Formations & Formateurs</p>
      </div>

      <div className="aside__block">
        <h2 className="aside__title">Pourquoi cette plateforme ?</h2>
        <p className="aside__text">
          Planifiez vos sessions, affectez le bon formateur selon
          <strong> compétences</strong>, <strong>disponibilités</strong> et
          <strong> localisation</strong>, avec détection des conflits.
        </p>
      </div>

      <div className="aside__block">
        <h2 className="aside__title">Agenda</h2>
        <SmallCalendar />
        <div className="aside__time">
          <span className="time__label">Heure locale</span>
          <Clock />
        </div>
      </div>
    </aside>
  );
}

/* --- Widgets --- */
function SmallCalendar() {
  const today = useMemo(() => new Date(), []);
  const [cursor, setCursor] = useState(new Date(today.getFullYear(), today.getMonth(), 1));

  const year = cursor.getFullYear();
  const month = cursor.getMonth();
  const firstDay = new Date(year, month, 1).getDay(); // 0=Dim
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  const weekDays = ["Lu", "Ma", "Me", "Je", "Ve", "Sa", "Di"];
  const blanks = (firstDay + 6) % 7;
  const cells = [...Array(blanks).fill(null), ...Array.from({ length: daysInMonth }, (_, i) => i + 1)];

  const isToday = (d) =>
    d && d === today.getDate() && month === today.getMonth() && year === today.getFullYear();

  return (
    <div className="cal">
      <div className="cal__head">
        <button className="cal__nav" onClick={() => setCursor(new Date(year, month - 1, 1))}>‹</button>
        <div className="cal__title">
          {cursor.toLocaleString("fr-FR", { month: "long", year: "numeric" })}
        </div>
        <button className="cal__nav" onClick={() => setCursor(new Date(year, month + 1, 1))}>›</button>
      </div>

      <div className="cal__grid cal__grid--header">
        {weekDays.map((d) => <div key={d} className="cal__cell cal__cell--dow">{d}</div>)}
      </div>

      <div className="cal__grid">
        {cells.map((d, i) => (
          <div key={i} className={`cal__cell ${isToday(d) ? "is-today" : ""}`}>{d ?? ""}</div>
        ))}
      </div>
    </div>
  );
}

function Clock() {
  const [, setTick] = useState(0);
  useEffect(() => {
    const id = setInterval(() => setTick((t) => t + 1), 1000);
    return () => clearInterval(id);
  }, []);
  return (
    <div className="time__value">
      {new Date().toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit", second: "2-digit" })}
    </div>
  );
}
