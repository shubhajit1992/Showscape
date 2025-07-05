import { useEffect, useState, useCallback } from 'react';
import type { Movie } from '../Movie';
import useDebounce from '../hooks/useDebounce';

interface MovieListPageProps {
  onEditMovie: (movie: Movie) => void;
}

const MovieListPage: React.FC<MovieListPageProps> = ({ onEditMovie }) => {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [genreFilter, setGenreFilter] = useState<string>('');
  const [yearFilter, setYearFilter] = useState<string>('');
  const [availableGenres, setAvailableGenres] = useState<string[]>([]);
  const [availableYears, setAvailableYears] = useState<number[]>([]);

  // Debounced filter values
  const debouncedGenreFilter = useDebounce(genreFilter, 500); // 500ms debounce
  const debouncedYearFilter = useDebounce(yearFilter, 500); // 500ms debounce

  const fetchMovies = useCallback(async () => {
    setLoading(true);
    setError(null);
    let url = 'http://localhost:8080/api/movies';

    // Backend currently supports only one filter at a time. Prioritizing genre.
    if (debouncedGenreFilter) {
      url = `http://localhost:8080/api/movies/genre/${debouncedGenreFilter}`;
    } else if (debouncedYearFilter) {
      url = `http://localhost:8080/api/movies/year/${debouncedYearFilter}`;
    }

    try {
      const response = await fetch(url);
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
  }, [debouncedGenreFilter, debouncedYearFilter]); // Depend on debounced filters

  const fetchFilterOptions = useCallback(async () => {
    try {
      const genresResponse = await fetch('http://localhost:8080/api/movies/genres');
      const genresData: string[] = await genresResponse.json();
      setAvailableGenres(genresData);

      const yearsResponse = await fetch('http://localhost:8080/api/movies/years');
      const yearsData: number[] = await yearsResponse.json();
      setAvailableYears(yearsData);
    } catch (err: any) {
      console.error('Failed to fetch filter options:', err);
      // Optionally set an error state for filter options
    }
  }, []);

  useEffect(() => {
    fetchMovies();
    fetchFilterOptions();
  }, [fetchMovies, fetchFilterOptions]);

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

  const handleClearFilters = () => {
    setGenreFilter('');
    setYearFilter('');
    // fetchMovies will be called by useEffect due to debounced filter state change
  };

  if (loading) {
    return <div>Loading movies...</div>;
  }

  return (
    <div className="movie-list-page">
      {error && <p className="error-message">Error: {error}</p>}

      <div className="filter-container">
        <select
          value={genreFilter}
          onChange={(e) => setGenreFilter(e.target.value)}
        >
          <option value="">All Genres</option>
          {availableGenres.map((genre) => (
            <option key={genre} value={genre}>{genre}</option>
          ))}
        </select>

        <select
          value={yearFilter}
          onChange={(e) => setYearFilter(e.target.value)}
        >
          <option value="">All Years</option>
          {availableYears.map((year) => (
            <option key={year} value={year}>{year}</option>
          ))}
        </select>

        <button onClick={handleClearFilters} className="clear-filter-button">Clear Filters</button>
      </div>

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
              <button onClick={() => onEditMovie(movie)} className="edit-button">Edit</button>
              <button onClick={() => handleDeleteClick(movie.id)} className="delete-button">Delete</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MovieListPage;