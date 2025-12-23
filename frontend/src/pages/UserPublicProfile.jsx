import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api } from "../api/client";
import "../styles/UserPublicProfile.css";

export default function UserPublicProfile() {
    const { userId } = useParams(); // /u/:userId
    const navigate = useNavigate();

    const [user, setUser] = useState(null);

    const [followersCount, setFollowersCount] = useState(0);
    const [followingCount, setFollowingCount] = useState(0);
    const [isFollowing, setIsFollowing] = useState(false);

    // rails open/close (başta kapalı)
    const [open, setOpen] = useState({
        events: false,
        favorites: false,
        watched: false,
        later: false,
    });

    const [events, setEvents] = useState([]);     // şimdilik boş (endpoint yoksa)
    const [favorites, setFavorites] = useState([]); // DtoUserMovieStatus[]
    const [watched, setWatched] = useState([]);     // DtoUserMovieStatus[]
    const [later, setLater] = useState([]);         // DtoUserMovieStatus[]

    const [loading, setLoading] = useState({
        profile: true,
        favorites: false,
        watched: false,
        later: false,
    });

    const toggle = (key) => setOpen((p) => ({ ...p, [key]: !p[key] }));

    // backend boş liste için exception atıyorsa -> empty kabul
    const safeGetList = async (url) => {
        try {
            const res = await api.get(url);
            return Array.isArray(res.data) ? res.data : [];
        } catch (e) {
            return [];
        }
    };

    useEffect(() => {
        const run = async () => {
            try {
                setLoading((p) => ({ ...p, profile: true }));
                const profileRes = await api.get(`/rest/api/movie_dive/users/get_user/${userId}`);
                setUser(profileRes.data);

                // events endpoint yoksa boş kalsın
                setEvents([]);

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
            } finally {
                setLoading((p) => ({ ...p, profile: false }));
            }
        };

        if (userId) run().catch(() => {});
    }, [userId]);

    const initial = useMemo(() => {
        const source = (user?.firstname || user?.username || "").trim();
        return source ? source[0].toUpperCase() : "U";
    }, [user?.firstname, user?.username]);

    const displayName = useMemo(() => {
        const fn = (user?.firstname || "").trim();
        const un = (user?.username || "").trim();
        return un || fn || "User";
    }, [user?.firstname, user?.username]);

    const openMovie = (dto) => {
        const movieId = dto?.movie?.id;
        if (!movieId) return;
        navigate(`/movie/${movieId}`);
    };

    const handleFollow = async () => {
        // backend bağlayınca:
        // await api.post(`/rest/api/movie_dive/follow/${userId}`);
        setIsFollowing((v) => !v);
        setFollowersCount((c) => (isFollowing ? Math.max(0, c - 1) : c + 1));
    };

    if (!user || loading.profile) return null;

    return (
        <div className="up-page">
            <div className="up-card">
                <div className="up-header">
                    <div>
                        <h2 className="up-title">{user.username}</h2>
                    </div>
                </div>

                <div className="up-grid">
                    {/* ✅ LEFT: Dashboard-style sections + poster rails */}
                    <div className="up-left">
                        <div className="up-dash">
                            <DashboardSection
                                title="Events"
                                subtitle="Upcoming events near you"
                                isOpen={open.events}
                                onToggle={() => toggle("events")}
                                emptyText="No events yet."
                                empty={events.length === 0}
                            >
                                <PosterRail
                                    items={events}
                                    type="event"
                                    getImage={(e) => e?.cover}
                                    getTitle={(e) => e?.title}
                                    getYear={(e) => e?.city || ""}
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

                    {/* RIGHT */}
                    <aside className="up-right">
                        <div className="up-sidecard">
                            <div className="up-avatar">
                                <span className="up-avatar-text">{initial}</span>
                            </div>

                            <div className="up-name">{displayName}</div>

                            <div className="up-stats">
                                <div className="up-stat">
                                    <div className="up-stat-number">{followersCount}</div>
                                    <div className="up-stat-label">Followers</div>
                                </div>
                                <div className="up-stat">
                                    <div className="up-stat-number">{followingCount}</div>
                                    <div className="up-stat-label">Following</div>
                                </div>
                            </div>

                            <div className="up-side-buttons">
                                <button className={`btn ${isFollowing ? "btn-ghost" : "btn-primary"}`} onClick={handleFollow}>
                                    {isFollowing ? "Following" : "Follow"}
                                </button>
                            </div>
                        </div>
                    </aside>
                </div>
            </div>
        </div>
    );
}

/* =========================
   DASHBOARD-STYLE SECTION
   ========================= */
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

/* =========================
   POSTER RAIL (4 cards scroll)
   ========================= */
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

    const img = (x) => getImage(x) || "";
    const safeTitle = (x) => getTitle(x) || "Movie";

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
                        style={{ backgroundImage: `url("${img(item)}")` }}
                    >
                        <div className="ud-cardInner">
                            <div className="ud-cardTop">
                                <div className="ud-badge">{getYear(item) || ""}</div>
                                <div className="ud-muted">{type === "event" ? item?.date : ""}</div>
                            </div>

                            <div className="ud-cardTitle">{safeTitle(item)}</div>

                            <div className="ud-cardDesc">
                                {type === "movie"
                                    ? `⭐ Rating: ${Number(getRating(item) ?? 0).toFixed(1)}`
                                    : item?.desc}
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
