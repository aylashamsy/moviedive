import { createContext, useContext, useMemo, useState } from "react";

const AuthContext = createContext(null);

const FAKE_USER = {
    username: "ayla",
    fullName: "Ayla Shamsi",
    email: "ayla@example.com",
    avatarUrl: null, // kullanıcı foto eklemediyse null
};

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);

    // Fake login
    function login(username, password) {
        if (username === "ayla" && password === "1234") {
            setUser(FAKE_USER);
            return true;
        }
        return false;
    }

    // Fake signup
    function signup({ username, email, fullName }) {
        setUser({
            username,
            email,
            fullName: fullName || username,
            avatarUrl: null,
        });
        return true;
    }

    function logout() {
        setUser(null);
    }

    const value = useMemo(() => ({ user, login, signup, logout }), [user]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    return useContext(AuthContext);
}
