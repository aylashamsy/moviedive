import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import "../styles/AuthForms.css";

function LoginForm({ onSuccess }) {
    const { login } = useAuth();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();

        const ok = login(username, password);

        if (!ok) {
            setError("Wrong username or password");
        } else {
            setError("");
            onSuccess && onSuccess(); // ✅ modal kapat
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2 className="auth-title">Log In</h2>

            <div className="auth-field">
                <label>Username</label>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="username"
                />
            </div>

            <div className="auth-field">
                <label>Password</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="password"
                />
            </div>

            {error && <div className="auth-error">{error}</div>}

            <button className="auth-btn" type="submit">
                Log In
            </button>

            <div className="auth-hint">
                Fake account: <b>ayla</b> / <b>1234</b>
            </div>
        </form>
    );
}

export default LoginForm;
