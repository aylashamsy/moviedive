import "../styles/Pages.css";

function HomePage() {
    return (
        <main className="page">
            <section className="hero">
                <h1 className="hero-title">Discover movies & events in one place</h1>
                <p className="hero-sub">
                    MovieDive is a desktop-first UI. Use the search bar to explore, then open details.
                </p>

                <div className="hero-grid">
                    <div className="card">
                        <h3>Trending Movies</h3>
                        <p>Later you will fetch from Spring Boot / TMDB.</p>
                    </div>

                    <div className="card">
                        <h3>Upcoming Events</h3>
                        <p>Later you will fetch event details from backend.</p>
                    </div>

                    <div className="card">
                        <h3>My Lists</h3>
                        <p>After login you can save favorites & watchlist.</p>
                    </div>
                </div>
            </section>
        </main>
    );
}

export default HomePage;
