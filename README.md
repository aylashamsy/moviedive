
# 🎬 MovieDive

MovieDive is a full-stack movie discovery and social networking platform that allows users to explore movies, share their opinions, manage personal watchlists, connect with other movie enthusiasts, and participate in movie-related events.

The application combines movie data provided by The Movie Database (TMDB) with interactive social features to create a personalized movie discovery experience.

## 🚀 Features

### Movie Discovery

- Explore trending movies.
- Search for movies by title.
- View detailed movie information, including genres, cast, directors, release dates, and ratings.
- Discover movies through TMDB integration.

### Personalized Movie Lists

- Mark movies as watched.
- Add movies to a watch-later list.
- Like favorite movies.
- Manage personal movie collections.

### Ratings and Reviews

- Rate movies.
- Write and share movie reviews.
- Update or remove personal ratings and reviews.
- View feedback shared by other users.

### User Management

- User registration and authentication.
- Session-based authentication.
- Personal and public user profiles.
- Profile information management.
- Password management.
- Role-based authorization.

### Social Features

- Follow and unfollow other users.
- View followers and following lists.
- Explore other users' public profiles.
- Connect with other movie enthusiasts.

### Events and VIP Membership

- Explore upcoming movie-related events.
- Register for events or cancel registrations.
- VIP membership system.
- Exclusive event creation privileges for VIP users.

## 🛠️ Technology Stack

| Layer            | Technology                   |
| ---------------- | ---------------------------- |
| Frontend         | React, Vite, JavaScript      |
| Backend          | Java 21, Spring Boot         |
| Database         | PostgreSQL                   |
| ORM              | Spring Data JPA, Hibernate   |
| API Architecture | REST API                     |
| Authentication   | Session-based Authentication |
| External API     | TMDB API                     |
| Build Tool       | Maven                        |
| Version Control  | Git, GitHub                  |

## 🏗️ System Architecture

MovieDive follows a client-server architecture with separate frontend, backend, and database layers.

- **Frontend:** Provides the user interface and communicates with the backend through REST API requests.
- **Backend:** Handles business logic, authentication, authorization, data processing, and API endpoints.
- **Database:** Stores user information, movies, reviews, social relationships, and event registrations.
- **TMDB API:** Provides external movie information and metadata.

## 🗄️ Database Design

The application uses a relational database to manage its core entities:

| Entity            | Description                                           |
| ----------------- | ----------------------------------------------------- |
| User              | Stores user accounts and profile information.         |
| City              | Stores city information associated with users.        |
| Movie             | Stores movie information and metadata.                |
| UserMovieStatus   | Manages watched movies, watch-later lists, and likes. |
| UserMovieFeedback | Stores user ratings and movie feedback.               |
| Comment           | Stores user comments.                                 |
| Follow            | Manages relationships between users.                  |
| Event             | Stores movie-related events.                          |
| EventRegistration | Manages user participation in events.                 |

## 🔐 User Roles

The application supports two user roles:

| Feature                     | Regular User | VIP User |
| --------------------------- | ------------ | -------- |
| Browse and search movies    | ✅           | ✅       |
| Rate and review movies      | ✅           | ✅       |
| Manage personal movie lists | ✅           | ✅       |
| Follow other users          | ✅           | ✅       |
| Register for events         | ✅           | ✅       |
| Create events               | ❌           | ✅       |

## 📦 Getting Started

### Prerequisites

Make sure the following tools are installed:

- Java 21
- Maven
- Node.js and npm
- PostgreSQL
- Git
- TMDB API credentials

### Installation

1. Clone the repository.
2. Create a PostgreSQL database for the application.
3. Configure the database connection and required API credentials in the backend configuration.
4. Start the Spring Boot backend using Maven.
5. Navigate to the frontend directory.
6. Install the required dependencies using `npm install`.
7. Start the frontend development server using `npm run dev`.

The backend runs on port `8080`, while the frontend uses the Vite development server.

## 🎯 Project Objectives

- Develop a complete full-stack web application.
- Implement a layered backend architecture.
- Design and manage a relational database.
- Integrate external APIs into a web application.
- Implement authentication and role-based authorization.
- Develop interactive and user-friendly interfaces.
- Gain practical experience in collaborative software development using Git and GitHub.

## 🔮 Future Improvements

- Personalized movie recommendations.
- Advanced movie filtering.
- Real-time notifications.
- Social activity feed.
- Enhanced event management.
- Improved mobile responsiveness.
- Automated testing and deployment.

## 👥 Contributors

MovieDive was developed as a collaborative university software development project.

## 🙏 Acknowledgements

- [The Movie Database (TMDB)](https://www.themoviedb.org/) — Movie data and metadata.
- [Spring Boot](https://spring.io/projects/spring-boot) — Backend framework.
- [React](https://react.dev/) — Frontend library.
- [PostgreSQL](https://www.postgresql.org/) — Relational database management system.

---

**MovieDive — Discover movies, share experiences, and connect with movie lovers.**
