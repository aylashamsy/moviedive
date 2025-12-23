import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api } from "../api/client";
import "../styles/EventDetail.css";

export default function EventDetail() {
    const navigate = useNavigate();
    const { id } = useParams(); // route: /eventdetail/:id

    const [event, setEvent] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // ✅ register states
    const [registering, setRegistering] = useState(false);
    const [registerMsg, setRegisterMsg] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(true);
    const [alreadyRegistered, setAlreadyRegistered] = useState(false);

    useEffect(() => {
        const run = async () => {
            if (!id) {
                setError("Event id not found.");
                return;
            }

            setLoading(true);
            setError("");
            setRegisterMsg("");

            try {
                const res = await api.get(`/rest/api/movie_dive/events/getevent/${id}`, {
                    withCredentials: true,
                });

                setEvent(res.data);

                // ✅ Eğer backend DTO'ya later boolean eklediysen:
                // setAlreadyRegistered(Boolean(res.data?.alreadyRegistered));
                // Şimdilik backend bunu göndermiyorsa false kalsın:
                setAlreadyRegistered(Boolean(res.data?.alreadyRegistered) || false);
            } catch (err) {
                setError(err?.response?.data?.message || "Event not found.");
                setEvent(null);
            } finally {
                setLoading(false);
            }
        };

        run();
    }, [id]);

    const handleRegister = async () => {
        if (!id) return;

        setRegisterMsg("");

        // ✅ login kontrolü (session düşmüşse 401 yakalanır)
        try {
            await api.get("/rest/api/movie_dive/users/auth/me", {
                withCredentials: true,
            });
            setIsLoggedIn(true);
        } catch {
            setIsLoggedIn(false);
            setRegisterMsg("Please login first.");
            return;
        }

        setRegistering(true);

        try {
            await api.post(
                `/rest/api/movie_dive/events/register/${id}`, // 👈 EVENT ID
                {},
                { withCredentials: true }
            );

            setRegisterMsg("Successfully registered to event ✅");
            setAlreadyRegistered(true);

            // ✅ count güncellensin diye event’i tekrar çek
            try {
                const refreshed = await api.get(
                    `/rest/api/movie_dive/events/getevent/${id}`,
                    { withCredentials: true }
                );
                setEvent(refreshed.data);
            } catch {
                // ignore
            }
        } catch (err) {
            const status = err?.response?.status;

            if (status === 401) {
                setIsLoggedIn(false);
                setRegisterMsg("Please login first.");
            } else if (status === 409 || status === 400) {
                // backend çoğunlukla "already registered" için 409 döndürür
                setAlreadyRegistered(true);
                setRegisterMsg("You already registered.");
            } else {
                setRegisterMsg(err?.response?.data?.message || "Registration failed.");
            }
        } finally {
            setRegistering(false);
        }
    };

    const dt = useMemo(() => {
        const s = event?.eventDateTime;
        if (!s) return { date: "-", time: "-" };
        const [d, t] = String(s).split("T");
        return { date: d || "-", time: t ? t.slice(0, 5) : "-" }; // HH:mm
    }, [event?.eventDateTime]);

    const { eventDate, eventTime } = useMemo(() => {
        if (!event?.eventDateTime) return { eventDate: "-", eventTime: "-" };
        const [datePart, timePart] = String(event.eventDateTime).split("T");
        return {
            eventDate: datePart || "-",
            eventTime: timePart ? timePart.slice(0, 5) : "-", // HH:mm
        };
    }, [event?.eventDateTime]);

    if (loading) {
        return (
            <div className="up-page">
                <div className="up-card">
                    <div className="up-msg up-msg-ok">Loading event...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="up-page">
                <div className="up-card">
                    <div className="up-header">
                        <h2 className="up-title">Event Detail</h2>
                        <button className="btn btn-ghost" onClick={() => navigate(-1)}>
                            Back
                        </button>
                    </div>
                    <div className="up-msg up-msg-error">{error}</div>
                </div>
            </div>
        );
    }

    if (!event) return null;

    return (
        <div className="up-page">
            <div className="up-card">
                <div
                    className="up-header"
                    style={{ display: "flex", justifyContent: "space-between", gap: 12 }}
                >
                    <div>
                        <h2 className="up-title">Event Detail</h2>
                    </div>

                    <div style={{ display: "flex", gap: 10 }}>
                        <button
                            className="btn btn-ghost"
                            type="button"
                            onClick={() => navigate(-1)}
                        >
                            Back
                        </button>
                        <button
                            className="btn btn-primary"
                            type="button"
                            onClick={() => navigate("/allevents")}
                        >
                            Events
                        </button>
                    </div>
                </div>

                <div
                    style={{
                        display: "grid",
                        gridTemplateColumns: "320px 1fr",
                        gap: 22,
                        alignItems: "start",
                    }}
                >
                    {/* LEFT: Poster */}
                    <div
                        style={{
                            borderRadius: 16,
                            overflow: "hidden",
                            border: "1px solid rgba(255,255,255,0.08)",
                            background: "rgba(255,255,255,0.04)",
                            minHeight: 420,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            padding: 16,
                        }}
                    >
                        {event?.posterPath || event?.poster_path ? (
                            <img
                                src={event.posterPath ?? event.poster_path}
                                alt={event.movieTitle || "Movie poster"}
                                style={{
                                    width: "100%",
                                    height: "auto",
                                    display: "block",
                                    borderRadius: 12,
                                }}
                                onError={(e) => {
                                    e.currentTarget.onerror = null;
                                    e.currentTarget.style.display = "none";
                                }}
                            />
                        ) : (
                            <div style={{ textAlign: "center", opacity: 0.8 }}>
                                <div style={{ fontSize: 14, marginBottom: 6 }}>
                                    Poster not available
                                </div>
                                <div style={{ fontSize: 12, opacity: 0.7 }}>
                                    Movie: {event.movieTitle ?? "-"} <br />
                                    MovieId: {event.movieId ?? "-"}
                                </div>
                            </div>
                        )}
                    </div>

                    {/* RIGHT: Details */}
                    <div
                        style={{
                            borderRadius: 16,
                            border: "1px solid rgba(255,255,255,0.08)",
                            background: "rgba(255,255,255,0.03)",
                            padding: 18,
                        }}
                    >
                        <div
                            style={{
                                display: "flex",
                                justifyContent: "space-between",
                                gap: 12,
                                flexWrap: "wrap",
                            }}
                        >
                            <div>
                                <div style={{ fontSize: 22, fontWeight: 800 }}>{event.title}</div>
                                <div style={{ opacity: 0.8, marginTop: 4 }}>
                                    {event.cityName ?? "-"}
                                </div>
                            </div>

                            <div style={{ textAlign: "right", opacity: 0.9 }}>
                                <div style={{ fontWeight: 700 }}>{dt.date}</div>
                                <div style={{ fontSize: 13, opacity: 0.75 }}>{dt.time}</div>
                            </div>
                        </div>

                        <hr
                            style={{
                                margin: "16px 0",
                                borderColor: "rgba(255,255,255,0.08)",
                            }}
                        />

                        <DetailRow label="Description" value={event.description} multiline />
                        <DetailRow label="Location" value={event.locationName} />
                        <DetailRow label="Movie" value={event.movieTitle ?? "-"} />
                        <DetailRow
                            label="Created By"
                            value={`${event.createdByUsername ?? "-"} `}
                        />
                        <DetailRow
                            label="Registered Count"
                            value={String(event.registeredCount ?? 0)}
                        />
                        <DetailRow label="Event Date" value={eventDate} />
                        <DetailRow label="Event Time" value={eventTime} />

                        {/* ✅ Register message */}
                        {registerMsg && (
                            <div
                                style={{
                                    marginTop: 10,
                                    fontSize: 14,
                                    opacity: 0.95,
                                    color: registerMsg.toLowerCase().includes("login")
                                        ? "#ffb4b4"
                                        : registerMsg.toLowerCase().includes("already")
                                            ? "#ffd27d"
                                            : "#7CFFB2",
                                }}
                            >
                                {registerMsg}
                            </div>
                        )}

                        <div
                            style={{
                                marginTop: 18,
                                display: "flex",
                                gap: 10,
                                flexWrap: "wrap",
                            }}
                        >
                            <button
                                className="btn btn-primary"
                                type="button"
                                onClick={handleRegister}
                                disabled={registering || alreadyRegistered}
                                title={
                                    alreadyRegistered
                                        ? "You already registered"
                                        : isLoggedIn
                                            ? "Register to this event"
                                            : "Please login first"
                                }
                            >
                                {alreadyRegistered
                                    ? "You already registered"
                                    : registering
                                        ? "Registering..."
                                        : "Register"}
                            </button>

                            <button
                                className="btn btn-ghost"
                                type="button"
                                onClick={() => navigate(-1)}
                            >
                                Close
                            </button>

                            {!isLoggedIn && (
                                <button
                                    className="btn btn-ghost"
                                    type="button"
                                    onClick={() => navigate("/login")}
                                >
                                    Go Login
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

function DetailRow({ label, value, multiline }) {
    return (
        <div
            style={{
                display: "grid",
                gridTemplateColumns: "160px 1fr",
                gap: 12,
                padding: "8px 0",
            }}
        >
            <div style={{ opacity: 0.75, fontSize: 13 }}>{label}</div>
            <div
                style={{
                    whiteSpace: multiline ? "pre-wrap" : "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                }}
            >
                {value ?? "-"}
            </div>
        </div>
    );
}
