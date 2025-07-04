package com.showscape.movieservice.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MovieResponse(
    Long id,
    String title,
    String description,
    LocalDate releaseDate,
    String genre,
    Double rating
) {}
