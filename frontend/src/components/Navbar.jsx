import { useEffect, useRef, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Search, Film, User as UserIcon } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import Avatar from "./Avatar";
import { api } from "../api/client";
import "../styles/Navbar.css";

function Navbar() {
    const navigate = useNavigate();
    const location = useLocation();
    const { user, logout } = useAuth();

    const goHome = () => navigate("/");
    const openLogin = () => navigate("/login");
    const openSignup = () => navigate("/signup");
    const goProfile = () => navigate("/profile");
    const goDashboard = () => navigate("/dashboard");

    const isAuthRoute = location.pathname === "/login" || location.pathname === "/signup";

    // ✅ Logout + redirect
    const handleLogout = async () => {
        await logout();
        navigate("/");
    };

    // -----------------------
    // SEARCH STATE
    // -----------------------
    const [q, setQ] = useState("");
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [results, setResults] = useState({ movies: [], users: [] });
    const wrapRef = useRef(null);
    const inputRef = useRef(null);

    // dışarı tıklayınca kapat
    useEffect(() => {
        const onDoc = (e) => {
            if (wrapRef.current && !wrapRef.current.contains(e.target)) setOpen(false);
        };
        document.addEventListener("mousedown", onDoc);
        return () => document.removeEventListener("mousedown", onDoc);
    }, []);

    // debounce arama
    // debounce arama
    useEffect(() => {
        const term = q.trim();
        if (!term) {
            setResults({ movies: [], users: [] });
            setOpen(false);
            return;
        }

        const t = setTimeout(async () => {
            setLoading(true);

            try {
                // ✅ MOVIE SEARCH (senin endpoint)
                let movies = [];
                try {
                    const mRes = await api.get(
                        `/rest/api/movie_dive/getbyname/${encodeURIComponent(term)}`);

                    // bazen backend tek movie döner, bazen liste döner -> ikisini de handle edelim
                    if (Array.isArray(mRes.data)) movies = mRes.data;
                    else if (mRes.data) movies = [mRes.data];
                } catch {
                    movies = [];
                }

                // ✅ USER SEARCH (sende tek-user endpoint var)
                let users = [];
                try {
                    const uRes = await api.get(
                        `/rest/api/movie_dive/profile/getusername/${encodeURIComponent(term)}`
                    );
                    if (uRes?.data?.username) users = [uRes.data];
                } catch {
                    users = [];
                }

                setResults({
                    movies: movies.slice(0, 6),
                    users: users.slice(0, 4),
                });

                setOpen(true);
            } finally {
                setLoading(false);
            }
        }, 300);

        return () => clearTimeout(t);
    }, [q]);


    const closeAndClear = () => {
        setOpen(false);
        setQ("");
    };

    const onSelectMovie = (movieId) => {
        closeAndClear();
        navigate(`/movie/${movieId}`);
    };

    const onSelectUser = (username) => {
        closeAndClear();
        navigate(`/profile/${username}`); // ✅ public profile
    };
    const hasAny = results.movies.length > 0 || results.users.length > 0;

    // Enter -> öncelik movie sonra user
    const onKeyDown = (e) => {
        if (e.key === "Escape") {
            setOpen(false);
            return;
        }
        if (e.key === "Enter") {
            const firstMovie = results.movies[0];
            const firstUser = results.users[0];
            if (firstMovie?.id) onSelectMovie(firstMovie.id);
            else if (firstUser?.username) onSelectUser(firstUser.username);
        }
    };

    return (
        <header className="nav">
            <div className="nav-inner">
                <div className="nav-left" onClick={goHome} role="button" tabIndex={0}>
                    <span className="logo">MovieDive</span>
                </div>

                <div className="nav-center">
                    <div className="searchWrap" ref={wrapRef}>
                        <Search size={18} className="searchIcon" />
                        <input
                            ref={inputRef}
                            className="search"
                            placeholder="Search movies or users..."
                            type="text"
                            value={q}
                            onChange={(e) => setQ(e.target.value)}
                            onFocus={() => q.trim() && setOpen(true)}
                            onKeyDown={onKeyDown}
                        />

                        {open && (
                            <div className="searchDrop">
                                {loading && <div className="searchEmpty">Searching...</div>}

                                {!loading && !hasAny && <div className="searchEmpty">No results</div>}

                                {/* MOVIES */}
                                {!loading && results.movies.length > 0 && (
                                    <div className="searchGroup">
                                        <div className="searchGroupTitle">
                                            <Film size={14} /> Movies
                                        </div>

                                        {results.movies.map((m) => {
                                            const poster =
                                                m.poster_url || m.poster_path || m.posterPath || m.poster || "";

                                            return (
                                                <button
                                                    key={m.id}
                                                    type="button"
                                                    className="searchItem"
                                                    onClick={() => onSelectMovie(m.id)}
                                                >
                                                    <div
                                                        className="searchThumb"
                                                        style={{ backgroundImage: `url("${poster}")` }}
                                                    />
                                                    <div className="searchItemText">
                                                        <div className="searchItemTitle">{m.title}</div>
                                                        <div className="searchItemSub">
                                                            {m.release_date ? String(m.release_date).slice(0, 4) : ""}
                                                        </div>
                                                    </div>
                                                </button>
                                            );
                                        })}
                                    </div>
                                )}

                                {/* USERS */}
                                {!loading && results.users.length > 0 && (
                                    <div className="searchGroup">
                                        <div className="searchGroupTitle">
                                            <UserIcon size={14} /> Users
                                        </div>

                                        {results.users.map((u) => (
                                            <button
                                                key={u.username}
                                                type="button"
                                                className="searchItem"
                                                onMouseDown={(e) => e.preventDefault()}
                                                onClick={() => onSelectUser(u.username)}
                                            >
                                                <div className="searchAvatar">
                                                    {String(u.username || "?").charAt(0).toUpperCase()}
                                                </div>
                                                <div className="searchItemText">
                                                    <div className="searchItemTitle">{u.username}</div>
                                                    <div className="searchItemSub">{u.email || ""}</div>
                                                </div>
                                            </button>
                                        ))}
                                    </div>
                                )}

                                <div className="searchHint">
                                    <span>Enter</span> to open first result • <span>Esc</span> to close
                                </div>
                            </div>
                        )}
                    </div>
                </div>

                <div className="nav-right">
                    {!user ? (
                        <div className="nav-actions">
                            <button className={`nav-btn ghost ${isAuthRoute ? "active" : ""}`} onClick={openLogin}>
                                Log in
                            </button>

                            <button className="nav-btn solid" onClick={openSignup}>
                                Sign up
                            </button>
                        </div>
                    ) : (
                        <div className="nav-user">
                            <button className="nav-link" onClick={goDashboard}>
                                Dashboard
                            </button>

                            <button className="avatar-btn" onClick={goProfile} title="Profile">
                                <Avatar user={user} size={36} />
                            </button>

                            <button className="nav-btn ghost" onClick={handleLogout}>
                                Logout
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </header>
    );
}

export default Navbar;
