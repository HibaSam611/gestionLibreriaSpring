package org.example.gestionlibreria.service;

import com.sergio.bookvault.dto.BookDTO;
import com.sergio.bookvault.exception.BookNotFoundException;
import com.sergio.bookvault.exception.DuplicateIsbnException;
import com.sergio.bookvault.model.Book;
import com.sergio.bookvault.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    /* ── CRUD ──────────────────────────────────────────── */

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(BookDTO dto) {
        // Comprobar ISBN duplicado
        repository.findByIsbn(dto.getIsbn()).ifPresent(existing -> {
            throw new DuplicateIsbnException(dto.getIsbn());
        });
        Book book = toEntity(dto);
        return repository.save(book);
    }

    public Book update(String id, BookDTO dto) {
        Book existing = findById(id); // lanza 404 si no existe

        // Si cambia el ISBN, comprobar que no esté duplicado
        if (!existing.getIsbn().equals(dto.getIsbn())) {
            repository.findByIsbn(dto.getIsbn()).ifPresent(other -> {
                throw new DuplicateIsbnException(dto.getIsbn());
            });
        }

        existing.setTitle(dto.getTitle());
        existing.setAuthor(dto.getAuthor());
        existing.setGenre(dto.getGenre());
        existing.setYear(dto.getYear());
        existing.setIsbn(dto.getIsbn());
        existing.setRating(dto.getRating());
        existing.setRead(dto.isRead());
        return repository.save(existing);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /* ── Búsquedas extra ──────────────────────────────── */

    public List<Book> searchByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchByAuthor(String author) {
        return repository.findByAuthorContainingIgnoreCase(author);
    }

    public List<Book> searchByGenre(String genre) {
        return repository.findByGenreIgnoreCase(genre);
    }

    /* ── Mappers ──────────────────────────────────────── */

    private Book toEntity(BookDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .genre(dto.getGenre())
                .year(dto.getYear())
                .isbn(dto.getIsbn())
                .rating(dto.getRating())
                .read(dto.isRead())
                .build();
    }

    public BookDTO toDto(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .year(book.getYear())
                .isbn(book.getIsbn())
                .rating(book.getRating())
                .read(book.isRead())
                .build();
    }
}
