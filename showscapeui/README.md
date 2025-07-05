# Showscape UI (Frontend)

This is the React frontend application for the Showscape project, providing a user interface to interact with the backend microservices.

## Technology Stack

*   **Framework:** React 18
*   **Build Tool:** Vite
*   **Language:** TypeScript
*   **Routing:** React Router DOM
*   **Styling:** Pure CSS

## Implemented Features

*   **Movie Listing:** Displays all movies fetched from the `movie-service` backend.
*   **Add New Movie:** A form to create new movie entries, with client-side validation and backend error handling.
*   **Edit Movie:** Functionality to edit existing movie details, pre-populating the form with current data.
*   **Delete Movie:** Ability to delete movies with a confirmation step.
*   **Search/Filter:** Filters movies by genre and release year using dynamically populated dropdowns and debounced inputs to optimize API calls.
*   **Client-Side Routing:** Uses React Router for navigation between different views (Home, Add Movie, Edit Movie).
*   **Improved UI/UX:** Enhanced form layout, consistent container widths, and basic styling for a better user experience.

## Getting Started

To run the Showscape UI locally:

### Prerequisites

*   Node.js (LTS version recommended)
*   npm (Node Package Manager)
*   Ensure the `movie-service` backend is running (refer to `movie-service/README.md`).

### Running the Application

1.  Navigate to the `showscapeui` directory:
    ```bash
    cd showscapeui
    ```
2.  Install dependencies (if you haven't already):
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```

The application will typically be available at `http://localhost:5173` (check your terminal for the exact URL).

## Project Structure

*   `src/App.tsx`: Main application component, handles routing and overall layout.
*   `src/Movie.ts`: TypeScript interface for the Movie data structure.
*   `src/components/`: Reusable UI components (e.g., `MovieForm.tsx`, `Header.tsx`).
*   `src/dto/`: TypeScript interfaces for Data Transfer Objects (e.g., `MovieRequest.ts`).
*   `src/hooks/`: Custom React hooks (e.g., `useDebounce.ts`).
*   `src/pages/`: Page-level components for different routes (e.g., `MovieListPage.tsx`, `AddMoviePage.tsx`, `EditMoviePage.tsx`).
*   `src/index.css`: Global styles for the application.