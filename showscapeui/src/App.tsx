import { useEffect, useState, useCallback } from 'react'
import './App.css'
import type { Movie } from './Movie';
import MovieForm from './components/MovieForm';

function App() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [editingMovie, setEditingMovie] = useState<Movie | null>(null); // State for editing movie

  const fetchMovies = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch('http://localhost:8080/api/movies');
      if (!response.ok) {
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

  const handleEditClick = (movie: Movie) => {
    setEditingMovie(movie);
  };

  const handleMovieUpdated = () => {
    setEditingMovie(null); // Clear editing state
    fetchMovies(); // Refresh movie list
  };

  const handleCancelEdit = () => {
    setEditingMovie(null); // Clear editing state without refreshing
  };

  const handleDeleteClick = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this movie?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/movies/${id}`, {
          method: 'DELETE',
        });

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        fetchMovies(); // Refresh the movie list after deletion
      } catch (err: any) {
        setError(err.message);
      }
    }
  };

  if (loading) {
    return <div>Loading movies...</div>;
  }

  return (
    <div className="App">
      <h1>Showscape Movies</h1>
      <MovieForm
        initialMovie={editingMovie ?? undefined}
        onMovieAdded={fetchMovies}
        onMovieUpdated={handleMovieUpdated}
        onCancelEdit={handleCancelEdit}
      />
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
              <button onClick={() => handleEditClick(movie)} className="edit-button">Edit</button>
              <button onClick={() => handleDeleteClick(movie.id)} className="delete-button">Delete</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default App;
