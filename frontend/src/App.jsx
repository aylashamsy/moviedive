import { Routes, Route, useLocation, useNavigate } from "react-router-dom";
import Navbar from "./components/Navbar";
import HomePage from "./pages/HomePage";
import UserDashboard from "./pages/UserDashboard";
import UserProfile from "./pages/UserProfile";
import MovieDetail from "./pages/MovieDetail";
import EventDetail from "./pages/EventDetail";
import NotFound from "./pages/NotFound";
import AuthModal from "./components/AuthModal";
import "./styles/Pages.css";
import ProtectedRoute from "./context/ProtectedRoute";
import UserPublicProfile  from "./pages/UserPublicProfile.jsx";

function App() {
    const location = useLocation();
    const navigate = useNavigate();

    const isLogin = location.pathname === "/login";
    const isSignup = location.pathname === "/signup";

    const closeModal = () => navigate("/");

    return (
        <>
            <Navbar />

            {/* Sayfalar */}
            <Routes element={<ProtectedRoute />}>
                <Route path="/" element={<HomePage />} />

                {/* Modals only routes (sayfa render etmez, sadece modal açar) */}
                <Route path="/login" element={<HomePage />} />
                <Route path="/signup" element={<HomePage />} />

                {/* Diğer sayfalar */}
                <Route path="/dashboard" element={<UserDashboard />} />
                <Route path="/profile" element={<UserProfile />} />
                <Route path="/movie/:id" element={<MovieDetail />} />
                <Route path="/event/:id" element={<EventDetail />} />
                <Route path="/profile/:username" element={<UserPublicProfile />} />

                <Route path="*" element={<NotFound />} />
            </Routes>

            {/* Login/Signup modal */}
            {(isLogin || isSignup) && (
                <AuthModal
                    mode={isLogin ? "login" : "signup"}
                    close={closeModal}
                    switchMode={(m) => navigate(`/${m}`)}
                    onAuthSuccess={closeModal}   // ✅ login/signup olunca kapanır
                />
            )}
        </>
    );
}
export default App;