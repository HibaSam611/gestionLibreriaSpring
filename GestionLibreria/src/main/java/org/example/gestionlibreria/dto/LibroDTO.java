package org.example.gestionlibreria.dto;

import jakarta.validation.constraints.*;

// DTO = Data Transfer Object (objeto de transferencia de datos)
// Es lo que recibe el controlador cuando el usuario envia datos
// Es parecido al modelo Book pero SIN el campo id (el id lo pone MongoDB)
// Asi separamos lo que llega del cliente del modelo de la base de datos

public class LibroDTO {

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

    private boolean leido;

    // Constructor vacio (lo necesita Spring para parsear el JSON)
    public LibroDTO() {}

    // Constructor con todos los campos
    public LibroDTO(String titulo, String autor, String genero, int anio,
                    String isbn, double nota, boolean leido) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.anio = anio;
        this.isbn = isbn;
        this.nota = nota;
        this.leido = leido;
    }

    // Getters y Setters

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}
