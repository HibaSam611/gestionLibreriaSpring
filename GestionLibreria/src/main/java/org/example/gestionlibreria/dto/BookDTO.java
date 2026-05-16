package org.example.gestionlibreria.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    private String id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String title;

    @NotBlank(message = "El autor no puede estar vacío")
    @Size(max = 120, message = "El autor no puede superar los 120 caracteres")
    private String author;

    @NotBlank(message = "El género no puede estar vacío")
    private String genre;

    @Min(value = 1450, message = "El año debe ser posterior a 1450")
    @Max(value = 2026, message = "El año no puede ser futuro")
    private int year;

    @NotBlank(message = "El ISBN no puede estar vacío")
    @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "El ISBN debe tener 10 o 13 dígitos")
    private String isbn;

    @Min(value = 0, message = "La puntuación mínima es 0")
    @Max(value = 10, message = "La puntuación máxima es 10")
    private double rating;

    private boolean read;
}
