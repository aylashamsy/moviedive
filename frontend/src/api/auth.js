import { api } from "./client";

const BASE = "/rest/api/movie_dive/users/auth";

/**
 * LOGIN
 * session oluşturur
 */
export async function loginRequest(email, password) {
    const res = await api.post(`${BASE}/login`, {
        email,
        password,
    });
    return res.data;
}

/**
 * ME
 * aktif session'daki user'ı döner
 */
export async function meRequest() {
    const res = await api.get(`${BASE}/me`);
    return res.data;
}

/**
 * REGISTER
 * yeni user oluşturur
 */
export async function registerRequest(dtoUser) {
    const res = await api.post(`${BASE}/register`, dtoUser);
    return res.data;
}

/**
 * LOGOUT
 * session invalidate eder
 */
export async function logoutRequest() {
    await api.post(`${BASE}/logout`);
    // response kullanmıyoruz, önemli olan session'ın bitmesi
}
