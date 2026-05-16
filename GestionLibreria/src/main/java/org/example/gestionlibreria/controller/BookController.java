package org.example.gestionlibreria.controller;

import com.example.gestionlibreria.dto.BookDTO;
import com.sergio.bookvault.model.Book;
import com.sergio.bookvault.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Libros", description = "CRUD de la colección de libros")
public class BookController {

    private final BookService service;

    /* ── GET  /api/books ──────────────────────────────── */
    @GetMapping
    @Operation(summary = "Obtener todos los libros")
    public ResponseEntity<List<Book>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /* ── GET  /api/books/{id} ─────────────────────────── */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un libro por su ID")
    public ResponseEntity<Book> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /* ── POST /api/books ──────────────────────────────── */
    @PostMapping
    @Operation(summary = "Crear un libro nuevo")
    public ResponseEntity<Book> create(@Valid @RequestBody BookDTO dto) {
        Book created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /* ── PUT  /api/books/{id} ─────────────────────────── */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un libro existente")
    public ResponseEntity<Book> update(@PathVariable String id,
                                       @Valid @RequestBody BookDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /* ── DELETE /api/books/{id} ────────────────────────── */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un libro por su ID")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ── GET /api/books/search?title=… ────────────────── */
    @GetMapping("/search")
    @Operation(summary = "Buscar libros por título, autor o género")
    public ResponseEntity<List<Book>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {

        if (title != null && !title.isBlank())  return ResponseEntity.ok(service.searchByTitle(title));
        if (author != null && !author.isBlank()) return ResponseEntity.ok(service.searchByAuthor(author));
        if (genre != null && !genre.isBlank())   return ResponseEntity.ok(service.searchByGenre(genre));

        return ResponseEntity.ok(service.findAll());
    }
}
