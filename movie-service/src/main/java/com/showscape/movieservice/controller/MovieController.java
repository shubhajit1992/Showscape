package com.showscape.movieservice.controller;

import com.showscape.movieservice.dto.MovieRequest;
import com.showscape.movieservice.dto.MovieResponse;
import com.showscape.movieservice.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest movieRequest) {
        MovieResponse createdMovie = movieService.createMovie(movieRequest);
        return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest movieRequest) {
        MovieResponse updatedMovie = movieService.updateMovie(id, movieRequest);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieResponse>> getMoviesByGenre(@PathVariable String genre) {
        List<MovieResponse> movies = movieService.getMoviesByGenre(genre);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<MovieResponse>> getMoviesByReleaseYear(@PathVariable int year) {
        List<MovieResponse> movies = movieService.getMoviesByReleaseYear(year);
        return ResponseEntity.ok(movies);
    }
}
