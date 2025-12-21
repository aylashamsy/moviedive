import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { Clock, CheckCircle2, Heart } from "lucide-react";
import "../styles/MovieDetail.css";

export default function MovieDetail() {
    useEffect(() => {
        window.scrollTo({ top: 0, left: 0, behavior: "smooth" });
    }, []);

    const { id } = useParams(); // DB id
    const isLoggedIn = true; // TODO: useAuth().isLoggedIn

    const [movie, setMovie] = useState(null);

    // ⭐ Kullanıcının verdiği rating (1..10) — yorumdan bağımsız
    const [userRating, setUserRating] = useState(0);

    // 💬 yorumlar (şimdilik dummy, yarın backend’den çekersin)
    const [comments, setComments] = useState([
        { id: 1, username: "selin", rating: 8, date: "2025-12-20", text: "Nice movie, great pacing." },
        { id: 2, username: "taha", rating: 7, date: "2025-12-18", text: "Fun watch, solid ending." },
    ]);

    // ✅ chip status (watchLater/watched/favorite)
    const [status, setStatus] = useState({
        watchLater: false,
        watched: false,
        favorite: false,
    });

    useEffect(() => {
        fetch(`http://localhost:8080/rest/api/movie_dive/getbyid/${id}`)
            .then((r) => r.json())
            .then((data) => {
                setMovie(data);

                // yarın backend’den status + userRating gelirse burada set edeceksin:
                // setStatus({ watchLater: data.watchLater, watched: data.watched, favorite: data.favorite })
                // setUserRating(data.myRating ?? 0)
            })
            .catch(console.error);
    }, [id]);

    const year = useMemo(
        () => (movie?.release_date ? movie.release_date.slice(0, 4) : "—"),
        [movie]
    );

    // Film’in TMDB ortalaması (display için)
    const avgRating = useMemo(
        () => (movie?.vote_average != null ? Number(movie.vote_average).toFixed(1) : "—"),
        [movie]
    );

    const genresText = useMemo(() => (movie?.genre_names ?? []).join(" • "), [movie]);
    const directorText = useMemo(() => (movie?.director_names ?? []).join(", "), [movie]);

    const onToggle = (key) => {
        setStatus((prev) => ({ ...prev, [key]: !prev[key] }));

        // ✅ YARIN BACKEND:
        // POST/PUT user_movie_status (key)
    };

    // ⭐ Rating değiştiğinde (yorumdan bağımsız)
    const handleRatingChange = (value) => {
        if (!isLoggedIn) return;

        setUserRating(value);

        // 🔜 YARIN BACKEND
        // POST /movies/{id}/rating
        // body: { rating: value }
    };


    // 💬 Comment submit (rating zorunlu değil)
    const onSubmitComment = ({ text }) => {
        setComments((prev) => [
            {
                id: Date.now(),
                username: "you",
                rating: userRating || 0, // istersen commentte rating göstermeyebilirsin
                date: new Date().toISOString().slice(0, 10),
                text,
            },
            ...prev,
        ]);

        // ✅ YARIN BACKEND:
        // POST /movies/{id}/comments body: { text }
    };

    if (!movie) return <div className="md md-loading">Loading...</div>;

    return (
        <div className="md">
            <div
                className="md-bg"
                style={{ backgroundImage: `url("${movie.poster_url }")` }}
            />

            <div className="md-shell">
                <div className="md-grid">
                    {/* LEFT */}
                    <div className="md-left">
                        <div className="md-posterWrap">
                            <div
                                className="md-poster"
                                style={{ backgroundImage: `url("${movie.poster_url }")` }}
                            />
                        </div>
                    </div>

                    {/* RIGHT */}
                    <div className="md-right">
                        <div className="md-kicker">
                            <span className="md-chip">{year}</span>
                            <span className="md-chip md-chip--rating">⭐ {avgRating}</span>
                        </div>

                        <h1 className="md-title">{movie.title}</h1>

                        <div className="md-rows">
                            <div className="md-row">
                                <div className="md-label">Genres</div>
                                <div className="md-value">{genresText || "—"}</div>
                            </div>

                            <div className="md-row">
                                <div className="md-label">Director</div>
                                <div className="md-value">{directorText || "—"}</div>
                            </div>
                        </div>

                        <p className="md-overview">{movie.overview}</p>

                        {/* ✅ CHIPLER POSTERİN SAĞINDA */}
                        <div className="md-actions md-actions--chips">
                            <ToggleChip
                                active={status.watchLater}
                                onClick={() => onToggle("watchLater")}
                                icon={<Clock size={18} />}
                                label="Watch Later"
                            />
                            <ToggleChip
                                active={status.watched}
                                onClick={() => onToggle("watched")}
                                icon={<CheckCircle2 size={18} />}
                                label="Watched"
                            />
                            <ToggleChip
                                active={status.favorite}
                                onClick={() => onToggle("favorite")}
                                icon={<Heart size={18} />}
                                label="Favorite"
                            />
                        </div>
                    </div>
                </div>

                {/* ⭐ RATING – BAĞIMSIZ */}
                <div className="md-ratingBox">
                    <div className="md-ratingLabel">Your Rating</div>
                    <StarRating
                        value={userRating}
                        onChange={handleRatingChange}
                        disabled={!isLoggedIn}
                    />
                    {!isLoggedIn && (
                        <div className="md-hint">
                            Login to rate this movie.
                        </div>
                    )}

                </div>

                {/* 💬 COMMENTS – BAĞIMSIZ */}
                <CommentsSection
                    isLoggedIn={isLoggedIn}
                    comments={comments}
                    onSubmit={onSubmitComment}
                />
            </div>
        </div>
    );
}

