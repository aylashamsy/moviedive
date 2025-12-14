import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import "../styles/AuthForms.css";

function SignupForm({ onSuccess, goLogin }) {
    const { signup } = useAuth();

    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [firstname ,setFirstName]=useState("")
    const [lastname, setLastName] = useState("");
    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!username || !email || !password) {
            setError("Please fill required fields.");
            return;
        }
        if (password !== confirm) {
            setError("Passwords do not match.");
            return;
        }

        setError("");
        signup({ username, email, fullName });
        onSuccess && onSuccess(); // ✅ modal kapanır
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2 className="auth-title">Sign Up</h2>

            <div className="auth-field">
                <label>Username *</label>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="your username"
                />
            </div>

            <div className="auth-field">
                <label>Email *</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="you@mail.com"
                />
            </div>

            <div className="auth-field">
                <label>First Name</label>
                <input
                    type="text"
                    value={firstname}
                    onChange={(e) => setFirstName(e.target.value)}
                    placeholder="Ayla"
                />
            </div>

            <div className="auth-field">
                <label>Last Name</label>
                <input
                    type="text"
                    value={lastname}
                    onChange={(e) => setLastName(e.target.value)}
                    placeholder="shamsi"
                />
            </div>

            <div className="auth-field">
                <label>Password *</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                />
            </div>

            <div className="auth-field">
                <label>Confirm Password *</label>
                <input
                    type="password"
                    value={confirm}
                    onChange={(e) => setConfirm(e.target.value)}
                    placeholder="••••••••"
                />
            </div>

            {error && <div className="auth-error">{error}</div>}

            <button className="auth-btn" type="submit">
                Create Account
            </button>

            <button className="auth-secondary" type="button" onClick={goLogin}>
                Already have an account? Log in
            </button>
        </form>
    );
}

export default SignupForm;
