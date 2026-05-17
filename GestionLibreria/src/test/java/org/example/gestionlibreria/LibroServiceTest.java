package org.example.gestionlibreria;

import org.example.gestionlibreria.dto.LibroDTO;
import org.example.gestionlibreria.model.Libro;
import org.example.gestionlibreria.repository.LibroRepository;
import org.example.gestionlibreria.service.LibroService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock // esto simula el repositorio sin base de datos
    private LibroRepository repositorio;

    @InjectMocks // le mete el mock del repositorio al servicio
    private LibroService servicio;

    // Crea un libro de ejemplo para las pruebas
    private Libro crearLibroPrueba() {
        Libro libro = new Libro();
        libro.setId("1");
        libro.setTitulo("Don Quijote");
        libro.setAutor("Cervantes");
        libro.setGenero("Novela");
        libro.setAnio(1605);
        libro.setIsbn("9788420412146");
        libro.setNota(9.0);
        libro.setLeido(true);
        return libro;
    }

    // Crea un DTO de ejemplo para las pruebas
    private LibroDTO crearDtoPrueba() {
        LibroDTO dto = new LibroDTO();
        dto.setTitulo("Don Quijote");
        dto.setAutor("Cervantes");
        dto.setGenero("Novela");
        dto.setAnio(1605);
        dto.setIsbn("9788420412146");
        dto.setNota(9.0);
        dto.setLeido(true);
        return dto;
    }

    @Test
    @DisplayName("obtenerTodos devuelve la lista de libros")
    void testObtenerTodos() {
        // simulamos que el repositorio devuelve un libro
        when(repositorio.findAll()).thenReturn(List.of(crearLibroPrueba()));

        List<Libro> resultado = servicio.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("Don Quijote", resultado.get(0).getTitulo());
    }

    @Test
    @DisplayName("obtenerPorId devuelve el libro si existe")
    void testObtenerPorIdExiste() {
        when(repositorio.findById("1")).thenReturn(Optional.of(crearLibroPrueba()));

        Optional<Libro> resultado = servicio.obtenerPorId("1");

        assertTrue(resultado.isPresent());
        assertEquals("Cervantes", resultado.get().getAutor());
    }

    @Test
    @DisplayName("obtenerPorId devuelve vacio si no existe")
    void testObtenerPorIdNoExiste() {
        when(repositorio.findById("999")).thenReturn(Optional.empty());

        Optional<Libro> resultado = servicio.obtenerPorId("999");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("guardar crea un libro nuevo a partir de un DTO")
    void testGuardar() {
        LibroDTO dto = crearDtoPrueba();
        Libro libroGuardado = crearLibroPrueba();
        when(repositorio.save(any(Libro.class))).thenReturn(libroGuardado);

        Libro resultado = servicio.guardar(dto);

        assertEquals("Don Quijote", resultado.getTitulo());
        verify(repositorio, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("eliminar borra un libro que existe")
    void testEliminar() {
        when(repositorio.existsById("1")).thenReturn(true);

        servicio.eliminar("1");

        verify(repositorio, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("eliminar lanza error si el libro no existe")
    void testEliminarNoExiste() {
        when(repositorio.existsById("999")).thenReturn(false);

        // Comprobamos que lanza la excepcion bien
        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            servicio.eliminar("999");
        });

        assertTrue(error.getMessage().contains("999"));
    }

    @Test
    @DisplayName("actualizar cambia los datos del libro")
    void testActualizar() {
        Libro libroExistente = crearLibroPrueba();
        when(repositorio.findById("1")).thenReturn(Optional.of(libroExistente));
        when(repositorio.save(any(Libro.class))).thenReturn(libroExistente);

        LibroDTO dto = crearDtoPrueba();
        dto.setTitulo("Don Quijote de la Mancha");

        Libro resultado = servicio.actualizar("1", dto);

        assertEquals("Don Quijote de la Mancha", resultado.getTitulo());
    }

    @Test
    @DisplayName("buscarPorTitulo encuentra libros por texto parcial")
    void testBuscarPorTitulo() {
        when(repositorio.findByTituloContainingIgnoreCase("quijote"))
                .thenReturn(List.of(crearLibroPrueba()));

        List<Libro> resultado = servicio.buscarPorTitulo("quijote");

        assertEquals(1, resultado.size());
    }
}
