package org.example.gestionlibreria.repository;

import com.sergio.bookvault.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByGenreIgnoreCase(String genre);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByReadTrue();

    List<Book> findByReadFalse();
}
