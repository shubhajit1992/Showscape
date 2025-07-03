package com.showscape.movieservice.repository;

import com.showscape.movieservice.entity.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void shouldSaveMovie() {
        Movie movie = Movie.builder()
                .title("Test Movie")
                .description("A test description.")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .genre("Action")
                .rating(7.5)
                .build();

        Movie savedMovie = movieRepository.save(movie);

        assertThat(savedMovie).isNotNull();
        assertThat(savedMovie.getId()).isNotNull();
        assertThat(savedMovie.getTitle()).isEqualTo("Test Movie");
    }

    @Test
    void shouldFindMovieById() {
        Movie movie = Movie.builder()
                .title("Find Me")
                .description("Description.")
                .releaseDate(LocalDate.of(2022, 5, 10))
                .genre("Comedy")
                .rating(8.0)
                .build();
        movieRepository.save(movie);

        Optional<Movie> foundMovie = movieRepository.findById(movie.getId());

        assertThat(foundMovie).isPresent();
        assertThat(foundMovie.get().getTitle()).isEqualTo("Find Me");
    }

    @Test
    void shouldFindAllMovies() {
        movieRepository.save(Movie.builder().title("Movie 1").build());
        movieRepository.save(Movie.builder().title("Movie 2").build());

        List<Movie> movies = movieRepository.findAll();

        assertThat(movies).hasSize(2);
    }

    @Test
    void shouldUpdateMovie() {
        Movie movie = Movie.builder().title("Old Title").build();
        movieRepository.save(movie);

        movie.setTitle("New Title");
        Movie updatedMovie = movieRepository.save(movie);

        assertThat(updatedMovie.getTitle()).isEqualTo("New Title");
    }

    @Test
    void shouldDeleteMovie() {
        Movie movie = Movie.builder().title("Delete Me").build();
        movieRepository.save(movie);

        movieRepository.deleteById(movie.getId());
        Optional<Movie> deletedMovie = movieRepository.findById(movie.getId());

        assertThat(deletedMovie).isNotPresent();
    }

    @Test
    void shouldFindByGenre() {
        movieRepository.save(Movie.builder().title("Action Movie 1").genre("Action").build());
        movieRepository.save(Movie.builder().title("Comedy Movie 1").genre("Comedy").build());
        movieRepository.save(Movie.builder().title("Action Movie 2").genre("Action").build());

        List<Movie> actionMovies = movieRepository.findByGenre("Action");

        assertThat(actionMovies).hasSize(2);
        assertThat(actionMovies.get(0).getTitle()).isEqualTo("Action Movie 1");
    }

    @Test
    void shouldFindByReleaseDateBetween() {
        movieRepository.save(Movie.builder().title("Movie A").releaseDate(LocalDate.of(2020, 1, 1)).build());
        movieRepository.save(Movie.builder().title("Movie B").releaseDate(LocalDate.of(2021, 6, 15)).build());
        movieRepository.save(Movie.builder().title("Movie C").releaseDate(LocalDate.of(2022, 12, 31)).build());

        List<Movie> moviesIn2021 = movieRepository.findByReleaseDateBetween(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31));

        assertThat(moviesIn2021).hasSize(1);
        assertThat(moviesIn2021.get(0).getTitle()).isEqualTo("Movie B");
    }

    @Test
    void shouldFindByReleaseYear() {
        movieRepository.save(Movie.builder().title("Movie 2020").releaseDate(LocalDate.of(2020, 3, 10)).build());
        movieRepository.save(Movie.builder().title("Movie 2021").releaseDate(LocalDate.of(2021, 7, 20)).build());
        movieRepository.save(Movie.builder().title("Another Movie 2020").releaseDate(LocalDate.of(2020, 11, 5)).build());

        List<Movie> moviesFrom2020 = movieRepository.findByReleaseYear(2020);

        assertThat(moviesFrom2020).hasSize(2);
        assertThat(moviesFrom2020.get(0).getTitle()).isEqualTo("Movie 2020");
    }
}
