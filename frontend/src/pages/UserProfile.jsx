import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";
import "../styles/UserProfile.css";

export default function UserProfile() {
    const navigate = useNavigate();

    const [me, setMe] = useState(null);
    const [form, setForm] = useState({});
    const [cities, setCities] = useState([]);
    const [isEditing, setIsEditing] = useState(false);

    // ✅ counts
    const [followersCount, setFollowersCount] = useState(0);
    const [followingCount, setFollowingCount] = useState(0);

    // ✅ Change Password modal state
    const [isPwOpen, setIsPwOpen] = useState(false);

    // ✅ followers/following modal
    const [followModal, setFollowModal] = useState({
        open: false,
        mode: "followers", // "followers" | "following"
    });
    const [followList, setFollowList] = useState([]);
    const [followLoading, setFollowLoading] = useState(false);
    const [followError, setFollowError] = useState("");

    const safeArray = (x) => (Array.isArray(x) ? x : []);

    // ✅ follow list mapping (DtoGetAllFollow farklı olabilir)
    const getFollowUsername = (x) =>
        x?.username ??
        x?.user?.username ??
        x?.followed?.username ??
        x?.follower?.username ??
        x?.followedUsername ??
        x?.followerUsername ??
        x?.targetUsername ??
        x?.sourceUsername ??
        null;

    const getFollowName = (x) => {
        const fn =
            x?.firstname ??
            x?.user?.firstname ??
            x?.followed?.firstname ??
            x?.follower?.firstname ??
            "";
        const sn =
            x?.surname ??
            x?.user?.surname ??
            x?.followed?.surname ??
            x?.follower?.surname ??
            "";
        const name = `${fn} ${sn}`.trim();
        return name || "";
    };

    const getFollowId = (x) =>
        x?.id ??
        x?.userId ??
        x?.user?.id ??
        x?.followed?.id ??
        x?.follower?.id ??
        x?.followedId ??
        x?.followerId ??
        null;

    const fetchRelationCounts = async (userId) => {
        try {
            const [fCountRes, fgCountRes] = await Promise.all([
                api.get(`/rest/api/movie_dive/relation/get/followercount/${userId}`),
                api.get(`/rest/api/movie_dive/relation/get/followingcount/${userId}`),
            ]);
            setFollowersCount(Number(fCountRes.data ?? 0));
            setFollowingCount(Number(fgCountRes.data ?? 0));
        } catch {
            setFollowersCount(0);
            setFollowingCount(0);
        }
    };

    useEffect(() => {
        Promise.all([
            api.get("/rest/api/movie_dive/users/auth/me"),
           api.get("/rest/api/movie_dive/cities"),
        ]).then(([meRes, cityRes]) => {
            const u = meRes.data;
            setMe(u);
            setForm(u);
            setCities(safeArray(cityRes.data));

            // ✅ counts for my profile
            if (u?.id) fetchRelationCounts(u.id);
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

    const cityName = useMemo(() => {
        const c =
            form?.cityName ??
            form?.city ??
            form?.addressCity ??
            form?.address ??
            form?.location ??
            form?.province ??
            form?.il ??
            "";
        return (c || "").toString().trim();
    }, [form]);

    const handleChange = (e) =>
        setForm({ ...form, [e.target.name]: e.target.value });

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

        // ✅ update counts again (optional)
        if (updated?.id) fetchRelationCounts(updated.id);
    };

    const handleCancel = () => {
        setForm(me);
        setIsEditing(false);
    };

    // ✅ open followers/following modal
    const openFollowModal = async (mode) => {
        const myId = me?.id;
        if (!myId) return;

        setFollowModal({ open: true, mode });
        setFollowList([]);
        setFollowError("");
        setFollowLoading(true);

        try {
            const url =
                mode === "followers"
                    ? `/rest/api/movie_dive/relation/get/followers/${myId}`
                    : `/rest/api/movie_dive/relation/get/followings/${myId}`;

            const res = await api.get(url);
            setFollowList(safeArray(res.data));
        } catch {
            setFollowError("Liste yüklenemedi.");
            setFollowList([]);
        } finally {
            setFollowLoading(false);
        }
    };

    const closeFollowModal = () =>
        setFollowModal((p) => ({ ...p, open: false }));

    if (!me) return null;

    return (
        <div className="up-page">
            <div className="up-card">
                <div className="up-header">
                    <div>
                        <h2 className="up-title">My Profile</h2>
                        <p className="up-subtitle">
                            {isEditing
                                ? "You can update your information."
                                : "Your information is displayed as read-only."}
                        </p>
                    </div>
                </div>

                <div className="up-grid">
                    {/* LEFT */}
                    <div className="up-left">
                        <div className="up-section">
                            <ProfileField
                                label="Username"
                                value={form.username}
                                name="username"
                                editable={isEditing}
                                onChange={handleChange}
                            />
                            <ProfileField
                                label="First Name"
                                value={form.firstname}
                                name="firstname"
                                editable={isEditing}
                                onChange={handleChange}
                            />
                            <ProfileField
                                label="Surname"
                                value={form.surname}
                                name="surname"
                                editable={isEditing}
                                onChange={handleChange}
                            />
                            <ProfileField
                                label="Email"
                                value={form.email}
                                name="email"
                                editable={isEditing}
                                onChange={handleChange}
                            />
                            <ProfileField
                                label="Phone Number"
                                value={form.phoneNumber}
                                name="phoneNumber"
                                editable={isEditing}
                                onChange={handleChange}
                            />

                            <div className="up-row">
                                <div className="up-label">City</div>
                                <div className="up-value">
                                    {isEditing ? (
                                        <select value={form.cityId ?? ""} onChange={handleCityChange}>
                                            <option value="" disabled>
                                                Select city
                                            </option>
                                            {cities.map((c) => (
                                                <option key={c.id} value={c.id}>
                                                    {c.name}
                                                </option>
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
                            {cityName && <div className="up-city">{cityName}</div>}

                            {/* ✅ clickable followers/following */}
                            <div className="up-stats">
                                <button
                                    className="up-stat up-statBtn"
                                    type="button"
                                    onClick={() => openFollowModal("followers")}
                                    title="Show followers"
                                >
                                    <div className="up-stat-number">{followersCount}</div>
                                    <div className="up-stat-label">Followers</div>
                                </button>

                                <button
                                    className="up-stat up-statBtn"
                                    type="button"
                                    onClick={() => openFollowModal("following")}
                                    title="Show following"
                                >
                                    <div className="up-stat-number">{followingCount}</div>
                                    <div className="up-stat-label">Following</div>
                                </button>
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

                {/* ✅ followers/following modal */}
                {followModal.open && (
                    <FollowListModal
                        title={followModal.mode === "followers" ? "Followers" : "Following"}
                        loading={followLoading}
                        error={followError}
                        items={followList}
                        onClose={closeFollowModal}
                        getUsername={getFollowUsername}
                        getName={getFollowName}
                        getId={getFollowId}
                        onOpenUser={(item) => {
                            const un = getFollowUsername(item);
                            if (un) {
                                closeFollowModal();
                                navigate(`/u/${encodeURIComponent(un)}`);
                            }
                        }}
                    />
                )}
            </div>

            {/* ✅ CHANGE PASSWORD MODAL */}
            <ChangePasswordModal open={isPwOpen} onClose={() => setIsPwOpen(false)} />
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
        if (!newPassword) {
            setError("Enter your new password");
            return;
        }

        try {
            setLoading(true);
            await api.put("/rest/api/movie_dive/profile/change-password", {
                oldPassword,
                newPassword,
                confirmPassword: confirm,
            });
            setOk("Password changed successfully.");
            onClose();
        } catch (err) {
            setError(err?.response?.data?.message || "Request failed.");
        } finally {
            setLoading(false);
        }
    };

    const stop = (e) => e.stopPropagation();

    return (
        <div className="up-modal-backdrop" onClick={onClose}>
            <div className="up-modal" onClick={stop} role="dialog" aria-modal="true">
                <div className="up-modal-head">
                    <div className="up-modal-title">Change Password</div>
                    <button className="up-modal-x" onClick={onClose} aria-label="Close">
                        ✕
                    </button>
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

/* =========================
   FOLLOW LIST MODAL
   ========================= */
function FollowListModal({ title, loading, error, items, onClose, onOpenUser, getUsername, getName, getId }) {
    const onBackdrop = (e) => {
        if (e.target.classList.contains("flm-backdrop")) onClose();
    };

    return (
        <div className="flm-backdrop" onMouseDown={onBackdrop} role="presentation">
            <div className="flm-modal" role="dialog" aria-modal="true" aria-label={title}>
                <div className="flm-head">
                    <div className="flm-title">{title}</div>
                    <button className="flm-x" type="button" onClick={onClose}>
                        ✕
                    </button>
                </div>

                <div className="flm-body">
                    {loading && <div className="flm-muted">Loading...</div>}
                    {!loading && error && <div className="flm-error">{error}</div>}

                    {!loading && !error && items.length === 0 && (
                        <div className="flm-muted">No users.</div>
                    )}

                    {!loading && !error && items.length > 0 && (
                        <div className="flm-list">
                            {items.map((x, idx) => {
                                const un = getUsername(x) || "user";
                                const name = getName(x);
                                const id = getId(x);
                                const initial = (un?.[0] ?? "U").toUpperCase();

                                return (
                                    <button
                                        key={id ?? un ?? idx}
                                        className="flm-item"
                                        type="button"
                                        onClick={() => onOpenUser(x)}
                                    >
                                        <div className="flm-avatar">{initial}</div>
                                        <div className="flm-info">
                                            <div className="flm-username">{un}</div>
                                            {name && <div className="flm-name">{name}</div>}
                                        </div>
                                        <div className="flm-go">›</div>
                                    </button>
                                );
                            })}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
