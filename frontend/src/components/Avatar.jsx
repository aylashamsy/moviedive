function Avatar({ user, size = 36 }) {
    const initials = (user?.fullName || user?.username || "U")
        .split(" ")
        .slice(0, 2)
        .map((p) => p[0]?.toUpperCase())
        .join("");

    if (user?.avatarUrl) {
        return (
            <img
                src={user.avatarUrl}
                alt="avatar"
                style={{
                    width: size,
                    height: size,
                    borderRadius: "999px",
                    objectFit: "cover",
                    display: "block",
                }}
            />
        );
    }

    // default boş daire + initials
    return (
        <div
            style={{
                width: size,
                height: size,
                borderRadius: "999px",
                display: "grid",
                placeItems: "center",
                border: "1px solid rgba(255,255,255,0.18)",
                background: "rgba(255,255,255,0.06)",
                color: "white",
                fontWeight: 700,
                letterSpacing: "0.5px",
                userSelect: "none",
            }}
        >
            {initials}
        </div>
    );
}

export default Avatar;
