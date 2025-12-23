import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api } from "../api/client";
import "../styles/UserPublicProfile.css";

export default function UserPublicProfile() {
    const { username } = useParams(); // ✅ /u/:username
    const navigate = useNavigate();

    const [user, setUser] = useState(null);

    // ✅ logged-in user info
    const [me, setMe] = useState(null);
    const meId = me?.id ?? null;

    const [followersCount, setFollowersCount] = useState(0);
    const [followingCount, setFollowingCount] = useState(0);
    const [isFollowing, setIsFollowing] = useState(false);

    // ✅ followers/following modal
    const [followModal, setFollowModal] = useState({
        open: false,
        mode: "followers", // "followers" | "following"
    });
    const [followList, setFollowList] = useState([]);
    const [followLoading, setFollowLoading] = useState(false);
    const [followError, setFollowError] = useState("");

    // rails open/close
    const [open, setOpen] = useState({
        events: false,
        favorites: false,
        watched: false,
        later: false,
    });

    const [events, setEvents] = useState([]);
    const [favorites, setFavorites] = useState([]);
    const [watched, setWatched] = useState([]);
    const [later, setLater] = useState([]);

    const [loading, setLoading] = useState({
        profile: true,
        favorites: false,
        watched: false,
        later: false,
    });

    const toggle = (key) => setOpen((p) => ({ ...p, [key]: !p[key] }));

    const safeGetList = async (url) => {
        try {
            const res = await api.get(url);
            return Array.isArray(res.data) ? res.data : [];
        } catch {
            return [];
        }
    };

    // ✅ session’dan “me” çek (DOĞRU YER: auth/me)
    useEffect(() => {
        api
            .get("/rest/api/movie_dive/users/auth/me")
            .then((res) => setMe(res.data ?? null))
            .catch(() => setMe(null));
    }, []);

    // ✅ follower/following count + isFollow
    const fetchRelationStats = async (targetUserId) => {
        try {
            const [fCountRes, fgCountRes] = await Promise.all([
                api.get(`/rest/api/movie_dive/relation/get/followercount/${targetUserId}`),
                api.get(`/rest/api/movie_dive/relation/get/followingcount/${targetUserId}`),
            ]);

            setFollowersCount(Number(fCountRes.data ?? 0));
            setFollowingCount(Number(fgCountRes.data ?? 0));

            // isFollow sadece login varsa anlamlı
            if (meId && meId !== targetUserId) {
                const isFollowRes = await api.get(
                    `/rest/api/movie_dive/relation/isfollow/${meId}/${targetUserId}`
                );
                setIsFollowing(Boolean(isFollowRes.data));
            } else {
                setIsFollowing(false);
            }
        } catch {
            setFollowersCount(0);
            setFollowingCount(0);
            setIsFollowing(false);
        }
    };

    // ✅ profile + lists fetch
    useEffect(() => {
        const run = async () => {
            try {
                setLoading((p) => ({ ...p, profile: true }));

                const profileRes = await api.get(
                    `/rest/api/movie_dive/profile/getusername/${encodeURIComponent(username)}`
                );

                const u = profileRes.data;
                setUser(u);

                setEvents([]);

                const userId = u?.id;
                if (!userId) {
                    setFavorites([]);
                    setWatched([]);
                    setLater([]);
                    return;
                }

                // ✅ relation stats
                await fetchRelationStats(userId);

                // ✅ lists
                setLoading((p) => ({ ...p, favorites: true, watched: true, later: true }));

                const [favDtos, watchedDtos, laterDtos] = await Promise.all([
                    safeGetList(`/rest/api/movie_dive/lists/getLikedMovies/${userId}`),
                    safeGetList(`/rest/api/movie_dive/lists/getWatchedMovie/${userId}`),
                    safeGetList(`/rest/api/movie_dive/lists/getWatchlaterMovie/${userId}`),
                ]);

                setFavorites(favDtos);
                setWatched(watchedDtos);
                setLater(laterDtos);
            } finally {
                setLoading((p) => ({
                    ...p,
                    profile: false,
                    favorites: false,
                    watched: false,
                    later: false,
                }));
            }
        };

        if (username) run().catch(() => {});
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [username, meId]); // ✅ meId değişirse isFollow re-check olması için

    // ✅ Kendim kendi profilime geldiysem /profile’a git (ID yoksa username ile)
    useEffect(() => {
        if (!me?.username) return;
        if (!username) return;

        const meU = String(me.username).toLowerCase().trim();
        const urlU = String(username).toLowerCase().trim();

        if (meU && urlU && meU === urlU) {
            navigate("/profile", { replace: true });
        }
    }, [me?.username, username, navigate]);

    // ✅ Follow modal helpers
    const openFollowModal = async (mode) => {
        const targetId = user?.id;
        if (!targetId) return;

        setFollowModal({ open: true, mode });
        setFollowList([]);
        setFollowError("");
        setFollowLoading(true);

        try {
            // ✅ DOĞRU: followers -> get/followers, following -> get/followings
            const url =
                mode === "followers"
                    ? `/rest/api/movie_dive/relation/get/followers/${targetId}`
                    : `/rest/api/movie_dive/relation/get/followings/${targetId}`;

            const res = await api.get(url);
            setFollowList(Array.isArray(res.data) ? res.data : []);
        } catch {
            setFollowError("Liste yüklenemedi.");
            setFollowList([]);
        } finally {
            setFollowLoading(false);
        }
    };

    const closeFollowModal = () => {
        setFollowModal((p) => ({ ...p, open: false }));
    };

    // ✅ item normalize (DtoGetAllFollow şekli farklı olabilir)
    const getFollowUsername = (x) =>
        x?.username ??
        x?.user?.username ??
        x?.followed?.username ??
        x?.follower?.username ??
        x?.followedUsername ??
        x?.followerUsername ??
        x?.targetUsername ??
        x?.sourceUsername ??
        null;

    const getFollowName = (x) => {
        const fn =
            x?.firstname ??
            x?.user?.firstname ??
            x?.followed?.firstname ??
            x?.follower?.firstname ??
            "";
        const sn =
            x?.surname ??
            x?.user?.surname ??
            x?.followed?.surname ??
            x?.follower?.surname ??
            "";
        const name = `${fn} ${sn}`.trim();
        return name || "";
    };

    const getFollowId = (x) =>
        x?.id ??
        x?.userId ??
        x?.user?.id ??
        x?.followed?.id ??
        x?.follower?.id ??
        x?.followedId ??
        x?.followerId ??
        null;

    const initial = useMemo(() => {
        const source = (user?.firstname || user?.username || "").trim();
        return source ? source[0].toUpperCase() : "U";
    }, [user?.firstname, user?.username]);

    const displayName = useMemo(() => {
        const fn = (user?.firstname || "").trim();
        const un = (user?.username || "").trim();
        return un || fn || "User";
    }, [user?.firstname, user?.username]);

    const cityName = useMemo(() => {
        const city = user?.cityName ?? user?.city ?? "";
        return city ? String(city) : "";
    }, [user?.cityName, user?.city]);

    const openMovie = (dto) => {
        const movieId = dto?.movie?.id;
        if (!movieId) return;
        navigate(`/movie/${movieId}`);
    };

    const handleFollow = async () => {
        const targetId = user?.id;
        if (!targetId) return;

        if (!meId) {
            navigate("/login");
            return;
        }

        if (meId === targetId) return;

        try {
            if (isFollowing) {
                await api.post(`/rest/api/movie_dive/relation/unfollow/${targetId}`);
                setIsFollowing(false);
                setFollowersCount((c) => Math.max(0, c - 1));
            } else {
                await api.post(`/rest/api/movie_dive/relation/follow/${targetId}`);
                setIsFollowing(true);
                setFollowersCount((c) => c + 1);
            }
        } catch {
            // sessiz geç
        }
    };

    if (!user || loading.profile) return null;

    return (
        <div className="up-page">
            <div className="up-card">
                <div className="up-header">
                    <div>
                        <h2 className="up-title">{user.username}</h2>
                        {(user.firstname || user.surname) && (
                            <p className="up-subtitle">
                                {(user.firstname || "").trim()} {(user.surname || "").trim()}
                            </p>
                        )}
                    </div>
                </div>

                <div className="up-grid">
                    {/* LEFT */}
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
                                emptyText={
                                    user?.id
                                        ? "No favorite movies yet."
                                        : "User id missing (backend must return id)."
                                }
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
                                emptyText={
                                    user?.id
                                        ? "No watched movies yet."
                                        : "User id missing (backend must return id)."
                                }
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
                                emptyText={
                                    user?.id
                                        ? "No movies in watch later yet."
                                        : "User id missing (backend must return id)."
                                }
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

                            {cityName && <div className="up-city">{cityName}</div>}

                            {/* ✅ clickable followers/following */}
                            <div className="up-stats">
                                <button
                                    className="up-stat up-statBtn"
                                    type="button"
                                    onClick={() => openFollowModal("followers")}
                                    title="Show followers"
                                >
                                    <div className="up-stat-number">{followersCount}</div>
                                    <div className="up-stat-label">Followers</div>
                                </button>

                                <button
                                    className="up-stat up-statBtn"
                                    type="button"
                                    onClick={() => openFollowModal("following")}
                                    title="Show following"
                                >
                                    <div className="up-stat-number">{followingCount}</div>
                                    <div className="up-stat-label">Following</div>
                                </button>
                            </div>

                            <div className="up-side-buttons">
                                <button
                                    className={`btn ${isFollowing ? "btn-ghost" : "btn-primary"}`}
                                    onClick={handleFollow}
                                >
                                    {!meId ? "Login to follow" : isFollowing ? "Following" : "Follow"}
                                </button>
                            </div>
                        </div>
                    </aside>
                </div>

                {/* ✅ modal */}
                {followModal.open && (
                    <FollowListModal
                        title={followModal.mode === "followers" ? "Followers" : "Following"}
                        loading={followLoading}
                        error={followError}
                        items={followList}
                        onClose={closeFollowModal}
                        getUsername={getFollowUsername}
                        getName={getFollowName}
                        getId={getFollowId}
                        onOpenUser={(item) => {
                            const un = getFollowUsername(item);
                            if (un) {
                                closeFollowModal();
                                navigate(`/u/${encodeURIComponent(un)}`); // ✅ FIX
                            }
                        }}
                    />
                )}
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
   POSTER RAIL
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

/* =========================
   FOLLOW LIST MODAL
   ========================= */
function FollowListModal({ title, loading, error, items, onClose, onOpenUser, getUsername, getName, getId }) {
    const onBackdrop = (e) => {
        if (e.target.classList.contains("flm-backdrop")) onClose();
    };

    return (
        <div className="flm-backdrop" onMouseDown={onBackdrop} role="presentation">
            <div className="flm-modal" role="dialog" aria-modal="true" aria-label={title}>
                <div className="flm-head">
                    <div className="flm-title">{title}</div>
                    <button className="flm-x" type="button" onClick={onClose}>
                        ✕
                    </button>
                </div>

                <div className="flm-body">
                    {loading && <div className="flm-muted">Loading...</div>}
                    {!loading && error && <div className="flm-error">{error}</div>}

                    {!loading && !error && items.length === 0 && <div className="flm-muted">No users.</div>}

                    {!loading && !error && items.length > 0 && (
                        <div className="flm-list">
                            {items.map((x, idx) => {
                                const un = getUsername(x) || "user";
                                const name = getName(x);
                                const id = getId(x);
                                const initial = (un?.[0] ?? "U").toUpperCase();

                                return (
                                    <button
                                        key={id ?? un ?? idx}
                                        className="flm-item"
                                        type="button"
                                        onClick={() => onOpenUser(x)}
                                    >
                                        <div className="flm-avatar">{initial}</div>
                                        <div className="flm-info">
                                            <div className="flm-username">{un}</div>
                                            {name && <div className="flm-name">{name}</div>}
                                        </div>
                                        <div className="flm-go">›</div>
                                    </button>
                                );
                            })}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
