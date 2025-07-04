import { useEffect, useState, useCallback } from 'react'
import './App.css'
import { type Movie } from './Movie';
import MovieForm from './components/MovieForm';

function App() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchMovies = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      // Adjust this URL if your movie-service is running on a different port or host
      const response = await fetch('http://localhost:8080/api/movies');
      if (!response.ok) {
        // Attempt to read error message from backend
        const errorData = await response.json();
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }
      const data: Movie[] = await response.json();
      setMovies(data);
    } catch (error: any) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMovies();
  }, [fetchMovies]);

  if (loading) {
    return <div>Loading movies...</div>;
  }

  return (
    <div className="App">
      <h1>Showscape Movies</h1>
      <MovieForm onMovieAdded={fetchMovies} />
      {error && <p className="error-message">Error: {error}</p>}

      {movies.length === 0 ? (
        <p>No movies available. Add some from the backend!</p>
      ) : (
        <div className="movie-list">
          {movies.map((movie) => (
            <div key={movie.id} className="movie-card">
              <h2>{movie.title}</h2>
              <p><strong>Genre:</strong> {movie.genre}</p>
              <p><strong>Release Date:</strong> {movie.releaseDate}</p>
              <p>{movie.description}</p>
              <p><strong>Rating:</strong> {movie.rating}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default App;
