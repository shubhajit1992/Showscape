import React, { useState, useEffect } from 'react';
import { type MovieRequest } from '../dto/MovieRequest';
import { type Movie } from '../Movie';

interface MovieFormProps {
  initialMovie?: Movie; // Optional prop for editing
  onMovieAdded: () => void; // Callback for adding a new movie
  onMovieUpdated?: () => void; // Callback for updating an existing movie
  onCancelEdit?: () => void; // Callback to cancel editing
}

const MovieForm: React.FC<MovieFormProps> = ({ initialMovie, onMovieAdded, onMovieUpdated, onCancelEdit }) => {
  const [formData, setFormData] = useState<MovieRequest>({
    title: '',
    description: '',
    releaseDate: '',
    genre: '',
    rating: 0,
  });
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    if (initialMovie) {
      setFormData({
        title: initialMovie.title,
        description: initialMovie.description,
        releaseDate: initialMovie.releaseDate, // Assuming YYYY-MM-DD string
        genre: initialMovie.genre,
        rating: initialMovie.rating,
      });
    } else {
      setFormData({
        title: '',
        description: '',
        releaseDate: '',
        genre: '',
        rating: 0,
      });
    }
  }, [initialMovie]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: name === 'rating' ? parseFloat(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage(null);

    const isEditing = initialMovie && initialMovie.id;
    const url = isEditing ? `http://localhost:8080/api/movies/${initialMovie.id}` : 'http://localhost:8080/api/movies';
    const method = isEditing ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        if (response.status === 400 && errorData.details) {
          const validationErrors = errorData.details.map((detail: any) => `${detail.field}: ${detail.message}`).join('; ');
          throw new Error(`Validation failed: ${validationErrors}`);
        } else {
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
      }

      if (isEditing) {
        setSuccessMessage('Movie updated successfully!');
        if (onMovieUpdated) onMovieUpdated();
      } else {
        setSuccessMessage('Movie added successfully!');
        setFormData({
          title: '',
          description: '',
          releaseDate: '',
          genre: '',
          rating: 0,
        });
        onMovieAdded();
      }
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div className="movie-form-container">
      <h2>{initialMovie ? 'Edit Movie' : 'Add New Movie'}</h2>
      <form onSubmit={handleSubmit} className="movie-form">
        {error && <p className="error-message">Error: {error}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}

        <div className="form-group">
          <label htmlFor="title">Title:</label>
          <input
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description:</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
          ></textarea>
        </div>

        <div className="form-group">
          <label htmlFor="releaseDate">Release Date:</label>
          <input
            type="date"
            id="releaseDate"
            name="releaseDate"
            value={formData.releaseDate}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="genre">Genre:</label>
          <input
            type="text"
            id="genre"
            name="genre"
            value={formData.genre}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="rating">Rating:</label>
          <input
            type="number"
            id="rating"
            name="rating"
            value={formData.rating}
            onChange={handleChange}
            step="0.1"
            min="0"
            max="10"
            required
          />
        </div>

        <button type="submit">{initialMovie ? 'Update Movie' : 'Add Movie'}</button>
        {initialMovie && onCancelEdit && (
          <button type="button" onClick={onCancelEdit} className="cancel-button">
            Cancel
          </button>
        )}
      </form>
    </div>
  );
};

export default MovieForm;