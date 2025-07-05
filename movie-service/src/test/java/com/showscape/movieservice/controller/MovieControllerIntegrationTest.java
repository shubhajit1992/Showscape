package com.showscape.movieservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.showscape.movieservice.dto.MovieRequest;
import com.showscape.movieservice.dto.MovieResponse;
import com.showscape.movieservice.entity.Movie;
import com.showscape.movieservice.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MovieControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/movies";
    }

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void createMovie_shouldReturnCreatedMovie() {
        MovieRequest movieRequest = MovieRequest.builder()
                .title("Inception")
                .description("A dream within a dream.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .genre("Sci-Fi")
                .rating(8.8)
                .build();

        ResponseEntity<MovieResponse> response = restTemplate.postForEntity(getBaseUrl(), movieRequest, MovieResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Inception");
    }

    @Test
    void getMovieById_shouldReturnMovie_whenMovieExists() {
        Movie movie = movieRepository.save(Movie.builder().title("Interstellar").releaseDate(LocalDate.of(2014, 11, 7)).genre("Sci-Fi").rating(8.6).build());

        ResponseEntity<MovieResponse> response = restTemplate.getForEntity(getBaseUrl() + "/" + movie.getId(), MovieResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(movie.getId());
        assertThat(response.getBody().title()).isEqualTo("Interstellar");
    }

    @Test
    void getMovieById_shouldReturnNotFound_whenMovieDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllMovies_shouldReturnListOfMovies() {
        movieRepository.save(Movie.builder().title("Movie 1").releaseDate(LocalDate.of(2020,1,1)).genre("Action").rating(7.0).build());
        movieRepository.save(Movie.builder().title("Movie 2").releaseDate(LocalDate.of(2021,1,1)).genre("Comedy").rating(8.0).build());

        ResponseEntity<MovieResponse[]> response = restTemplate.getForEntity(getBaseUrl(), MovieResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()[0].title()).isEqualTo("Movie 1");
    }

    @Test
    void updateMovie_shouldReturnUpdatedMovie() {
        Movie movie = movieRepository.save(Movie.builder().title("Old Title").releaseDate(LocalDate.of(2020,1,1)).genre("Action").rating(7.0).build());

        MovieRequest updatedRequest = MovieRequest.builder()
                .title("New Title")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genre("Action")
                .rating(7.5)
                .build();

        ResponseEntity<MovieResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + movie.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updatedRequest),
                MovieResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("New Title");
    }

    @Test
    void updateMovie_shouldReturnNotFound_whenMovieDoesNotExist() {
        MovieRequest updatedRequest = MovieRequest.builder()
                .title("New Title")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genre("Action")
                .rating(7.5)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/999",
                HttpMethod.PUT,
                new HttpEntity<>(updatedRequest),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteMovie_shouldReturnNoContent_whenMovieExists() {
        Movie movie = movieRepository.save(Movie.builder().title("To Be Deleted").releaseDate(LocalDate.of(2020,1,1)).genre("Action").rating(7.0).build());

        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + movie.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(movieRepository.findById(movie.getId())).isEmpty();
    }

    @Test
    void deleteMovie_shouldReturnNotFound_whenMovieDoesNotExist() {
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/999",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getMoviesByGenre_shouldReturnListOfMovies() {
        movieRepository.save(Movie.builder().title("Action Movie").genre("Action").releaseDate(LocalDate.of(2020,1,1)).rating(7.0).build());
        movieRepository.save(Movie.builder().title("Comedy Movie").genre("Comedy").releaseDate(LocalDate.of(2020,1,1)).rating(7.0).build());

        ResponseEntity<MovieResponse[]> response = restTemplate.getForEntity(getBaseUrl() + "/genre/Action", MovieResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].genre()).isEqualTo("Action");
    }

    @Test
    void getMoviesByReleaseYear_shouldReturnListOfMovies() {
        movieRepository.save(Movie.builder().title("Movie 2020").releaseDate(LocalDate.of(2020, 3, 10)).genre("Drama").rating(6.5).build());
        movieRepository.save(Movie.builder().title("Movie 2021").releaseDate(LocalDate.of(2021, 7, 20)).genre("Drama").rating(6.5).build());

        ResponseEntity<MovieResponse[]> response = restTemplate.getForEntity(getBaseUrl() + "/year/2020", MovieResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].title()).isEqualTo("Movie 2020");
    }

    @Test
    void getDistinctGenres_shouldReturnDistinctGenres() {
        movieRepository.save(Movie.builder().title("Movie 1").genre("Action").releaseDate(LocalDate.of(2020,1,1)).rating(7.0).build());
        movieRepository.save(Movie.builder().title("Movie 2").genre("Comedy").releaseDate(LocalDate.of(2021,1,1)).rating(8.0).build());

        ResponseEntity<String[]> response = restTemplate.getForEntity(getBaseUrl() + "/genres", String[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsExactlyInAnyOrder("Action", "Comedy");
    }

    @Test
    void getDistinctYears_shouldReturnDistinctYears() {
        movieRepository.save(Movie.builder().title("Movie 1").genre("Action").releaseDate(LocalDate.of(2020,1,1)).rating(7.0).build());
        movieRepository.save(Movie.builder().title("Movie 2").genre("Comedy").releaseDate(LocalDate.of(2021,1,1)).rating(8.0).build());

        ResponseEntity<Integer[]> response = restTemplate.getForEntity(getBaseUrl() + "/years", Integer[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsExactlyInAnyOrder(2020, 2021);
    }
}
