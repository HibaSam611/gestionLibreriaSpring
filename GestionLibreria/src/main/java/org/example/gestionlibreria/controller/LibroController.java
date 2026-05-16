package org.example.gestionlibreria.controller;

import jakarta.validation.Valid;
import org.example.gestionlibreria.dto.LibroDTO;
import org.example.gestionlibreria.model.Libro;
import org.example.gestionlibreria.service.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Este controlador maneja todas las peticiones HTTP de la API REST
// Cada metodo corresponde a una ruta (endpoint)

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") // para que JavaFX pueda conectarse sin problemas
public class LibroController {

    private final LibroService servicio;

    public LibroController(LibroService servicio) {
        this.servicio = servicio;
    }

    // GET /api/books --> devuelve todos los libros
    @GetMapping
    public ResponseEntity<List<Libro>> obtenerTodos() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    // GET /api/books/123 --> devuelve un libro por su id
    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerPorId(@PathVariable String id) {
        return servicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/books --> crea un libro nuevo (recibe BookDTO con validaciones)
    @PostMapping
    public ResponseEntity<Libro> crear(@Valid @RequestBody LibroDTO dto) {
        Libro guardado = servicio.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT /api/books/123 --> actualiza un libro existente
    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizar(@PathVariable String id, @Valid @RequestBody LibroDTO dto) {
        Libro actualizado = servicio.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE /api/books/123 --> elimina un libro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/books/search?titulo=quijote --> busca por titulo, autor o genero
    @GetMapping("/search")
    public ResponseEntity<List<Libro>> buscar(@RequestParam(required = false) String titulo,
                                              @RequestParam(required = false) String autor,
                                              @RequestParam(required = false) String genero) {
        if (titulo != null) return ResponseEntity.ok(servicio.buscarPorTitulo(titulo));
        if (autor != null)  return ResponseEntity.ok(servicio.buscarPorAutor(autor));
        if (genero != null) return ResponseEntity.ok(servicio.buscarPorGenero(genero));
        return ResponseEntity.ok(servicio.obtenerTodos());
    }
}
