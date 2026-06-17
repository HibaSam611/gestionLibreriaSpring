package org.example.gestionlibreria.repository;

import org.example.gestionlibreria.model.Libro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends MongoRepository<Libro, String> {

    // Buscar libros que contengan un texto en el titulo
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    // Buscar libros por autor
    List<Libro> findByAutorContainingIgnoreCase(String autor);

    // Buscar libros por genero
    List<Libro> findByGeneroIgnoreCase(String genero);
}