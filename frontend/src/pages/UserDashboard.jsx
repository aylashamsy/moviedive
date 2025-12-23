import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";
import "../styles/UserDashboard.css";

export default function UserDashboard() {
    const navigate = useNavigate();

    const [me, setMe] = useState(null);
    const [userId, setUserId] = useState(null);

    // ✅ user lists
    const [favorites, setFavorites] = useState([]);
    const [watched, setWatched] = useState([]);
    const [later, setLater] = useState([]);

    // ✅ registered events
    const [registeredEvents, setRegisteredEvents] = useState([]);
    const [eventsLoading, setEventsLoading] = useState(false);

    const [loading, setLoading] = useState({
        me: true,
        favorites: false,
        watched: false,
        later: false,
    });

    const [open, setOpen] = useState({
        events: false,
        favorites: false,
        watched: false,
        later: false,
    });

    const toggle = (key) => setOpen((p) => ({ ...p, [key]: !p[key] }));

    // ✅ Backend bazen exception atıyor -> empty kabul ediyoruz
    const safeGetList = async (url) => {
        try {
            const res = await api.get(url, { withCredentials: true });
            return Array.isArray(res.data) ? res.data : [];
        } catch (e) {
            // Login first / 401 / 403 -> boş liste gibi davran
            const msg = String(e?.response?.data?.message || e?.message || "");
            if (
                e?.response?.status === 401 ||
                e?.response?.status === 403 ||
                msg.toLowerCase().includes("login")
            ) {
                return [];
            }
            return [];
        }
    };

    // 1) Me -> userId
    useEffect(() => {
        const run = async () => {
            try {
                const res = await api.get("rest/api/movie_dive/users/auth/me", {
                    withCredentials: true,
                });
                setMe(res.data);

                // ⚠️ id şart:
                const id = res.data?.id;
                setUserId(id ?? null);
            } catch (e) {
                console.error("get/me failed", e);
                setMe(null);
                setUserId(null);
            } finally {
                setLoading((p) => ({ ...p, me: false }));
            }
        };
        run();
    }, []);

    // 2) listeleri çek
    useEffect(() => {
        if (!userId) return;

        const run = async () => {
            setLoading((p) => ({ ...p, favorites: true, watched: true, later: true }));

            const [favDtos, watchedDtos, laterDtos] = await Promise.all([
                safeGetList(`/rest/api/movie_dive/lists/getLikedMovies/${userId}`),
                safeGetList(`/rest/api/movie_dive/lists/getWatchedMovie/${userId}`),
                safeGetList(`/rest/api/movie_dive/lists/getWatchlaterMovie/${userId}`),
            ]);

            setFavorites(favDtos);
            setWatched(watchedDtos);
            setLater(laterDtos);

            setLoading((p) => ({ ...p, favorites: false, watched: false, later: false }));
        };

        run();
    }, [userId]);

    // ✅ 3) Registered events çek (events rail açılınca)
    const EVENTS_API = "/rest/api/movie_dive/events/my-registered"; // <-- endpoint'in buysa

    useEffect(() => {
        if (!open.events) return;

        const run = async () => {
            setEventsLoading(true);
            const list = await safeGetList(EVENTS_API);
            setRegisteredEvents(list);
            setEventsLoading(false);
        };

        run();
    }, [open.events]);

    // Kart click -> movie detail
    const openMovie = (dto) => {
        const movieId = dto?.movie?.id;
        if (!movieId) return;
        navigate(`/movie/${movieId}`);
    };

    // ✅ Event click -> event detail
    const openEvent = (ev) => {
        const eventId = ev?.id;
        if (!eventId) return;
        navigate(`/eventdetail/${eventId}`);
    };

    return (
        <div className="ud">
            <div className="ud-container">
                <header className="ud-hero">
                    <h1 className="ud-h1">Dashboard</h1>
                    <p className="ud-sub">Quick access to your lists and registered events.</p>
                </header>

                {/* ✅ EVENTS = registered events */}
                <DashboardSection
                    title="My Events"
                    subtitle={eventsLoading ? "Loading..." : "Events you registered to"}
                    isOpen={open.events}
                    onToggle={() => toggle("events")}
                    emptyText="You haven't registered to any event yet."
                    empty={!eventsLoading && registeredEvents.length === 0}
                >
                    <PosterRail
                        items={registeredEvents}
                        type="event"
                        getImage={(e) => e?.poster_path || e?.posterPath || ""} // DTO'dan
                        getTitle={(e) => e?.title}
                        getYear={(e) => ""} // event'te year yok
                        getRating={() => null}
                        onOpen={openEvent}
                        // event ekstra alanlar
                        getCity={(e) => e?.cityName}
                        getDate={(e) => (e?.eventDateTime ? String(e.eventDateTime).split("T")[0] : "")}
                        getDesc={(e) => e?.description}
                    />
                </DashboardSection>

                <DashboardSection
                    title="Favorites"
                    subtitle={loading.favorites ? "Loading..." : "Movies you liked"}
                    isOpen={open.favorites}
                    onToggle={() => toggle("favorites")}
                    emptyText="No favorite movies yet."
                    empty={favorites.length === 0 && !loading.favorites}
                >
                    <PosterRail
                        items={favorites}
                        type="movie"
                        getImage={(dto) => dto?.movie?.poster_path}
                        getTitle={(dto) => dto?.movie?.title}
                        getYear={(dto) => dto?.movie?.release_date?.slice(0, 4)}
                        getRating={(dto) => dto?.movie?.vote_average}
                        onOpen={openMovie}
                    />
                </DashboardSection>

                <DashboardSection
                    title="Watched"
                    subtitle={loading.watched ? "Loading..." : "Movies you watched"}
                    isOpen={open.watched}
                    onToggle={() => toggle("watched")}
                    emptyText="No watched movies yet."
                    empty={watched.length === 0 && !loading.watched}
                >
                    <PosterRail
                        items={watched}
                        type="movie"
                        getImage={(dto) => dto?.movie?.poster_path}
                        getTitle={(dto) => dto?.movie?.title}
                        getYear={(dto) => dto?.movie?.release_date?.slice(0, 4)}
                        getRating={(dto) => dto?.movie?.vote_average}
                        onOpen={openMovie}
                    />
                </DashboardSection>

                <DashboardSection
                    title="Watch Later"
                    subtitle={loading.later ? "Loading..." : "Movies saved for later"}
                    isOpen={open.later}
                    onToggle={() => toggle("later")}
                    emptyText="No movies in watch later yet."
                    empty={later.length === 0 && !loading.later}
                >
                    <PosterRail
                        items={later}
                        type="movie"
                        getImage={(dto) => dto?.movie?.poster_path}
                        getTitle={(dto) => dto?.movie?.title}
                        getYear={(dto) => dto?.movie?.release_date?.slice(0, 4)}
                        getRating={(dto) => dto?.movie?.vote_average}
                        onOpen={openMovie}
                    />
                </DashboardSection>
            </div>
        </div>
    );
}

