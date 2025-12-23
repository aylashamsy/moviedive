import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";
import "../styles/UserDashboard.css";

export default function UserDashboard() {
    const navigate = useNavigate();

    // Dummy events
    const events = useMemo(
        () =>
            Array.from({ length: 10 }).map((_, i) => ({
                id: i + 1,
                title: `Event #${i + 1}`,
                date: `2025-01-${String((i % 28) + 1).padStart(2, "0")}`,
                city: ["İzmir", "İstanbul", "Ankara", "Bursa"][i % 4],
                desc: "Short event description goes here.",
                cover: `https://picsum.photos/seed/dash-event-${i + 1}/900/600`,
            })),
        []
    );

    const [me, setMe] = useState(null);
    const [userId, setUserId] = useState(null);

    // Bunlar artık DtoUserMovieStatus listesi:
    const [favorites, setFavorites] = useState([]); // liked
    const [watched, setWatched] = useState([]);     // relation_type = WATCHED
    const [later, setLater] = useState([]);         // relation_type = LATER_WATCH

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

    // Backend boş liste için exception atıyor -> bunu empty kabul edeceğiz
    const safeGetList = async (url) => {
        try {
            const res = await api.get(url);
            return Array.isArray(res.data) ? res.data : [];
        } catch (e) {
            return [];
        }
    };

    // 1) Me -> userId
    useEffect(() => {
        const run = async () => {
            try {
                const res = await api.get("rest/api/movie_dive/users/auth/me");
                setMe(res.data);

                // ⚠️ burada id şart:
                const id = res.data?.id; // RegisterResponse içine id eklenmeli
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

    // Kart click -> movie detail
    const openMovie = (dto) => {
        const movieId = dto?.movie?.id;
        if (!movieId) return;
        navigate(`/movie/${movieId}`);
    };

    return (
        <div className="ud">
            <div className="ud-container">
                <header className="ud-hero">
                    <h1 className="ud-h1">Dashboard</h1>
                    <p className="ud-sub">Quick access to your lists and upcoming events.</p>
                </header>

                <DashboardSection
                    title="Events"
                    subtitle="Upcoming events near you"
                    isOpen={open.events}
                    onToggle={() => toggle("events")}
                >
                    <PosterRail
                        items={events}
                        type="event"
                        getImage={(e) => e.cover}
                        getTitle={(e) => e.title}
                        getYear={(e) => ""}
                        getRating={() => null}
                        onOpen={() => {}}
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

            {isOpen && (
                <div className="ud-railArea">
                    {empty ? <div className="ud-empty">{emptyText}</div> : children}
                </div>
            )}
        </section>
    );
}

/* 4 kart kaydırmalı rail */
function PosterRail({ items, type, getImage, getTitle, getYear, getRating, onOpen }) {
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

    return (
        <div className="ud-railWrap">
            <button className="ud-arrow ud-arrowLeft" onClick={() => scrollBy4(-1)} type="button">
                ‹
            </button>

            <div ref={railRef} className="ud-rail">
                {items.map((item, idx) => (
                    <div
                        key={(type === "movie" ? item?.movie?.id : item?.id) ?? idx}
                        className="ud-card"
                        style={{ backgroundImage: `url("${getImage(item) || ""}")` }}
                    >
                        <div className="ud-cardInner">
                            <div className="ud-cardTop">
                                <div className="ud-badge">{getYear(item) || (type === "event" ? item.city : "")}</div>
                                <div className="ud-muted">{type === "event" ? item.date : ""}</div>
                            </div>

                            <div className="ud-cardTitle">{getTitle(item) || "Movie"}</div>

                            <div className="ud-cardDesc">
                                {type === "movie"
                                    ? `⭐ Rating: ${Number(getRating(item) ?? 0).toFixed(1)}`
                                    : item.desc}
                            </div>

                            <button className="ud-cta" onClick={() => onOpen(item)} type="button">
                                Open
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            <button className="ud-arrow ud-arrowRight" onClick={() => scrollBy4(1)} type="button">
                ›
            </button>
        </div>
    );
}
