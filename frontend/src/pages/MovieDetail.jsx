import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { Clock, CheckCircle2, Heart, Trash2 } from "lucide-react";
import { api } from "../api/client";
import "../styles/MovieDetail.css";

export default function MovieDetail() {
    useEffect(() => {
        window.scrollTo({ top: 0, left: 0, behavior: "smooth" });
    }, []);

    const { id } = useParams(); // DB movie id

    // TODO: bunu AuthContext'e bağla
    const isLoggedIn = true;

    const [movie, setMovie] = useState(null);

    // ⭐ backend’ten gelecek (user rating)
    const [userRating, setUserRating] = useState(0);
    const [ratingLoading, setRatingLoading] = useState(false);

    // 💬 backend comments
    const [comments, setComments] = useState([]);
    const [commentsLoading, setCommentsLoading] = useState(false);

    // ✅ chip status (watchLater/watched/favorite)
    const [status, setStatus] = useState({
        watchLater: false,
        watched: false,
        favorite: false,
    });

    const [meUserId, setMeUserId] = useState(null);

    // ---------------------------
    // Helpers
    // ---------------------------
    const safeGetMe = async () => {
        // 1) users/auth/me (sen kullanıyorsun)
        try {
            const res = await api.get("/rest/api/movie_dive/users/auth/me");
            const uid = res.data?.id ?? res.data?.userId ?? res.data?.user_id;
            if (uid) return uid;
        } catch {}

    };

    // DtoUserMovieFeedback -> UI comment
    const mapFeedbackToComment = (fb) => {
        return {
            id: fb?.comment_id ?? `${fb?.user?.id}-${fb?.movie?.id}-${Math.random()}`,
            comment_id: fb?.comment_id,
            username: fb?.user?.username ?? fb?.user?.firstname ?? "user",
            date: fb?.commentedAt ? String(fb.commentedAt).slice(0, 10) : "",
            text: fb?.text ?? "",
            rating: fb?.score ? Number(fb.score) : 0, // istersen commentte göstermeyebilirsin
            userId: fb?.user?.id,
        };
    };

    // ---------------------------
    // 1) Movie fetch
    // ---------------------------
    useEffect(() => {
        const run = async () => {
            try {
                const res = await api.get(`/rest/api/movie_dive/getbyid/${id}`);
                setMovie(res.data);
            } catch (e) {
                console.error("movie fetch failed", e);
                setMovie(null);
            }
        };
        run();
    }, [id]);

    // ---------------------------
    // 2) Me fetch (id lazım)
    // ---------------------------
    useEffect(() => {
        const load = async () => {
            const uid = await safeGetMe();
            setMeUserId(uid);
        };
        load();
    }, []);

    // ---------------------------
    // 3) Status fetch
    // ---------------------------
    const fetchStatusFromBackend = async () => {
        if (!meUserId) return;

        try {
            const likedRes = await api.get(`/rest/api/movie_dive/lists/isliked/${meUserId}/${id}`);
            const isFav = !!likedRes.data;

            let rel = null;
            try {
                const relRes = await api.get(`/rest/api/movie_dive/lists/get/relation_type/${meUserId}/${id}`);
                rel = relRes.data; // "WATCHED" / "LATER_WATCH"
            } catch {
                rel = null;
            }

            setStatus({
                favorite: isFav,
                watched: rel === "WATCHED",
                watchLater: rel === "LATER_WATCH",
            });
        } catch (e) {
            console.error("status fetch failed", e);
        }
    };

    useEffect(() => {
        if (!meUserId) return;
        fetchStatusFromBackend();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [meUserId, id]);

    // ---------------------------
    // 4) Rating fetch (getrating/{user_id}/{movie_id})
    // ---------------------------
    const fetchMyRating = async () => {
        if (!meUserId) return;
        setRatingLoading(true);

        try {
            const res = await api.get(`/rest/api/movie_dive/rating/getrating/${meUserId}/${id}`);
            const s = res.data?.score;
            setUserRating(s != null ? Number(s) : 0);
        } catch (e) {
            // NO_RECORD_EXIST -> puan yok demek, UI 0 kalsın
            setUserRating(0);
        } finally {
            setRatingLoading(false);
        }
    };

    useEffect(() => {
        if (!meUserId) return;
        fetchMyRating();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [meUserId, id]);

    // ---------------------------
    // 5) Comments fetch (getallcomments/{movie_id})
    // ---------------------------
    const fetchComments = async () => {
        setCommentsLoading(true);
        try {
            const res = await api.get(`/rest/api/movie_dive/rating/getallcomments/${id}`);
            const list = Array.isArray(res.data) ? res.data : [];
            setComments(list.map(mapFeedbackToComment));
        } catch (e) {
            // NO_RECORD_EXIST -> yorum yok
            setComments([]);
        } finally {
            setCommentsLoading(false);
        }
    };

    useEffect(() => {
        fetchComments();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id]);

    // ---------------------------
    // UI memo
    // ---------------------------
    const year = useMemo(() => (movie?.release_date ? movie.release_date.slice(0, 4) : "—"), [movie]);

    // Film ortalaması zaten movie.vote_average içinde güncelleniyor (service bunu update ediyor)
    const avgRating = useMemo(
        () => (movie?.vote_average != null ? Number(movie.vote_average).toFixed(1) : "—"),
        [movie]
    );

    const genresText = useMemo(() => (movie?.genre_names ?? []).join(" • "), [movie]);
    const directorText = useMemo(() => (movie?.director_names ?? []).join(", "), [movie]);

    const poster =
        movie?.poster_url ||
        movie?.poster_path ||
        movie?.posterPath ||
        movie?.poster ||
        "";

    // ---------------------------
    // CHIP toggle + backend
    // ---------------------------
    const onToggle = async (key) => {
        if (!isLoggedIn) return;

        // optimistic
        setStatus((prev) => ({ ...prev, [key]: !prev[key] }));

        try {
            if (key === "favorite") {
                if (!status.favorite) await api.post(`/rest/api/movie_dive/lists/setliked/${id}`);
                else await api.post(`/rest/api/movie_dive/lists/setdisliked/${id}`);
            }

            if (key === "watchLater") {
                if (!status.watchLater) {
                    await api.post(`/rest/api/movie_dive/lists/setwatchlater/relation_type/${id}`);
                } else {
                    await api.delete(`/rest/api/movie_dive/lists/deletewatchlater/${id}`);
                }
            }

            if (key === "watched") {
                if (!status.watched) {
                    await api.post(`/rest/api/movie_dive/lists/setwatched/relation_type/${id}`);
                } else {
                    await api.delete(`/rest/api/movie_dive/lists/deletewatched/${id}`);
                }
            }

            // ✅ her toggle sonrası backend ile senkronla
            await fetchStatusFromBackend();
        } catch (e) {
            console.error("toggle failed", e);
            // rollback
            setStatus((prev) => ({ ...prev, [key]: !prev[key] }));
        }
    };

    // ---------------------------
    // RATING set/update/delete
    // Controller:
    // POST rating/ratemovie/{movie_id}/{score}
    // PUT  rating/updaterating/{movie_id}/{score}  (ama sen zaten addorupdate yapıyorsun)
    // DELETE rating/deleterating/{movie_id}
    // ---------------------------
    const handleRatingChange = async (value) => {
        if (!isLoggedIn) return;

        setUserRating(value); // optimistic
        setRatingLoading(true);

        try {
            // senin backend zaten addorupdate yapıyor, POST yeterli:
            await api.post(`/rest/api/movie_dive/rating/ratemovie/${id}/${value}`);

            // film ortalaması da backend’de güncelleniyor ama MovieDetail’deki movie state’i eski kalır.
            // Bu yüzden film’i tekrar çekelim ki avgRating güncellensin:
            const res = await api.get(`/rest/api/movie_dive/getbyid/${id}`);
            setMovie(res.data);

            // rating'i de kesinleştirelim
            await fetchMyRating();
        } catch (e) {
            console.error("rateMovie failed", e);
            // hata -> geri al
            setUserRating(0);
        } finally {
            setRatingLoading(false);
        }
    };

    const handleDeleteRating = async () => {
        if (!isLoggedIn) return;

        setRatingLoading(true);
        try {
            await api.delete(`/rest/api/movie_dive/rating/deleterating/${id}`);
            setUserRating(0);

            // ortalama güncellenmiş olabilir -> movie refresh
            const res = await api.get(`/rest/api/movie_dive/getbyid/${id}`);
            setMovie(res.data);
        } catch (e) {
            console.error("deleteRating failed", e);
        } finally {
            setRatingLoading(false);
        }
    };

    // ---------------------------
    // COMMENTS add/delete
    // commentmovie/{movie_id} body: String comment
    // deletecomment/{movie_id}/{comment_id}
    // getallcomments/{movie_id}
    // ---------------------------
    const onSubmitComment = async ({ text }) => {
        if (!isLoggedIn) return;
        if (!text?.trim()) return;

        try {
            // Backend @RequestBody String comment bekliyor.
            // axios default JSON gönderir -> burada plain text göndermek en sorunsuz:
            await api.post(`/rest/api/movie_dive/rating/commentmovie/${id}`, text.trim(), {
                headers: { "Content-Type": "text/plain" },
            });

            await fetchComments();
        } catch (e) {
            console.error("commentMovie failed", e);
        }
    };

    const onDeleteComment = async (comment_id) => {
        if (!isLoggedIn) return;
        if (!comment_id) return;

        try {
            await api.delete(`/rest/api/movie_dive/rating/deletecomment/${id}/${comment_id}`);
            await fetchComments();
        } catch (e) {
            console.error("deleteComment failed", e);
        }
    };

    if (!movie) return <div className="md md-loading">Loading...</div>;

    return (
        <div className="md">
            <div className="md-bg" style={{ backgroundImage: `url("${poster}")` }} />

            <div className="md-shell">
                <div className="md-grid">
                    {/* LEFT */}
                    <div className="md-left">
                        <div className="md-posterWrap">
                            <div className="md-poster" style={{ backgroundImage: `url("${poster}")` }} />
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

                        {/* CHIPLER */}
                        <div className="md-actions md-actions--chips">
                            <ToggleChip
                                active={status.watchLater}
                                onClick={() => onToggle("watchLater")}
                                icon={<Clock size={18} />}
                                label="Watch Later"
                                disabled={!isLoggedIn}
                            />
                            <ToggleChip
                                active={status.watched}
                                onClick={() => onToggle("watched")}
                                icon={<CheckCircle2 size={18} />}
                                label="Watched"
                                disabled={!isLoggedIn}
                            />
                            <ToggleChip
                                active={status.favorite}
                                onClick={() => onToggle("favorite")}
                                icon={<Heart size={18} />}
                                label="Favorite"
                                disabled={!isLoggedIn}
                            />
                        </div>

                        {!isLoggedIn && (
                            <div className="md-hint" style={{ marginTop: 10 }}>
                                Login to use Watch Later / Watched / Favorite.
                            </div>
                        )}
                    </div>
                </div>

                {/* ⭐ RATING */}
                <div className="md-ratingBox">
                    <div className="md-ratingLabel">Your Rating</div>

                    <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                        <StarRating value={userRating} onChange={handleRatingChange} disabled={!isLoggedIn || ratingLoading} />

                        <button
                            type="button"
                            className="md-btn md-btn--ghost"
                            onClick={handleDeleteRating}
                            disabled={!isLoggedIn || ratingLoading || userRating === 0}
                            title="Delete rating"
                            style={{ display: "inline-flex", alignItems: "center", gap: 8 }}
                        >
                            <Trash2 size={16} />
                            Delete
                        </button>

                        {ratingLoading && <div className="md-hint">Saving...</div>}
                    </div>

                    {!isLoggedIn && <div className="md-hint">Login to rate this movie.</div>}
                </div>

                {/* 💬 COMMENTS */}
                <CommentsSection
                    isLoggedIn={isLoggedIn}
                    comments={comments}
                    loading={commentsLoading}
                    onSubmit={onSubmitComment}
                    onDelete={onDeleteComment}
                />
            </div>
        </div>
    );
}

function ToggleChip({ active, onClick, icon, label, disabled }) {
    return (
        <button
            type="button"
            className={`md-tchip ${active ? "md-tchip--active" : ""}`}
            onClick={() => {
                if (disabled) return;
                onClick?.();
            }}
            aria-pressed={active}
            aria-disabled={disabled}
            disabled={disabled}
            title={disabled ? "Login required" : label}
            style={disabled ? { opacity: 0.6, cursor: "not-allowed" } : undefined}
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
                        title={disabled ? "Login to rate this movie" : `Rate ${starValue}/10`}
                        aria-disabled={disabled}
                    >
                        ★
                    </button>
                );
            })}

            <div className="md-starValue">{value ? `${value}/10` : "—/10"}</div>
        </div>
    );
}

