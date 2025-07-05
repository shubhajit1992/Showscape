package com.showscape.movieservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.showscape.movieservice.dto.MovieRequest;
import com.showscape.movieservice.dto.MovieResponse;
import com.showscape.movieservice.entity.Movie;
import com.showscape.movieservice.exception.MovieNotFoundException;
import com.showscape.movieservice.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private MovieRequest movieRequest;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .description("A dream within a dream.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .genre("Sci-Fi")
                .rating(8.8)
                .build();

        movieRequest = MovieRequest.builder()
                .title("Inception")
                .description("A dream within a dream.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .genre("Sci-Fi")
                .rating(8.8)
                .build();
    }

    @Test
    void createMovie_shouldReturnCreatedMovieResponse() {
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.createMovie(movieRequest);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("Inception");
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void getMovieById_shouldReturnMovieResponse_whenMovieExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        MovieResponse response = movieService.getMovieById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        verify(movieRepository).findById(1L);
    }

    @Test
    void getMovieById_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(1L));
        verify(movieRepository).findById(1L);
    }

    @Test
    void getAllMovies_shouldReturnListOfMovieResponses() {
        when(movieRepository.findAll()).thenReturn(Arrays.asList(movie, movie));

        List<MovieResponse> responses = movieService.getAllMovies();

        assertThat(responses).hasSize(2);
        verify(movieRepository).findAll();
    }

    @Test
    void updateMovie_shouldReturnUpdatedMovieResponse_whenMovieExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.updateMovie(1L, movieRequest);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("Inception");
        verify(movieRepository).findById(1L);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updateMovie_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(1L, movieRequest));
        verify(movieRepository).findById(1L);
        verify(movieRepository, times(0)).save(any(Movie.class));
    }

    @Test
    void deleteMovie_shouldDeleteMovie_whenMovieExists() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(1L);

        movieService.deleteMovie(1L);

        verify(movieRepository).existsById(1L);
        verify(movieRepository).deleteById(1L);
    }

    @Test
    void deleteMovie_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        when(movieRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(1L));
        verify(movieRepository).existsById(1L);
        verify(movieRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void getMoviesByGenre_shouldReturnListOfMovieResponses() {
        when(movieRepository.findByGenre("Sci-Fi")).thenReturn(List.of(movie));

        List<MovieResponse> responses = movieService.getMoviesByGenre("Sci-Fi");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).genre()).isEqualTo("Sci-Fi");
        verify(movieRepository).findByGenre("Sci-Fi");
    }

    @Test
    void getMoviesByReleaseYear_shouldReturnListOfMovieResponses() {
        when(movieRepository.findByReleaseYear(2010)).thenReturn(List.of(movie));

        List<MovieResponse> responses = movieService.getMoviesByReleaseYear(2010);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).releaseDate().getYear()).isEqualTo(2010);
        verify(movieRepository).findByReleaseYear(2010);
    }

    @Test
    void getDistinctGenres_shouldReturnDistinctGenres() {
        List<String> genres = Arrays.asList("Action", "Comedy", "Drama");
        when(movieRepository.findDistinctGenres()).thenReturn(genres);

        List<String> result = movieService.getDistinctGenres();

        assertThat(result).isEqualTo(genres);
        verify(movieRepository).findDistinctGenres();
    }

    @Test
    void getDistinctYears_shouldReturnDistinctYears() {
        List<Integer> years = Arrays.asList(2020, 2021, 2022);
        when(movieRepository.findDistinctReleaseYears()).thenReturn(years);

        List<Integer> result = movieService.getDistinctYears();

        assertThat(result).isEqualTo(years);
        verify(movieRepository).findDistinctReleaseYears();
    }
}
