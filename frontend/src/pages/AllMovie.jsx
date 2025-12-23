import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";
import "../styles/AllMovie.css";

export default function AllMovies() {
    const navigate = useNavigate();

    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    const API_URL = "rest/api/movie_dive/allmovies";

    useEffect(() => {
        const run = async () => {
            setLoading(true);
            setErr("");
            try {
                const res = await api.get(API_URL, { withCredentials: true });
                setMovies(Array.isArray(res.data) ? res.data : []);
            } catch (e) {
                setErr(e?.response?.data?.message || "Movies could not be loaded.");
                setMovies([]);
            } finally {
                setLoading(false);
            }
        };
        run();
    }, []);

    const yearLabel = (m) => {
        const d = m?.releaseDate || m?.release_date || m?.release || null;
        if (!d) return "-";
        const s = String(d);
        return s.includes("T") ? s.split("T")[0].slice(0, 4) : s.slice(0, 4);
    };

    const safeText = (txt, max = 90) => {
        const t = String(txt || "").trim();
        if (!t) return "No overview.";
        return t.length > max ? t.slice(0, max) + "…" : t;
    };

    const posterUrl = (m) =>
        m?.posterPath ||
        m?.poster_path ||
        m?.posterUrl ||
        m?.poster_url ||
        m?.poster ||
        null;

    const ratingLabel = (m) => {
        const r = m?.voteAverage ?? m?.vote_average ?? m?.rating ?? null;
        if (r === null || r === undefined || r === "") return "-";
        const num = Number(r);
        return Number.isFinite(num) ? num.toFixed(1) : String(r);
    };

    const genreLabel = (m) => {
        // backend bazen string, bazen array döner
        const g = m?.genres ?? m?.genreNames ?? m?.genre ?? null;
        if (!g) return "";
        if (Array.isArray(g)) return g.slice(0, 2).join(" • ");
        return String(g);
    };

    return (
        <div className="am-page">
            <div className="am-container">
                <div className="am-header">
                    <div>
                        <h2 className="am-title">All Movies</h2>
                        <p className="am-subtitle">Browse everything in MovieDive.</p>
                    </div>

                    <button className="am-btn am-btn-ghost" onClick={() => navigate(-1)}>
                        Back
                    </button>
                </div>

                {loading && <div className="am-state">Loading…</div>}
                {!loading && err && <div className="am-state am-state-error">{err}</div>}

                {!loading && !err && movies.length === 0 && (
                    <div className="am-state">No movies yet.</div>
                )}

                {!loading && !err && movies.length > 0 && (
                    <div className="am-grid">
                        {movies.map((m) => {
                            const bg = posterUrl(m);

                            return (
                                <article
                                    key={m?.id ?? m?.tmdbId ?? m?.tmdb_id}
                                    className="am-card"
                                    style={
                                        bg
                                            ? {
                                                backgroundImage: `url(${bg})`,
                                            }
                                            : undefined
                                    }
                                >
                                    <div className="am-card-overlay" />

                                    <div className="am-card-top">
                                        <div className="am-pill">
                                            {genreLabel(m) || "Movie"}
                                        </div>
                                        <div className="am-meta">
                                            <span className="am-year">{yearLabel(m)}</span>
                                            <span className="am-dot">•</span>
                                            <span className="am-rating">⭐ {ratingLabel(m)}</span>
                                        </div>
                                    </div>

                                    <div className="am-card-body">
                                        <h3 className="am-card-title">
                                            {m?.title || m?.name || `Movie #${m?.id}`}
                                        </h3>

                                        <p className="am-card-desc">
                                            {safeText(m?.overview || m?.description)}
                                        </p>

                                        <button
                                            className="am-card-btn"
                                            onClick={() => navigate(`/movie/${m?.id ?? m?.tmdbId ?? m?.tmdb_id}`)}
                                        >
                                            View details
                                        </button>
                                    </div>
                                </article>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
}
