package org.example.gestionlibreria.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String id) {
        super("No se encontró ningún libro con ID: " + id);
    }
}
