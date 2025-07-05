import { useState, useCallback } from 'react';
import './App.css';
import type { Movie } from './Movie';
import Header from './components/Header';
import MovieListPage from './pages/MovieListPage';
import AddMoviePage from './pages/AddMoviePage';
import EditMoviePage from './pages/EditMoviePage';
import { Routes, Route, useNavigate } from 'react-router-dom';

function App() {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const [editingMovie, setEditingMovie] = useState<Movie | null>(null); // State for editing movie

  // This fetchMovies will be passed down to MovieListPage to trigger data refresh
  const fetchMovies = useCallback(async (genreFilter?: string, yearFilter?: string) => {
    // This logic will be moved to MovieListPage
    // For now, it's a placeholder to satisfy dependencies
    console.log('Fetching movies with filters:', genreFilter, yearFilter);
  }, []);

  const handleEditMovie = (movie: Movie) => {
    setEditingMovie(movie);
    navigate(`/edit-movie/${movie.id}`);
  };

  const handleMovieUpdated = () => {
    setEditingMovie(null); // Clear editing state
    navigate('/'); // Navigate back to the movie list
  };

  const handleCancelEdit = () => {
    setEditingMovie(null); // Clear editing state
    navigate('/'); // Navigate back to the movie list
  };

  return (
    <div className="App">
      <Header />
      {error && <p className="error-message">Error: {error}</p>}
      <Routes>
        <Route path="/" element={<MovieListPage onEditMovie={handleEditMovie} />} />
        <Route path="/add-movie" element={<AddMoviePage />} />
        <Route
          path="/edit-movie/:id"
          element={
            <EditMoviePage
              initialMovie={editingMovie}
              onMovieUpdated={handleMovieUpdated}
              onCancelEdit={handleCancelEdit}
            />
          }
        />
      </Routes>
    </div>
  );
}

export default App;