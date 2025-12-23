import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";
import "../styles/AllEvents.css";

export default function AllEvents() {
    const navigate = useNavigate();

    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    const API_URL = "rest/api/movie_dive/events/upcoming";

    useEffect(() => {
        const run = async () => {
            setLoading(true);
            setErr("");
            try {
                const res = await api.get(API_URL, { withCredentials: true });
                setEvents(Array.isArray(res.data) ? res.data : []);
            } catch (e) {
                setErr(e?.response?.data?.message || "Events could not be loaded.");
                setEvents([]);
            } finally {
                setLoading(false);
            }
        };
        run();
    }, []);

    const formatDate = (s) => {
        if (!s) return "-";
        return String(s).split("T")[0];
    };

    const cityLabel = (ev) =>
        ev?.cityName || (ev?.cityId ? `City #${ev.cityId}` : "City");

    const safeDesc = (ev) => {
        const d = (ev?.description || "").trim();
        if (!d) return "No description.";
        return d.length > 80 ? d.slice(0, 80) + "…" : d;
    };

    return (
        <div className="ae-page">
            <div className="ae-container">
                <div className="ae-header">
                    <div>
                        <h2 className="ae-title">All Events</h2>
                        <p className="ae-subtitle">Explore what’s happening.</p>
                    </div>

                    <button
                        className="ae-btn ae-btn-ghost"
                        onClick={() => navigate(-1)}
                    >
                        Back
                    </button>
                </div>

                {loading && <div className="ae-state">Loading…</div>}
                {!loading && err && (
                    <div className="ae-state ae-state-error">{err}</div>
                )}

                {!loading && !err && events.length === 0 && (
                    <div className="ae-state">No events yet.</div>
                )}

                {!loading && !err && events.length > 0 && (
                    <div className="ae-grid">
                        {events.map((ev) => {
                            const posterUrl = ev.poster_path || null;

                            return (
                                <article
                                    key={ev.id}
                                    className="ae-card"
                                    style={
                                        posterUrl
                                            ? {
                                                backgroundImage: `url(${posterUrl})`,
                                            }
                                            : undefined
                                    }
                                >
                                    {/* poster overlay */}
                                    <div className="ae-card-overlay" />

                                    {/* top info */}
                                    <div className="ae-card-top">
                                        <div className="ae-pill">{cityLabel(ev)}</div>
                                        <div className="ae-date">
                                            {formatDate(ev.eventDateTime)}
                                        </div>
                                    </div>

                                    {/* bottom */}
                                    <div className="ae-card-body">
                                        <h3 className="ae-card-title">
                                            {ev?.title || `Event #${ev.id}`}
                                        </h3>

                                        <p className="ae-card-desc">
                                            {safeDesc(ev)}
                                        </p>

                                        <button
                                            className="ae-card-btn"
                                            onClick={() =>
                                                navigate(`/eventdetail/${ev.id}`)
                                            }
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
