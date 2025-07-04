package com.showscape.movieservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.showscape.movieservice.dto.MovieRequest;
import com.showscape.movieservice.dto.MovieResponse;
import com.showscape.movieservice.exception.MovieNotFoundException;
import com.showscape.movieservice.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieRequest movieRequest;
    private MovieResponse movieResponse;

    @BeforeEach
    void setUp() {
        movieRequest = MovieRequest.builder()
                .title("Inception")
                .description("A dream within a dream.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .genre("Sci-Fi")
                .rating(8.8)
                .build();

        movieResponse = MovieResponse.builder()
                .id(1L)
                .title("Inception")
                .description("A dream within a dream.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .genre("Sci-Fi")
                .rating(8.8)
                .build();
    }

    @Test
    void createMovie_shouldReturnCreatedMovie() throws Exception {
        when(movieService.createMovie(any(MovieRequest.class))).thenReturn(movieResponse);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void createMovie_shouldReturnBadRequest_whenValidationFails() throws Exception {
        movieRequest = MovieRequest.builder()
                .title("") // Empty/Invalid title
                .description("A dream within a dream.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .genre("Sci-Fi")
                .rating(8.8)
                .build();

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMovieById_shouldReturnMovie_whenMovieExists() throws Exception {
        when(movieService.getMovieById(1L)).thenReturn(movieResponse);

        mockMvc.perform(get("/api/movies/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getMovieById_shouldReturnNotFound_whenMovieDoesNotExist() throws Exception {
        when(movieService.getMovieById(anyLong())).thenThrow(new MovieNotFoundException("Not found"));

        mockMvc.perform(get("/api/movies/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllMovies_shouldReturnListOfMovies() throws Exception {
        List<MovieResponse> movies = Arrays.asList(movieResponse, movieResponse);
        when(movieService.getAllMovies()).thenReturn(movies);

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    void updateMovie_shouldReturnUpdatedMovie() throws Exception {
        when(movieService.updateMovie(eq(1L), any(MovieRequest.class))).thenReturn(movieResponse);

        mockMvc.perform(put("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void updateMovie_shouldReturnNotFound_whenMovieDoesNotExist() throws Exception {
        doThrow(new MovieNotFoundException("Not found")).when(movieService).updateMovie(anyLong(), any(MovieRequest.class));

        mockMvc.perform(put("/api/movies/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovie_shouldReturnNoContent_whenMovieExists() throws Exception {
        doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/movies/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMovie_shouldReturnNotFound_whenMovieDoesNotExist() throws Exception {
        doThrow(new MovieNotFoundException("Not found")).when(movieService).deleteMovie(anyLong());

        mockMvc.perform(delete("/api/movies/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMoviesByGenre_shouldReturnListOfMovies() throws Exception {
        List<MovieResponse> movies = List.of(movieResponse);
        when(movieService.getMoviesByGenre("Sci-Fi")).thenReturn(movies);

        mockMvc.perform(get("/api/movies/genre/{genre}", "Sci-Fi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].genre").value("Sci-Fi"));
    }

    @Test
    void getMoviesByReleaseYear_shouldReturnListOfMovies() throws Exception {
        List<MovieResponse> movies = List.of(movieResponse);
        when(movieService.getMoviesByReleaseYear(2010)).thenReturn(movies);

        mockMvc.perform(get("/api/movies/year/{year}", 2010))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].releaseDate").value("2010-07-16"));
    }
}
