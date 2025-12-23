import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/HomePage.css";

export default function HomePage() {
    // ✅ EVENTS artık backend’den gelecek
    const [events, setEvents] = useState([]);
    const [eventsLoading, setEventsLoading] = useState(true);

    // ✅ MOVIES zaten backend’den geliyor
    const [movies, setMovies] = useState([]);
    const [moviesLoading, setMoviesLoading] = useState(true);

    useEffect(() => {
        const run = async () => {
            // EVENTS
            try {
                setEventsLoading(true);

                // ✅ BURAYI kendi endpoint’in ile değiştir
                // örn: /rest/api/movie_dive/events/allevents
                const res = await fetch("http://localhost:8080/rest/api/movie_dive/events/upcoming", {
                    method: "GET",
                    credentials: "include",
                });
                if (!res.ok) throw new Error("Events fetch failed");
                const data = await res.json();

                const mapped = (Array.isArray(data) ? data : []).map((e) => ({
                    id: e.id,
                    title: e.title,
                    city: e.cityName ?? (e.cityId ? `City #${e.cityId}` : ""),
                    date: (e.eventDateTime || "").split("T")[0] || "",
                    desc: e.description || "",
                    poster_path: e.posterPath || e.poster_path || null, // dto'nda hangisi varsa
                }));

                setEvents(mapped);
            } catch (e) {
                console.error(e);
                setEvents([]);
            } finally {
                setEventsLoading(false);
            }

            // MOVIES
            try {
                setMoviesLoading(true);
                const res = await fetch("http://localhost:8080/rest/api/movie_dive/movies/latest20", {
                    method: "GET",
                    credentials: "include",
                });
                if (!res.ok) throw new Error("Movies fetch failed");
                const data = await res.json();

                const mapped = (data ?? []).map((m) => ({
                    id: m.id,
                    tmdbId: m.tmdbId,
                    title: m.title,
                    release_date: m.release_date,
                    vote_average: m.vote_average,
                    poster_path: m.poster_path,
                }));

                setMovies(mapped);
            } catch (e) {
                console.error(e);
                setMovies([]);
            } finally {
                setMoviesLoading(false);
            }
        };

        run();
    }, []);

    return (
        <div className="hp">
            <div className="hp-container">
                <header className="hp-hero">
                    <h1 className="hp-h1">Discover movies & events in one place</h1>
                    <p className="hp-sub">Use the sections below to explore events and trending movies.</p>
                </header>

                <SectionTitle
                    title="Events"
                    subtitle="Upcoming events"
                    to="/allevents"
                />


                {eventsLoading ? (
                    <div className="hp-sub">Loading events...</div>
                ) : (
                    <PosterRail
                        items={events}
                        type="event"
                        getImage={(e) => e.poster_path}   // ✅ event poster
                    />
                )}

                <div className="hp-spacer" />

                <SectionTitle title="Trending Movies" subtitle="Top 20 from your database" />

                {moviesLoading ? (
                    <div className="hp-sub">Loading movies...</div>
                ) : (
                    <PosterRail items={movies} type="movie" getImage={(m) => m.poster_path} />
                )}
            </div>
        </div>
    );
}

function SectionTitle({ title, subtitle, to }) {
    const navigate = useNavigate();

    const clickable = Boolean(to);

    return (
        <div className="hp-sectionHead">
            <div
                className={`hp-sectionTitleWrap ${clickable ? "hp-sectionTitleClickable" : ""}`}
                onClick={clickable ? () => navigate(to) : undefined}
                role={clickable ? "button" : undefined}
                tabIndex={clickable ? 0 : undefined}
                onKeyDown={
                    clickable
                        ? (e) => {
                            if (e.key === "Enter" || e.key === " ") navigate(to);
                        }
                        : undefined
                }
            >
                <div className="hp-sectionTitle">{title}</div>
                {clickable && <span className="hp-sectionArrow">›</span>}
            </div>

            {subtitle && <div className="hp-sectionSub">{subtitle}</div>}
        </div>
    );
}

function PosterRail({ items, type, getImage }) {
    const railRef = useRef(null);
    const navigate = useNavigate();

    const scrollByCard = (dir) => {
        const el = railRef.current;
        if (!el) return;
        const cardWidth = 320;
        const gap = 14;
        el.scrollBy({ left: dir * (cardWidth + gap), behavior: "smooth" });
    };

    const safeBg = (url) => {
        const u = getImage?.(url); // yanlış kullanım olmasın
        return u ? `url("${u}")` : "none";
    };

    return (
        <div className="hp-railWrap">
            <button className="hp-arrow hp-arrowLeft" onClick={() => scrollByCard(-1)}>
                ‹
            </button>

            <div ref={railRef} className="hp-rail">
                {items.map((item) => {
                    const bg = getImage?.(item);
                    return (
                        <div
                            key={item.tmdbId ?? item.id}
                            className={`hp-card hp-card--poster ${type === "movie" ? "hp-card--movie" : ""}`}
                            style={{ backgroundImage: bg ? `url("${bg}")` : undefined }}
                        >
                            <div className="hp-cardInner">
                                <div className="hp-cardTop">
                                    <div className="hp-badge">
                                        {type === "movie" ? item.release_date?.slice(0, 4) : item.city || "City"}
                                    </div>

                                    <div className="hp-muted">
                                        {type === "movie" ? "" : item.date || ""}
                                    </div>
                                </div>

                                <div className="hp-cardTitle">{item.title}</div>

                                <div className="hp-cardDesc">
                                    {type === "movie"
                                        ? `⭐ Rating: ${item.vote_average?.toFixed?.(1) ?? item.vote_average ?? "-"}`
                                        : (item.desc?.length > 70 ? item.desc.slice(0, 70) + "…" : item.desc)}
                                </div>

                                {/* ✅ BUTON */}
                                {type === "movie" ? (
                                    <button className="hp-cta" onClick={() => navigate(`/movie/${item.id}`)}>
                                        Open
                                    </button>
                                ) : (
                                    <button className="hp-cta" onClick={() => navigate(`/eventdetail/${item.id}`)}>
                                        View details
                                    </button>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>

            <button className="hp-arrow hp-arrowRight" onClick={() => scrollByCard(1)}>
                ›
            </button>
        </div>
    );
}
