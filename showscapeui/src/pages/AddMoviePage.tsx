import React from 'react';
import MovieForm from '../components/MovieForm';
import { useNavigate } from 'react-router-dom';

const AddMoviePage: React.FC = () => {
  const navigate = useNavigate();

  const handleMovieAdded = () => {
    navigate('/'); // Navigate back to the movie list after adding
  };

  return (
    <div className="add-movie-page">
      <MovieForm onMovieAdded={handleMovieAdded} />
    </div>
  );
};

export default AddMoviePage;
