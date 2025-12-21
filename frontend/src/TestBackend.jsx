import { useEffect, useState } from "react";
import { api } from "./api/client";

export default function TestBackend() {
    const [msg, setMsg] = useState("loading...");

    useEffect(() => {
        api.get("/health")
            .then(r => setMsg(r.data))
            .catch(e => setMsg("ERROR: " + (e.response?.data?.message || e.message)));
    }, []);

    return <div style={{ padding: 20 }}>Backend says: {msg}</div>;
}
