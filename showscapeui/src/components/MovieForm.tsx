import React, { useState } from 'react';
import { type MovieRequest } from '../dto/MovieRequest';

interface MovieFormProps {
  onMovieAdded: () => void;
}

const MovieForm: React.FC<MovieFormProps> = ({ onMovieAdded }) => {
  const [formData, setFormData] = useState<MovieRequest>({
    title: '',
    description: '',
    releaseDate: '',
    genre: '',
    rating: 0,
  });
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

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

    try {
      const response = await fetch('http://localhost:8080/api/movies', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        // Assuming ApiErrorResponse structure for validation errors
        if (response.status === 400 && errorData.details) {
          const validationErrors = errorData.details.map((detail: any) => `${detail.field}: ${detail.message}`).join('; ');
          throw new Error(`Validation failed: ${validationErrors}`);
        } else {
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
      }

      setSuccessMessage('Movie added successfully!');
      setFormData({
        title: '',
        description: '',
        releaseDate: '',
        genre: '',
        rating: 0,
      });
      onMovieAdded(); // Notify parent to refresh movie list
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div className="movie-form-container">
      <h2>Add New Movie</h2>
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

        <button type="submit">Add Movie</button>
      </form>
    </div>
  );
};

export default MovieForm;
