package com.showscape.movieservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    @Test
    void testMovieCreation() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .description("A dream within a dream.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .genre("Sci-Fi")
                .rating(8.8)
                .build();

        assertNotNull(movie);
        assertEquals(1L, movie.getId());
        assertEquals("Inception", movie.getTitle());
        assertEquals("A dream within a dream.", movie.getDescription());
        assertEquals(LocalDate.of(2010, 7, 16), movie.getReleaseDate());
        assertEquals("Sci-Fi", movie.getGenre());
        assertEquals(8.8, movie.getRating());
    }

    @Test
    void testNoArgsConstructor() {
        Movie movie = new Movie();
        assertNotNull(movie);
    }

    @Test
    void testAllArgsConstructor() {
        Movie movie = new Movie(2L, "Interstellar", "Space travel.", LocalDate.of(2014, 11, 7), "Sci-Fi", 8.6);
        assertNotNull(movie);
        assertEquals(2L, movie.getId());
        assertEquals("Interstellar", movie.getTitle());
    }

    @Test
    void testSetters() {
        Movie movie = new Movie();
        movie.setTitle("The Matrix");
        assertEquals("The Matrix", movie.getTitle());
    }
}