function ToggleChip({ active, onClick, icon, label }) {
    return (
        <button
            type="button"
            className={`md-tchip ${active ? "md-tchip--active" : ""}`}
            onClick={onClick}
            aria-pressed={active}
        >
            <span className="md-tchipIcon">{icon}</span>
            <span className="md-tchipText">{label}</span>
        </button>
    );
}

function StarRating({ value, onChange, disabled }) {
    return (
        <div className={`md-stars ${disabled ? "md-stars--disabled" : ""}`}>
            {Array.from({ length: 10 }).map((_, i) => {
                const starValue = i + 1;
                const filled = starValue <= value;

                return (
                    <button
                        key={starValue}
                        type="button"
                        className={`md-star ${filled ? "md-star--filled" : ""}`}
                        onClick={() => {
                            if (disabled) return;
                            onChange(starValue);
                        }}
                        disabled={disabled}
                        title={
                            disabled
                                ? "Login to rate this movie"
                                : `Rate ${starValue}/10`
                        }
                        aria-disabled={disabled}
                    >
                        ★
                    </button>
                );
            })}

            <div className="md-starValue">
                {value ? `${value}/10` : "—/10"}
            </div>
        </div>
    );
}


function CommentsSection({ isLoggedIn, comments, onSubmit }) {
    const [text, setText] = useState("");

    const handleSubmit = () => {
        if (!isLoggedIn) return;
        if (!text.trim()) return;

        onSubmit?.({ text: text.trim() });
        setText("");
    };

    return (
        <section className="md-comments">
            <div className="md-commentsHead">
                <div>
                    <div className="md-commentsTitle">User Comments</div>
                    <div className="md-commentsSub">
                        {comments.length} comment{comments.length === 1 ? "" : "s"}
                    </div>
                </div>
            </div>

            <div className="md-commentList">
                {comments.map((c) => (
                    <div key={c.id} className="md-comment">
                        <div className="md-commentTop">
                            <div className="md-commentUser">{c.username}</div>
                            <div className="md-commentMeta">
                                {/* rating’i commentten tamamen kaldırmak istersen bu satırı sil */}
                                {c.rating ? <span className="md-commentRating">★ {c.rating}/10</span> : null}
                                <span className="md-commentDate">{c.date}</span>
                            </div>
                        </div>
                        <div className="md-commentText">{c.text}</div>
                    </div>
                ))}
            </div>

            <div className="md-reviewCard">
                {!isLoggedIn && <div className="md-hint">Login to write a comment.</div>}

                <textarea
                    className="md-textarea"
                    placeholder={isLoggedIn ? "Write your comment..." : "Login to write a comment..."}
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    disabled={!isLoggedIn}
                    rows={4}
                />

                <div className="md-reviewActions">
                    <button
                        className="md-btn md-btn--primary"
                        onClick={handleSubmit}
                        disabled={!isLoggedIn || !text.trim()}
                    >
                        Submit Comment
                    </button>
                </div>
            </div>
        </section>
    );
}
