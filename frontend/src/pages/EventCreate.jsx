import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";
import "../styles/EventCreate.css";

export default function EventCreate() {
    const navigate = useNavigate();

    const [cities, setCities] = useState([]);
    const [loadingCities, setLoadingCities] = useState(false);

    // ✅ movie search state
    const [movieQuery, setMovieQuery] = useState("");
    const [movieSuggestions, setMovieSuggestions] = useState([]);
    const [movieLoading, setMovieLoading] = useState(false);
    const [movieError, setMovieError] = useState("");
    const [selectedMovie, setSelectedMovie] = useState(null); // {id,title,...}
    const debounceRef = useRef(null);

    const [form, setForm] = useState({
        title: "",
        description: "",
        locationName: "",
        cityId: "",
        eventDateTime: "", // "2025-01-20T20:00:00"
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [ok, setOk] = useState("");

    const safeArray = (x) => (Array.isArray(x) ? x : []);

    useEffect(() => {
        const run = async () => {
            setLoadingCities(true);
            try {
                const res = await api.get("/rest/api/movie_dive/cities");
                setCities(safeArray(res.data));
            } catch {
                setCities([]);
            } finally {
                setLoadingCities(false);
            }
        };
        run();
    }, []);

    const handleChange = (e) => {
        setForm((p) => ({ ...p, [e.target.name]: e.target.value }));
    };

    // ✅ movie search (endpointi buradan değiştir)
    const searchMovies = async (q) => {
        const query = (q || "").trim();
        if (query.length < 2) {
            setMovieSuggestions([]);
            setMovieError("");
            return;
        }

        setMovieLoading(true);
        setMovieError("");
        try {
            // 🔁 Burayı kendi backend endpoint’ine göre ayarla
            // Örn: /movies/search?query=...
            const res = await api.get(
                "/rest/api/movie_dive/search/search-by-name",
                { params: { q: query } }
            );

            const list = res.data?.movies ?? res.data?.results ?? [];
            setMovieSuggestions(Array.isArray(list) ? list : []);
        } catch (err) {
            setMovieSuggestions([]);
            setMovieError(err?.response?.data?.message || "Movie search failed.");
        } finally {
            setMovieLoading(false);
        }
    };

    const onMovieQueryChange = (e) => {
        const val = e.target.value;
        setMovieQuery(val);

        // kullanıcı tekrar yazıyorsa seçimi sıfırla
        setSelectedMovie(null);

        // debounce
        if (debounceRef.current) clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => searchMovies(val), 250);
    };

    const pickMovie = (m) => {
        setSelectedMovie(m);
        setMovieQuery(m?.title ?? "");
        setMovieSuggestions([]);
        setMovieError("");
    };

    const canSubmit = useMemo(() => {
        return (
            form.title.trim() &&
            form.description.trim() &&
            form.locationName.trim() &&
            String(form.cityId).trim() &&
            form.eventDateTime.trim() &&
            selectedMovie?.id // ✅ movieId artık buradan geliyor
        );
    }, [form, selectedMovie]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setOk("");

        if (!canSubmit) {
            setError("Please fill all fields and select a movie from suggestions.");
            return;
        }

        const payload = {
            title: form.title.trim(),
            description: form.description.trim(),
            locationName: form.locationName.trim(),
            cityId: Number(form.cityId),
            movieId: Number(selectedMovie.id),
            eventDateTime: form.eventDateTime.trim(),
        };

        try {
            setLoading(true);
            const res = await api.post("/rest/api/movie_dive/events/create", payload, {
                withCredentials: true,
            });

            console.log("CREATE EVENT:", res.status, res.data);
            setOk(`Event created ✅ id=${res.data?.id ?? "NO_ID"}`);
            setTimeout(() => navigate(`/eventdetail/${res.data.id}`));
        } catch (err) {
            setError(err?.response?.data?.message || "Create event failed.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="up-page">
            <div className="up-card">
                <div className="up-header">
                    <div>
                        <h2 className="up-title">Create Event</h2>
                        <p className="up-subtitle">VIP users can create events.</p>
                    </div>

                    <button className="btn btn-ghost" type="button" onClick={() => navigate(-1)}>
                        Back
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="up-section" style={{ maxWidth: 720 }}>
                    <label className="up-field">
                        <span className="up-field-label">Title</span>
                        <input
                            name="title"
                            value={form.title}
                            onChange={handleChange}
                            placeholder='Movie Night: Interstellar'
                            autoFocus
                        />
                    </label>

                    <label className="up-field">
                        <span className="up-field-label">Description</span>
                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            placeholder="Watching Interstellar together and discussing science behind it."
                            rows={4}
                        />
                    </label>

                    <label className="up-field">
                        <span className="up-field-label">Location Name</span>
                        <input
                            name="locationName"
                            value={form.locationName}
                            onChange={handleChange}
                            placeholder="Alsancak Cinema Hall"
                        />
                    </label>

                    <label className="up-field">
                        <span className="up-field-label">City</span>
                        {loadingCities ? (
                            <div className="up-msg up-msg-ok">Loading cities...</div>
                        ) : cities.length > 0 ? (
                            <select name="cityId" value={form.cityId} onChange={handleChange}>
                                <option value="" disabled>
                                    Select city
                                </option>
                                {cities.map((c) => (
                                    <option key={c.id} value={c.id}>
                                        {c.name}
                                    </option>
                                ))}
                            </select>
                        ) : (
                            <input
                                name="cityId"
                                value={form.cityId}
                                onChange={handleChange}
                                placeholder="CityId (e.g. 35)"
                            />
                        )}
                    </label>

                    {/* ✅ MOVIE NAME + SUGGESTIONS */}
                    <div className="up-field" style={{ position: "relative" }}>
                        <span className="up-field-label">Movie</span>

                        <input
                            value={movieQuery}
                            onChange={onMovieQueryChange}
                            placeholder="Type movie name (e.g. Interstellar)"
                        />

                        {/* selected badge */}
                        {selectedMovie?.id && (
                            <div className="up-field-label" style={{ marginTop: 6 }}>
                                Selected: <b>{selectedMovie.title}</b>
                            </div>
                        )}

                        {/* loading/error */}
                        {movieLoading && <div className="up-field-label" style={{ marginTop: 6 }}>Searching...</div>}
                        {movieError && <div className="up-msg up-msg-error" style={{ marginTop: 8 }}>{movieError}</div>}

                        {/* dropdown suggestions */}
                        {movieSuggestions.length > 0 && (
                            <div
                                style={{
                                    position: "absolute",
                                    top: "100%",
                                    left: 0,
                                    right: 0,
                                    marginTop: 6,
                                    background: "rgba(21, 24, 36, 0.98)",
                                    border: "1px solid rgba(255,255,255,0.12)",
                                    borderRadius: 10,
                                    overflow: "hidden",
                                    zIndex: 50,
                                }}
                            >
                                {movieSuggestions.slice(0, 8).map((m) => (
                                    <button
                                        key={m.id}
                                        type="button"
                                        onClick={() => pickMovie(m)}
                                        style={{
                                            width: "100%",
                                            textAlign: "left",
                                            padding: "10px 12px",
                                            background: "transparent",
                                            border: "none",
                                            color: "rgba(229,231,235,0.95)",
                                            cursor: "pointer",
                                        }}
                                        onMouseDown={(e) => e.preventDefault()} // input blur olmasın
                                    >
                                        <div style={{ fontWeight: 800 }}>{m.title ?? m.name ?? "Movie"}</div>
                                        {(m.year || m.releaseDate) && (
                                            <div style={{ fontSize: 12, color: "rgba(156,163,175,0.95)" }}>
                                                {m.year ?? String(m.releaseDate).slice(0, 10)}
                                            </div>
                                        )}
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>

                    <label className="up-field">
                        <span className="up-field-label">Event Date Time</span>
                        <input
                            name="eventDateTime"
                            value={form.eventDateTime}
                            onChange={handleChange}
                            placeholder="2025-01-20T20:00:00"
                        />
                        <div className="up-field-label" style={{ marginTop: 4 }}>
                            Format: YYYY-MM-DDTHH:mm:ss
                        </div>
                    </label>

                    {error && <div className="up-msg up-msg-error">{error}</div>}
                    {ok && <div className="up-msg up-msg-ok">{ok}</div>}

                    <div className="up-modal-actions" style={{ justifyContent: "flex-start" }}>
                        <button className="btn btn-primary" type="submit" disabled={loading}>
                            {loading ? "Creating..." : "Create"}
                        </button>
                        <button className="btn btn-ghost" type="button" onClick={() => navigate(-1)} disabled={loading}>
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
