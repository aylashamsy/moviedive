import { useEffect, useState } from "react";
import { api } from "../api/client";
import "../styles/UserProfile.css";

export default function UserProfile() {
    const [me, setMe] = useState(null);
    const [form, setForm] = useState({});
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        api.get("/rest/api/movie_dive/profile/get/me")
            .then(res => {
                setMe(res.data);
                setForm(res.data);
            });
    }, []);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleUpdate = async () => {
        if (!isEditing) {
            setIsEditing(true);
            return;
        }

        await api.put("/rest/api/movie_dive/profile/update", form);
        setMe(form);
        setIsEditing(false);
    };

    if (!me) return null;

    return (
        <div className="up-page">
            <div className="up-card">
                <h2 className="up-title">My Profile</h2>
                <p className="up-subtitle">
                    {isEditing ? "You can update your information." : "Your information is displayed as read-only."}
                </p>

                <div className="up-section">

                    <ProfileField label="Username" value={form.username} name="username"
                                  editable={isEditing} onChange={handleChange} />

                    <ProfileField label="First Name" value={form.firstname} name="firstname"
                                  editable={isEditing} onChange={handleChange} />

                    <ProfileField label="Surname" value={form.surname} name="surname"
                                  editable={isEditing} onChange={handleChange} />

                    <ProfileField label="Email" value={form.email} name="email"
                                  editable={isEditing} onChange={handleChange} />

                    <ProfileField label="Phone Number" value={form.phoneNumber} name="phoneNumber"
                                  editable={isEditing} onChange={handleChange} />

                    <ProfileField label="City" value={form.cityName} name="city"
                                  editable={isEditing} onChange={handleChange} />

                </div>

                <div className="up-buttons">
                    <button className="btn btn-primary" onClick={handleUpdate}>
                        {isEditing ? "Save" : "Update"}
                    </button>

                    <button className="btn btn-ghost">Change Password</button>

                    <button className="btn btn-danger">Delete Account</button>
                </div>
            </div>
        </div>
    );
}

/* Küçük reusable component */
function ProfileField({ label, value, name, editable, onChange }) {
    return (
        <div className="up-row">
            <div className="up-label">{label}</div>
            <div className="up-value">
                {editable ? (
                    <input
                        name={name}
                        value={value || ""}
                        onChange={onChange}
                    />
                ) : (
                    value
                )}
            </div>
        </div>
    );
}
