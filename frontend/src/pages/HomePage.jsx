import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/HomePage.css";

export default function HomePage() {
    const events = useMemo(
        () =>
            Array.from({ length: 12 }).map((_, i) => ({
                id: i + 1,
                title: `Event #${i + 1}`,
                date: `2025-01-${String((i % 28) + 1).padStart(2, "0")}`,
                city: ["İzmir", "İstanbul", "Ankara", "Bursa"][i % 4],
                desc: "Short event description goes here.",
                cover: `https://picsum.photos/seed/event-${i + 1}/900/600`,
            })),
        []
    );

    // ✅ movies artık backend’den gelecek
    const [movies, setMovies] = useState([]);
    const [moviesLoading, setMoviesLoading] = useState(true);

    useEffect(() => {
        const run = async () => {
            try {
                const res = await fetch(
                    "http://localhost:8080/rest/api/movie_dive/movies/latest20", {
                    method: "GET",
                    credentials: "include", // ✅ çok kritik
                });
                if (!res.ok) throw new Error("Movies fetch failed");
                const data = await res.json();

                // backend DTO -> UI format
                const mapped = (data ?? []).map((m) => ({
                    id: m.id,
                    tmdbId: m.tmdbId,               // varsa ekle (yoksa sil)
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

                <SectionTitle title="Events" subtitle="Upcoming events near you" />
                <PosterRail items={events} type="event" getImage={(e) => e.cover} />

                <div className="hp-spacer" />

                <SectionTitle title="Trending Movies" subtitle="Top 20 from your database" />

                {moviesLoading ? (
                    <div className="hp-sub">Loading movies...</div>
                ) : (
                    <PosterRail items={movies} type="movie" getImage={(m) => m.poster} />
                )}
            </div>
        </div>
    );
}

function SectionTitle({ title, subtitle }) {
    return (
        <div className="hp-sectionHead">
            <div>
                <div className="hp-sectionTitle">{title}</div>
                <div className="hp-sectionSub">{subtitle}</div>
            </div>
        </div>
    );
}

function PosterRail({ items, type, getImage }) {
    const railRef = useRef(null);
    const navigate = useNavigate();   // ✅

    const scrollByCard = (dir) => {
        const el = railRef.current;
        if (!el) return;
        const cardWidth = 320;
        const gap = 14;
        el.scrollBy({ left: dir * (cardWidth + gap), behavior: "smooth" });
    };

    return (
        <div className="hp-railWrap">
            <button className="hp-arrow hp-arrowLeft" onClick={() => scrollByCard(-1)}>
                ‹
            </button>

            <div ref={railRef} className="hp-rail">
                {items.map((item) => (
                    <div
                        key={item.tmdbId ?? item.id}
                        className={`hp-card hp-card--poster ${type === "movie" ? "hp-card--movie" : ""}`}
                        style={{ backgroundImage: `url("${item.poster_path}")` }}
                    >
                        <div className="hp-cardInner">
                            <div className="hp-cardTop">
                                <div className="hp-badge">{type === "movie" ? item.release_date?.slice(0,4) : item.city}</div>

                                <div className="hp-muted">
                                    {type === "movie"
                                        ? ""
                                        : item.date}
                                </div>
                            </div>

                            <div className="hp-cardTitle">{item.title}</div>

                            <div className="hp-cardDesc">
                                {type === "movie"
                                    ? `⭐ Rating: ${item.vote_average?.toFixed(1)}`
                                    : item.desc}
                            </div>

                            {/* ✅ BUTON */}
                            {type === "movie" && (
                                <button
                                    className="hp-cta"
                                    onClick={() => navigate(`/movie/${item.id}`)}   // ✅ DB id
                                >
                                    Open
                                </button>
                            )}

                            {type !== "movie" && (
                                <button className="hp-cta">View details</button>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            <button className="hp-arrow hp-arrowRight" onClick={() => scrollByCard(1)}>
                ›
            </button>
        </div>
    );
}

