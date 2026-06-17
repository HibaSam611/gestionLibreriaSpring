package org.example.gestionlibreria.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "books")
@Data                   // Lombok genera getters, setters, toString, equals y hashCode
@NoArgsConstructor      // Constructor vacio (lo necesita Spring)
@AllArgsConstructor     // Constructor con todos los campos
public class Libro {

    @Id // Este es el identificador unico, lo genera MongoDB solo
    private String id;

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @NotBlank(message = "El genero es obligatorio")
    private String genero;

    @Min(value = 1400, message = "El año debe ser mayor a 1400")
    private int anio;

    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @Min(value = 0, message = "La nota minima es 0")
    @Max(value = 10, message = "La nota maxima es 10")
    private double nota;

    // true si ya lo has leido, false si no
    private boolean leido;
}
