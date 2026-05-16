package org.example.gestionlibreria.repository;

import org.example.gestionlibreria.model.Libro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Este es el repositorio (la capa DAO)
// Al extender MongoRepository ya tenemos gratis: save(), findById(), findAll(), deleteById()
// Solo añadimos busquedas personalizadas que necesitemos

@Repository
public interface LibroRepository extends MongoRepository<Libro, String> {

    // Buscar libros que contengan un texto en el titulo (sin importar mayusculas)
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    // Buscar libros por autor
    List<Libro> findByAutorContainingIgnoreCase(String autor);

    // Buscar libros por genero
    List<Libro> findByGeneroIgnoreCase(String genero);
}