function CommentsSection({ isLoggedIn, comments, loading, onSubmit, onDelete }) {
    const [text, setText] = useState("");

    const handleSubmit = async () => {
        if (!isLoggedIn) return;
        if (!text.trim()) return;

        await onSubmit?.({ text: text.trim() });
        setText("");
    };

    return (
        <section className="md-comments">
            <div className="md-commentsHead">
                <div>
                    <div className="md-commentsTitle">User Comments</div>
                    <div className="md-commentsSub">
                        {loading ? "Loading..." : `${comments.length} comment${comments.length === 1 ? "" : "s"}`}
                    </div>
                </div>
            </div>

            <div className="md-commentList">
                {!loading &&
                    comments.map((c) => (
                        <div key={c.id} className="md-comment">
                            <div className="md-commentTop">
                                <div className="md-commentUser">{c.username}</div>
                                <div className="md-commentMeta">
                                    {/* İstersen comment rating’i kaldır: bu span’i sil */}
                                    {c.rating ? <span className="md-commentRating">★ {c.rating}/10</span> : null}
                                    <span className="md-commentDate">{c.date}</span>

                                    {/* delete: backend user kontrolü yapıyor zaten */}
                                    {isLoggedIn && c.comment_id ? (
                                        <button
                                            type="button"
                                            className="md-commentDel"
                                            onClick={() => onDelete?.(c.comment_id)}
                                            title="Delete comment"
                                        >
                                            Delete
                                        </button>
                                    ) : null}

                                </div>
                            </div>
                            <div className="md-commentText">{c.text}</div>
                        </div>
                    ))}

                {!loading && comments.length === 0 && (
                    <div className="md-hint" style={{ padding: "10px 0" }}>
                        No comments yet.
                    </div>
                )}
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
                    <button className="md-btn md-btn--primary" onClick={handleSubmit} disabled={!isLoggedIn || !text.trim()}>
                        Submit Comment
                    </button>
                </div>
            </div>
        </section>
    );
}
