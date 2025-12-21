import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Avatar from "./Avatar";
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

    const isAuthRoute =
        location.pathname === "/login" || location.pathname === "/signup";

    // ✅ Logout + redirect
    const handleLogout = async () => {
        await logout();        // session + user state temizlenir
        navigate("/");         // ✅ ana sayfa
    };

    return (
        <header className="nav">
            <div className="nav-inner">
                <div className="nav-left" onClick={goHome} role="button" tabIndex={0}>
                    <span className="logo">MovieDive</span>
                </div>

                <div className="nav-center">
                    <div className="searchWrap">
                        <input
                            className="search"
                            placeholder="Search movies, events..."
                            type="text"
                        />
                    </div>
                </div>

                <div className="nav-right">
                    {!user ? (
                        <div className="nav-actions">
                            <button
                                className={`nav-btn ghost ${isAuthRoute ? "active" : ""}`}
                                onClick={openLogin}
                            >
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
