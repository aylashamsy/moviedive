import "../styles/Pages.css";
import { useAuth } from "../context/AuthContext";

export default function UserProfile() {
    const { user } = useAuth();

    // ✅ fullName yok → firstname + surname
    const fullName =
        [user?.firstname, user?.surname].filter(Boolean).join(" ") || "-";

    return (
        <main className="page">
            <div className="panel">
                <h2>User Profile</h2>

                <div className="profileRow">
                    <div className="profileBlock">
                        <div className="profileLabel">Username</div>
                        <div className="profileValue">{user?.username ?? "-"}</div>
                    </div>

                    <div className="profileBlock">
                        <div className="profileLabel">Full Name</div>
                        <div className="profileValue">{fullName}</div>
                    </div>

                    <div className="profileBlock">
                        <div className="profileLabel">Email</div>
                        <div className="profileValue">{user?.email ?? "-"}</div>
                    </div>
                </div>
            </div>
        </main>
    );
}
