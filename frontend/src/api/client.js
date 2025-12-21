import axios from "axios";

export const api = axios.create({
    baseURL: "http://localhost:8080",            // proxy ile aynı origin
    withCredentials: true,  // session cookie önemli
});
