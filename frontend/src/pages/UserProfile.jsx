import { useEffect, useMemo, useState } from "react";
import { api } from "../api/client";
import "../styles/UserProfile.css";

export default function UserProfile() {
    const [me, setMe] = useState(null);
    const [form, setForm] = useState({});
    const [cities, setCities] = useState([]);
    const [isEditing, setIsEditing] = useState(false);

    const [followersCount] = useState(0);
    const [followingCount] = useState(0);

    // ✅ Change Password modal state
    const [isPwOpen, setIsPwOpen] = useState(false);

    useEffect(() => {
        Promise.all([
            api.get("/rest/api/movie_dive/profile/get/me"),
            api.get("/rest/api/movie_dive/cities"),
        ]).then(([meRes, cityRes]) => {
            setMe(meRes.data);
            setForm(meRes.data);
            setCities(cityRes.data);
        });
    }, []);

    const initial = useMemo(() => {
        const source = (form?.firstname || form?.username || "").trim();
        return source ? source[0].toUpperCase() : "U";
    }, [form?.firstname, form?.username]);

    const displayName = useMemo(() => {
        const fn = (form?.firstname || "").trim();
        const un = (form?.username || "").trim();
        return un || fn || "User";
    }, [form?.firstname, form?.username]);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleCityChange = (e) => {
        const newCityId = Number(e.target.value);
        const selected = cities.find((c) => Number(c.id) === newCityId);
        setForm({ ...form, cityId: newCityId, cityName: selected?.name ?? "" });
    };

    const handleUpdate = async () => {
        if (!isEditing) {
            setIsEditing(true);
            return;
        }

        const payload = {
            ...form,
            cityId: form.cityId ? Number(form.cityId) : null,
            password: null,
            role: null,
        };

        const res = await api.put("/rest/api/movie_dive/profile/update", payload);
        const updated = res.data ?? payload;

        setMe(updated);
        setForm(updated);
        setIsEditing(false);
    };

    const handleCancel = () => {
        setForm(me);
        setIsEditing(false);
    };

    if (!me) return null;

    return (
        <div className="up-page">
            <div className="up-card">
                <div className="up-header">
                    <div>
                        <h2 className="up-title">My Profile</h2>
                        <p className="up-subtitle">
                            {isEditing ? "You can update your information." : "Your information is displayed as read-only."}
                        </p>
                    </div>
                </div>

                <div className="up-grid">
                    {/* LEFT */}
                    <div className="up-left">
                        <div className="up-section">
                            <ProfileField label="Username" value={form.username} name="username" editable={isEditing} onChange={handleChange} />
                            <ProfileField label="First Name" value={form.firstname} name="firstname" editable={isEditing} onChange={handleChange} />
                            <ProfileField label="Surname" value={form.surname} name="surname" editable={isEditing} onChange={handleChange} />
                            <ProfileField label="Email" value={form.email} name="email" editable={isEditing} onChange={handleChange} />
                            <ProfileField label="Phone Number" value={form.phoneNumber} name="phoneNumber" editable={isEditing} onChange={handleChange} />

                            <div className="up-row">
                                <div className="up-label">City</div>
                                <div className="up-value">
                                    {isEditing ? (
                                        <select value={form.cityId ?? ""} onChange={handleCityChange}>
                                            <option value="" disabled>Select city</option>
                                            {cities.map((c) => (
                                                <option key={c.id} value={c.id}>{c.name}</option>
                                            ))}
                                        </select>
                                    ) : (
                                        form.cityName || "-"
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* RIGHT */}
                    <aside className="up-right">
                        <div className="up-sidecard">
                            <div className="up-avatar">
                                <span className="up-avatar-text">{initial}</span>
                            </div>

                            <div className="up-name">{displayName}</div>

                            <div className="up-stats">
                                <div className="up-stat">
                                    <div className="up-stat-number">{followersCount}</div>
                                    <div className="up-stat-label">Followers</div>
                                </div>
                                <div className="up-stat">
                                    <div className="up-stat-number">{followingCount}</div>
                                    <div className="up-stat-label">Following</div>
                                </div>
                            </div>

                            <div className="up-side-buttons">
                                <button className="btn btn-primary" onClick={handleUpdate}>
                                    {isEditing ? "Save" : "Update"}
                                </button>

                                {isEditing && (
                                    <button className="btn btn-ghost" onClick={handleCancel}>
                                        Cancel
                                    </button>
                                )}

                                {/* ✅ OPEN MODAL */}
                                <button className="btn btn-ghost" onClick={() => setIsPwOpen(true)}>
                                    Change Password
                                </button>

                                <button className="btn btn-danger">Delete Account</button>
                            </div>
                        </div>
                    </aside>
                </div>
            </div>

            {/* ✅ MODAL */}
            <ChangePasswordModal
                open={isPwOpen}
                onClose={() => setIsPwOpen(false)}
            />
        </div>
    );
}

function ProfileField({ label, value, name, editable, onChange }) {
    return (
        <div className="up-row">
            <div className="up-label">{label}</div>
            <div className="up-value">
                {editable ? (
                    <input name={name} value={value || ""} onChange={onChange} />
                ) : (
                    value ?? "-"
                )}
            </div>
        </div>
    );
}

/* =========================
   CHANGE PASSWORD MODAL
   ========================= */
function ChangePasswordModal({ open, onClose }) {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirm, setConfirm] = useState("");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [ok, setOk] = useState("");

    useEffect(() => {
        if (!open) {
            setOldPassword("");
            setNewPassword("");
            setConfirm("");
            setLoading(false);
            setError("");
            setOk("");
        }
    }, [open]);

    if (!open) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (newPassword !== confirm) {
            setError("New passwords do not match.");
            return;
        }
        if(newPassword ==null){
            setError("Enter your new password");
            return;
        }

        try {
            await api.put("/rest/api/movie_dive/profile/change-password", {
                oldPassword,
                newPassword,
                confirmPassword: confirm
            });
            setOk("Password changed successfully.");
            onClose();
        } catch (err) {
            setError(err?.response?.data?.message || "Request failed.");
        }
    };


    const stop = (e) => e.stopPropagation();

    return (
        <div className="up-modal-backdrop" onClick={onClose}>
            <div className="up-modal" onClick={stop} role="dialog" aria-modal="true">
                <div className="up-modal-head">
                    <div className="up-modal-title">Change Password</div>
                    <button className="up-modal-x" onClick={onClose} aria-label="Close">✕</button>
                </div>

                <form onSubmit={handleSubmit} className="up-modal-body">
                    <label className="up-field">
                        <span className="up-field-label">Old Password</span>
                        <input
                            type="password"
                            value={oldPassword}
                            onChange={(e) => setOldPassword(e.target.value)}
                            placeholder="Enter your current password"
                            autoFocus
                        />
                    </label>

                    <label className="up-field">
                        <span className="up-field-label">New Password</span>
                        <input
                            type="password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            placeholder="Enter a new password"
                        />
                    </label>

                    <label className="up-field">
                        <span className="up-field-label">Confirm New Password</span>
                        <input
                            type="password"
                            value={confirm}
                            onChange={(e) => setConfirm(e.target.value)}
                            placeholder="Repeat new password"
                        />
                    </label>

                    {error && <div className="up-msg up-msg-error">{error}</div>}
                    {ok && <div className="up-msg up-msg-ok">{ok}</div>}

                    <div className="up-modal-actions">
                        <button type="button" className="btn btn-ghost" onClick={onClose} disabled={loading}>
                            Cancel
                        </button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? "Saving..." : "Save Password"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
