package org.example.gestionlibreria.fx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LibroViewController {

    @FXML private TableView<Map<String, Object>> tablaLibros;
    @FXML private TableColumn<Map<String, Object>, String>  colTitulo, colAutor, colGenero, colIsbn;
    @FXML private TableColumn<Map<String, Object>, Number>  colAnio, colNota;
    @FXML private TableColumn<Map<String, Object>, Boolean> colLeido;

    @FXML private TextField campoTitulo, campoAutor, campoAnio, campoIsbn, campoBusqueda;
    @FXML private ComboBox<String> comboGenero;
    @FXML private Slider sliderNota;
    @FXML private CheckBox checkLeido;
    @FXML private Label etiquetaEstado;

    private final String URL_API = "http://localhost:8080/api/books";
    private final HttpClient clienteHttp = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObservableList<Map<String, Object>> listaLibros = FXCollections.observableArrayList();
    private String idSeleccionado = null; // id del libro que tenemos seleccionado en la tabla

    @FXML
    public void initialize() {
        comboGenero.setItems(FXCollections.observableArrayList(
                "Novela", "Ciencia ficcion", "Fantasia", "Terror",
                "Historia", "Biografia", "Poesia", "Ensayo",
                "Tecnico", "Autoayuda", "Infantil", "Otro"));

        // Configuramos que dato muestra cada columna
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(obtenerTexto(c.getValue(), "titulo")));
        colAutor.setCellValueFactory(c -> new SimpleStringProperty(obtenerTexto(c.getValue(), "autor")));
        colGenero.setCellValueFactory(c -> new SimpleStringProperty(obtenerTexto(c.getValue(), "genero")));
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(obtenerTexto(c.getValue(), "isbn")));
        colAnio.setCellValueFactory(c -> new SimpleIntegerProperty(obtenerEntero(c.getValue(), "anio")));
        colNota.setCellValueFactory(c -> new SimpleDoubleProperty(obtenerDecimal(c.getValue(), "nota")));
        colLeido.setCellValueFactory(c -> new SimpleBooleanProperty(obtenerBooleano(c.getValue(), "leido")));

        tablaLibros.setItems(listaLibros);

        // Cuando hacemos click en una fila de la tabla, rellenamos el formulario
        tablaLibros.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) {
                rellenarFormulario(nuevo);
            }
        });

        // Cargamos todos los libros al abrir
        cargarTodosLosLibros();
    }


    // boton GUARDAR --> crea un libro nuevo
    @FXML
    private void onGuardar() {
        String json = construirJson();
        if (json == null) return; // habia algun campo mal

        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            clienteHttp.sendAsync(peticion, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(respuesta -> {
                        Platform.runLater(() -> {
                            if (respuesta.statusCode() == 201) {
                                mostrarEstado("Libro guardado correctamente");
                                limpiarFormulario();
                                cargarTodosLosLibros();
                            } else {
                                mostrarError("Error al guardar: " + respuesta.body());
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> mostrarError("No se pudo conectar al servidor"));
                        return null;
                    });
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    // boton ACTUALIZAR --> modifica el libro que tenemos seleccionado
    @FXML
    private void onActualizar() {
        if (idSeleccionado == null) {
            mostrarAviso("Selecciona un libro de la tabla primero");
            return;
        }

        String json = construirJson();
        if (json == null) return;

        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API + "/" + idSeleccionado))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            clienteHttp.sendAsync(peticion, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(respuesta -> {
                        Platform.runLater(() -> {
                            if (respuesta.statusCode() == 200) {
                                mostrarEstado("Libro actualizado correctamente");
                                limpiarFormulario();
                                cargarTodosLosLibros();
                            } else {
                                mostrarError("Error al actualizar: " + respuesta.body());
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> mostrarError("No se pudo conectar al servidor"));
                        return null;
                    });
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    // Boton ELIMINAR --> borra el libro seleccionado
    @FXML
    private void onEliminar() {
        if (idSeleccionado == null) {
            mostrarAviso("Selecciona un libro de la tabla primero");
            return;
        }

        // Pedimos confirmacion antes de borrar
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que quieres eliminar este libro?", ButtonType.YES, ButtonType.NO);
        confirmar.setHeaderText("Confirmar eliminacion");
        confirmar.showAndWait().ifPresent(boton -> {
            if (boton == ButtonType.YES) {
                try {
                    HttpRequest peticion = HttpRequest.newBuilder()
                            .uri(URI.create(URL_API + "/" + idSeleccionado))
                            .DELETE()
                            .build();

                    clienteHttp.sendAsync(peticion, HttpResponse.BodyHandlers.ofString())
                            .thenAccept(respuesta -> {
                                Platform.runLater(() -> {
                                    if (respuesta.statusCode() == 204) {
                                        mostrarEstado("Libro eliminado");
                                        limpiarFormulario();
                                        cargarTodosLosLibros();
                                    } else {
                                        mostrarError("Error al eliminar");
                                    }
                                });
                            })
                            .exceptionally(ex -> {
                                Platform.runLater(() -> mostrarError("No se pudo conectar al servidor"));
                                return null;
                            });
                } catch (Exception e) {
                    mostrarError("Error: " + e.getMessage());
                }
            }
        });
    }

    // Boton BUSCAR --> busca libros por titulo
    @FXML
    private void onBuscar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty()) {
            cargarTodosLosLibros();
            return;
        }

        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API + "/search?titulo=" + texto))
                    .GET()
                    .build();

            clienteHttp.sendAsync(peticion, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(respuesta -> {
                        Platform.runLater(() -> {
                            if (respuesta.statusCode() == 200) {
                                List<Map<String, Object>> libros = parsearListaJson(respuesta.body());
                                listaLibros.setAll(libros);
                                mostrarEstado(libros.size() + " resultado(s) para '" + texto + "'");
                            }
                        });
                    });
        } catch (Exception e) {
            mostrarError("Error en la busqueda");
        }
    }

    // Boton REFRESCAR --> vuelve a cargar todos
    @FXML
    private void onRefrescar() {
        campoBusqueda.clear();
        cargarTodosLosLibros();
    }

    // Boton LIMPIAR --> limpia el formulario
    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    // Carga todos los libros del backend y los pone en la tabla
    private void cargarTodosLosLibros() {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API))
                    .GET()
                    .build();

            clienteHttp.sendAsync(peticion, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(respuesta -> {
                        Platform.runLater(() -> {
                            if (respuesta.statusCode() == 200) {
                                List<Map<String, Object>> libros = parsearListaJson(respuesta.body());
                                listaLibros.setAll(libros);
                                mostrarEstado(libros.size() + " libro(s) en la coleccion");
                            } else {
                                mostrarEstado("Error al cargar los libros");
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> mostrarEstado("No se pudo conectar. ¿Esta el backend arrancado?"));
                        return null;
                    });
        } catch (Exception e) {
            mostrarEstado("Error de conexion");
        }
    }

    // Construye el JSON con los datos del formulario para enviarlo al backend
    private String construirJson() {
        String titulo = campoTitulo.getText().trim();
        String autor  = campoAutor.getText().trim();
        String genero = comboGenero.getValue();
        String anioTexto = campoAnio.getText().trim();
        String isbn   = campoIsbn.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty() || genero == null || anioTexto.isEmpty() || isbn.isEmpty()) {
            mostrarAviso("Todos los campos son obligatorios");
            return null;
        }

        // comprobamos que el año sea un numero valido
        int anio;
        try {
            anio = Integer.parseInt(anioTexto);
        } catch (NumberFormatException e) {
            mostrarError("El año debe ser un numero");
            return null;
        }

        double nota = sliderNota.getValue();
        boolean leido = checkLeido.isSelected();

        // Usamos Locale.US para que los decimales usen punto (7.5) y no coma (7,5)
        return String.format(Locale.US,
                "{\"titulo\":\"%s\",\"autor\":\"%s\",\"genero\":\"%s\",\"anio\":%d,\"isbn\":\"%s\",\"nota\":%.1f,\"leido\":%b}",
                titulo, autor, genero, anio, isbn, nota, leido
        );
    }

    // rellena el formulario con los datos del libro que hemos seleccionado en la tabla
    private void rellenarFormulario(Map<String, Object> fila) {
        idSeleccionado = obtenerTexto(fila, "id");
        campoTitulo.setText(obtenerTexto(fila, "titulo"));
        campoAutor.setText(obtenerTexto(fila, "autor"));
        comboGenero.setValue(obtenerTexto(fila, "genero"));
        campoAnio.setText(String.valueOf(obtenerEntero(fila, "anio")));
        campoIsbn.setText(obtenerTexto(fila, "isbn"));
        sliderNota.setValue(obtenerDecimal(fila, "nota"));
        checkLeido.setSelected(obtenerBooleano(fila, "leido"));
        mostrarEstado("Editando: " + obtenerTexto(fila, "titulo"));
    }

    // deja el formulario vacio
    private void limpiarFormulario() {
        idSeleccionado = null;
        campoTitulo.clear();
        campoAutor.clear();
        comboGenero.setValue(null);
        campoAnio.clear();
        campoIsbn.clear();
        sliderNota.setValue(5);
        checkLeido.setSelected(false);
        tablaLibros.getSelectionModel().clearSelection();
    }

    // Convierte el JSON que viene del servidor en una lista de mapas
    private List<Map<String, Object>> parsearListaJson(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }


    private String obtenerTexto(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor != null ? valor.toString() : "";
    }

    private int obtenerEntero(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor instanceof Number ? ((Number) valor).intValue() : 0;
    }

    private double obtenerDecimal(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor instanceof Number ? ((Number) valor).doubleValue() : 0;
    }

    private boolean obtenerBooleano(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor instanceof Boolean && (Boolean) valor;
    }

    private void mostrarEstado(String mensaje) {
        Platform.runLater(() -> etiquetaEstado.setText(mensaje));
    }

    private void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
            alerta.setHeaderText("Error");
            alerta.showAndWait();
        });
    }

    private void mostrarAviso(String mensaje) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
            alerta.setHeaderText("Aviso");
            alerta.showAndWait();
        });
    }
}