function DashboardSection({ title, subtitle, isOpen, onToggle, children, empty, emptyText }) {
    return (
        <section className="ud-section">
            <button className="ud-box" onClick={onToggle} type="button">
                <div className="ud-box-left">
                    <div className="ud-box-title">{title}</div>
                    <div className="ud-box-sub">{subtitle}</div>
                </div>
                <div className="ud-box-arrow">{isOpen ? "˅" : "›"}</div>
            </button>

            {isOpen && <div className="ud-railArea">{empty ? <div className="ud-empty">{emptyText}</div> : children}</div>}
        </section>
    );
}

/* 4 kart kaydırmalı rail */
function PosterRail({
                        items,
                        type,
                        getImage,
                        getTitle,
                        getYear,
                        getRating,
                        onOpen,
                        // event extras
                        getCity,
                        getDate,
                        getDesc,
                    }) {
    const railRef = useRef(null);

    const scrollBy4 = (dir) => {
        const el = railRef.current;
        if (!el) return;

        const firstCard = el.querySelector(".ud-card");
        const cardW = firstCard ? firstCard.getBoundingClientRect().width : 260;

        const styles = window.getComputedStyle(el);
        const gap = parseFloat(styles.columnGap || styles.gap || "20") || 20;

        const step = 4 * (cardW + gap);
        el.scrollBy({ left: dir * step, behavior: "smooth" });
    };

    const safeText = (s, n = 70) => {
        const t = String(s || "").trim();
        if (!t) return "";
        return t.length > n ? t.slice(0, n) + "…" : t;
    };

    return (
        <div className="ud-railWrap">
            <button className="ud-arrow ud-arrowLeft" onClick={() => scrollBy4(-1)} type="button">
                ‹
            </button>

            <div ref={railRef} className="ud-rail">
                {items.map((item, idx) => {
                    const key = (type === "movie" ? item?.movie?.id : item?.id) ?? idx;

                    const bg = getImage(item) || "";
                    const title = getTitle(item) || (type === "event" ? "Event" : "Movie");

                    const badgeLeft =
                        type === "movie" ? getYear(item) || "" : (getCity?.(item) || "");

                    const badgeRight =
                        type === "event" ? (getDate?.(item) || "") : "";

                    const desc =
                        type === "movie"
                            ? `⭐ Rating: ${Number(getRating(item) ?? 0).toFixed(1)}`
                            : safeText(getDesc?.(item) || item?.description || "");

                    return (
                        <div
                            key={key}
                            className="ud-card"
                            style={{ backgroundImage: bg ? `url("${bg}")` : "none" }}
                        >
                            <div className="ud-cardInner">
                                <div className="ud-cardTop">
                                    <div className="ud-badge">{badgeLeft}</div>
                                    <div className="ud-muted">{badgeRight}</div>
                                </div>

                                <div className="ud-cardTitle">{title}</div>

                                <div className="ud-cardDesc">{desc}</div>

                                <button className="ud-cta" onClick={() => onOpen(item)} type="button">
                                    Open
                                </button>
                            </div>
                        </div>
                    );
                })}
            </div>

            <button className="ud-arrow ud-arrowRight" onClick={() => scrollBy4(1)} type="button">
                ›
            </button>
        </div>
    );
}
