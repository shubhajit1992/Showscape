import React, { useEffect, useState } from 'react';
import MovieForm from '../components/MovieForm';
import { useNavigate, useParams } from 'react-router-dom';
import type { Movie } from '../Movie';

const EditMoviePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [movieToEdit, setMovieToEdit] = useState<Movie | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchMovie = async () => {
      if (!id) return;
      try {
        const response = await fetch(`http://localhost:8080/api/movies/${id}`);
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        const data: Movie = await response.json();
        setMovieToEdit(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchMovie();
  }, [id]);

  const handleMovieUpdated = () => {
    navigate('/'); // Navigate back to the movie list after updating
  };

  const handleCancelEdit = () => {
    navigate('/'); // Navigate back to the movie list without updating
  };

  if (loading) {
    return <div>Loading movie details...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!movieToEdit) {
    return <div>Movie not found.</div>;
  }

  return (
    <div className="edit-movie-page">
      <MovieForm
        initialMovie={movieToEdit}
        onMovieAdded={() => {}} // Not used in edit mode
        onMovieUpdated={handleMovieUpdated}
        onCancelEdit={handleCancelEdit}
      />
    </div>
  );
};

export default EditMoviePage;
