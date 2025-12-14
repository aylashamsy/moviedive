import { useParams } from "react-router-dom";
import "../styles/Pages.css";

function EventDetail() {
    const { id } = useParams();

    return (
        <main className="page">
            <div className="panel">
                <h2>Event Detail</h2>
                <p>Event ID: <b>{id}</b></p>
                <div className="card">
                    <h3>Placeholder</h3>
                    <p>Later you will call: <code>GET http://localhost:8080/api/events/{id}</code></p>
                </div>
            </div>
        </main>
    );
}

export default EventDetail;
