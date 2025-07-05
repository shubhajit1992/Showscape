package com.showscape.movieservice.repository;

import com.showscape.movieservice.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenre(String genre);

    List<Movie> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate);

    // Custom query to find by release year
    @Query("SELECT m FROM Movie m WHERE YEAR(m.releaseDate) = :year")
    List<Movie> findByReleaseYear(@Param("year") int year);

    @Query("SELECT DISTINCT m.genre FROM Movie m")
    List<String> findDistinctGenres();

    @Query("SELECT DISTINCT YEAR(m.releaseDate) FROM Movie m ORDER BY YEAR(m.releaseDate) ASC")
    List<Integer> findDistinctReleaseYears();
}
