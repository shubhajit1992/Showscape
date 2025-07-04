package com.showscape.movieservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MovieRequest(
    @NotBlank(message = "Title is mandatory")
    String title,
    String description,
    @NotNull(message = "Release date is mandatory")
    @PastOrPresent(message = "Release date cannot be in the future")
    LocalDate releaseDate,
    @NotBlank(message = "Genre is mandatory")
    String genre,
    @NotNull(message = "Rating is mandatory")
    @PositiveOrZero(message = "Rating must be positive or zero")
    Double rating
) {}
