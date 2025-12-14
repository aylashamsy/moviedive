import "../styles/Pages.css";
import { useAuth } from "../context/AuthContext";

function UserDashboard() {
    const { user } = useAuth();

    return (
        <main className="page">
            <div className="panel">
                <h2>Dashboard</h2>
                <p>Welcome, <b>{user?.fullName || user?.username}</b></p>

                <div className="grid2">
                    <div className="card">
                        <h3>Favorites</h3>
                        <p>Will list liked movies.</p>
                    </div>
                    <div className="card">
                        <h3>My Events</h3>
                        <p>Will show subscribed events.</p>
                    </div>
                </div>
            </div>
        </main>
    );
}

export default UserDashboard;
