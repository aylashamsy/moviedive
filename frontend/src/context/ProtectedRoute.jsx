import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "./AuthContext.jsx";

export default function ProtectedRoute() {
    const { user, authLoading } = useAuth();

    if (authLoading) {
        return (
            <main className="page">
                <div className="panel">Loading...</div>
            </main>
        );
    }

    return user ? <Outlet /> : <Navigate to="/login" replace />;
}
