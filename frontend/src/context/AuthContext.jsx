import { createContext, useContext, useEffect, useState } from "react";
import {
    loginRequest,
    meRequest,
    logoutRequest,
    registerRequest,
} from "../api/auth.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [authLoading, setAuthLoading] = useState(true); // ✅ EKLENDİ

    useEffect(() => {
        const loadMe = async () => {
            try {
                const res = await fetch(
                    "http://localhost:8080/rest/api/movie_dive/users/auth/me",
                    { credentials: "include" }
                );

                if (!res.ok) throw new Error("not logged in");

                const me = await res.json();
                setUser(me);
            } catch {
                setUser(null);
            } finally {
                setAuthLoading(false); // ✅ artık tanımlı
            }
        };

        loadMe();
    }, []);

    // LOGIN
    const login = async (email, password) => {
        try {
            await loginRequest(email, password); // session set
            const me = await meRequest();        // user bilgisi
            setUser(me);
            return true;
        } catch (err) {
            console.log(
                "LOGIN ERROR:",
                err?.response?.status,
                err?.response?.data || err?.message
            );
            setUser(null);
            return false;
        }
    };

    // SIGNUP + LOGIN
    const signupAndLogin = async (dtoUser) => {
        try {
            await registerRequest(dtoUser);                       // 1) register
            await loginRequest(dtoUser.email, dtoUser.password);  // 2) login
            const me = await meRequest();                         // 3) me
            setUser(me);
            return true;
        } catch (err) {
            console.log(
                "SIGNUP+LOGIN ERROR:",
                err?.response?.status,
                err?.response?.data || err?.message
            );
            setUser(null);
            return false;
        }
    };

    // LOGOUT
    const logout = async () => {
        try {
            await logoutRequest(); // backend session invalidate
        } catch (err) {
            console.log("LOGOUT ERROR:", err?.message);
        }

        setUser(null);
        return true;
    };

    // ⛔ authLoading bitmeden app render olmasın
    if (authLoading) {
        return null; // veya spinner
    }

    return (
        <AuthContext.Provider
            value={{
                user,
                authLoading,
                login,
                signupAndLogin,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
