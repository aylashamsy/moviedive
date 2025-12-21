import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "../styles/AuthForms.css";

function LoginForm({ onSuccess }) {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!email || !password) {
            setError("Please fill required fields.");
            return;
        }

        const ok = await login(email, password); // ✅ async login

        if (!ok) {
            setError("Wrong email or password");
            return;
        }

        setError("");
        onSuccess?.();          // modal kapatıyorsan
        navigate("/");   // ✅ kendi sayfasına git
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2 className="auth-title">Log In</h2>

            <div className="auth-field">
                <label>Email</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="you@mail.com"
                />
            </div>

            <div className="auth-field">
                <label>Password</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                />
            </div>

            {error && <div className="auth-error">{error}</div>}

            <button className="auth-btn" type="submit">
                Log In
            </button>
        </form>
    );
}

export default LoginForm;
