import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "../styles/AuthForms.css";

function SignupForm({ onSuccess, goLogin }) {
    const { signupAndLogin } = useAuth();
    const navigate = useNavigate();

    const [step, setStep] = useState(1);

    // step 1
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");

    // step 2
    const [username, setUsername] = useState("");
    const [firstname, setFirstName] = useState("");
    const [surname, setSurname] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [cityId, setCityId] = useState(""); // ✅ DTO: Long cityId

    const [cities, setCities] = useState([]); // dropdown için
    const [error, setError] = useState("");

    // ✅ (Opsiyonel ama öneririm) şehirleri backend’den çek
    // Backend’de: GET /rest/api/movie_dive/cities gibi bir endpoint olmalı
    useEffect(() => {
        const loadCities = async () => {
            try {
                const res = await fetch(
                    "http://localhost:8080/rest/api/movie_dive/cities",{
                        method: "GET",
                        credentials: "include", // ✅ çok kritik
                    }
                );
                if (!res.ok) return;
                const data = await res.json();
                setCities(data); // beklenen: [{id:1,name:"Adana"}, ...]
            } catch (e) {
                // şehir endpoint yoksa sorun etme, manuel liste de kullanabilirsin
            }
        };
        loadCities();
    }, []);

    const goNext = (e) => {
        e.preventDefault();

        if (!email || !password || !confirm) {
            setError("Please fill required fields.");
            return;
        }
        if (password !== confirm) {
            setError("Passwords do not match.");
            return;
        }

        setError("");
        setStep(2);
    };

    const goBack = () => {
        setError("");
        setStep(1);
    };

    const handleFinalSubmit = async (e) => {
        e.preventDefault();

        if (!username || !firstname || !surname) {
            setError("Please fill required fields.");
            return;
        }

        if (!cityId) {
            setError("Please select a city.");
            return;
        }

        const dtoUser = {
            username,
            password,
            firstname,
            surname,
            email,
            phoneNumber: phoneNumber?.trim() ? phoneNumber.trim() : null,
            cityId: cityId ? Number(cityId) : null, // ✅ Long beklediği için number yap
            role: null, // backend default USER yapıyorsa null bırak
        };

        const ok = await signupAndLogin(dtoUser);
        if (!cityId) {
            setError("Please select a city.");
            return;
        }

        if (!ok) {
            setError("Sign up failed. Please try again.");
            return;
        }

        setError("");
        onSuccess?.();
        navigate("/");
    };

    // Step 1
    if (step === 1) {
        return (
            <form onSubmit={goNext}>
                <h2 className="auth-title">Sign Up</h2>

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
                    Continue
                </button>


            </form>
        );
    }

    // Step 2
    return (
        <form onSubmit={handleFinalSubmit}>
            <h2 className="auth-title">Complete Profile</h2>

            <div className="auth-field">
                <label>Username *</label>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="your username"
                />
            </div>

            <div className="auth-row">
                <div className="auth-field">
                    <label>First Name *</label>
                    <input
                        type="text"
                        value={firstname}
                        onChange={(e) => setFirstName(e.target.value)}
                        placeholder="Name"
                    />
                </div>

                <div className="auth-field">
                    <label>Last Name *</label>
                    <input
                        type="text"
                        value={surname}
                        onChange={(e) => setSurname(e.target.value)}
                        placeholder="Last Name"
                    />
                </div>
            </div>

            <div className="auth-field">
                <label>Phone Number *</label>
                <input
                    type="tel"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    placeholder="0 5xx xxx xx xx"
                />
            </div>

            <div className="auth-field">
                <label>City *</label>

                {/* ✅ Şehir endpoint varsa dropdown dolacak */}
                {cities?.length > 0 ? (
                    <select value={cityId} onChange={(e) => setCityId(e.target.value)}>
                        <option value="">Select a city</option>
                        {cities.map((c) => (
                            <option key={c.id} value={c.id}>
                                {c.name}
                            </option>
                        ))}
                    </select>
                ) : (
                    // ✅ şehir endpoint yoksa geçici manuel seçenek
                    <input
                        type="number"
                        value={cityId}
                        onChange={(e) => setCityId(e.target.value)}
                        placeholder="Enter cityId (e.g., 35 for İzmir)"
                    />
                )}
            </div>

            {error && <div className="auth-error">{error}</div>}

            <button className="auth-btn" type="submit">
                Create Account
            </button>

            <button className="auth-secondary" type="button" onClick={goBack}>
                Back
            </button>
        </form>
    );
}

export default SignupForm;
