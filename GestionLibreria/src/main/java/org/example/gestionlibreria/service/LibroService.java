package org.example.gestionlibreria.service;

import org.example.gestionlibreria.dto.LibroDTO;
import org.example.gestionlibreria.model.Libro;
import org.example.gestionlibreria.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// El servicio contiene la logica de la aplicacion
// Es el intermediario entre el controlador (que recibe las peticiones)
// y el repositorio (que accede a la base de datos)

@Service
public class LibroService {

    private final LibroRepository repositorio;

    // Spring inyecta el repositorio automaticamente por el constructor
    public LibroService(LibroRepository repositorio) {
        this.repositorio = repositorio;
    }

    // -- OPERACIONES CRUD --

    // Obtener todos los libros
    public List<Libro> obtenerTodos() {
        return repositorio.findAll();
    }

    // Buscar un libro por su id
    public Optional<Libro> obtenerPorId(String id) {
        return repositorio.findById(id);
    }

    // Guardar un libro nuevo (recibe el DTO y lo convierte a Book)
    public Libro guardar(LibroDTO dto) {
        Libro libro = convertirDtoABook(dto);
        return repositorio.save(libro);
    }

    // Actualizar un libro que ya existe
    public Libro actualizar(String id, LibroDTO dto) {
        // Primero comprobamos que el libro existe
        Libro libroExistente = repositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el libro con ID: " + id));

        // Actualizamos sus campos con los datos nuevos del DTO
        libroExistente.setTitulo(dto.getTitulo());
        libroExistente.setAutor(dto.getAutor());
        libroExistente.setGenero(dto.getGenero());
        libroExistente.setAnio(dto.getAnio());
        libroExistente.setIsbn(dto.getIsbn());
        libroExistente.setNota(dto.getNota());
        libroExistente.setLeido(dto.isLeido());

        return repositorio.save(libroExistente);
    }

    // Eliminar un libro por su id
    public void eliminar(String id) {
        // Comprobamos que existe antes de borrar
        if (!repositorio.existsById(id)) {
            throw new RuntimeException("No se encontro el libro con ID: " + id);
        }
        repositorio.deleteById(id);
    }

    // -- BUSQUEDAS --

    public List<Libro> buscarPorTitulo(String titulo) {
        return repositorio.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Libro> buscarPorAutor(String autor) {
        return repositorio.findByAutorContainingIgnoreCase(autor);
    }

    public List<Libro> buscarPorGenero(String genero) {
        return repositorio.findByGeneroIgnoreCase(genero);
    }

    // -- METODOS DE CONVERSION (DTO <-> Book) --

    // Convierte un DTO a un Book para poder guardarlo en la base de datos
    private Libro convertirDtoABook(LibroDTO dto) {
        Libro libro = new Libro();
        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setGenero(dto.getGenero());
        libro.setAnio(dto.getAnio());
        libro.setIsbn(dto.getIsbn());
        libro.setNota(dto.getNota());
        libro.setLeido(dto.isLeido());
        return libro;
    }

    // Convierte un Book a DTO para enviarlo al cliente
    public LibroDTO convertirBookADto(Libro libro) {
        LibroDTO dto = new LibroDTO();
        dto.setTitulo(libro.getTitulo());
        dto.setAutor(libro.getAutor());
        dto.setGenero(libro.getGenero());
        dto.setAnio(libro.getAnio());
        dto.setIsbn(libro.getIsbn());
        dto.setNota(libro.getNota());
        dto.setLeido(libro.isLeido());
        return dto;
    }
}
