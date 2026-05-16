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
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BookViewController {

    /* ── Componentes FXML ─────────────────────────────── */
    @FXML private TableView<Map<String, Object>> bookTable;
    @FXML private TableColumn<Map<String, Object>, String>  colTitle, colAuthor, colGenre, colIsbn;
    @FXML private TableColumn<Map<String, Object>, Number>  colYear, colRating;
    @FXML private TableColumn<Map<String, Object>, Boolean> colRead;

    @FXML private TextField  titleField, authorField, yearField, isbnField, searchField;
    @FXML private ComboBox<String> genreCombo;
    @FXML private Slider     ratingSlider;
    @FXML private CheckBox   readCheck;
    @FXML private Label      statusLabel;

    /* ── Estado interno ───────────────────────────────── */
    private final String API = "http://localhost:8080/api/books";
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
    private String selectedId = null;

    /* ── Inicialización ───────────────────────────────── */
    @FXML
    public void initialize() {
        // Géneros predefinidos
        genreCombo.setItems(FXCollections.observableArrayList(
                "Novela", "Ciencia ficción", "Fantasía", "Terror",
                "Historia", "Biografía", "Poesía", "Ensayo",
                "Técnico", "Autoayuda", "Infantil", "Otro"));

        // Mapeo de columnas
        colTitle.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "title")));
        colAuthor.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "author")));
        colGenre.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "genre")));
        colIsbn.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "isbn")));
        colYear.setCellValueFactory(c ->
                new SimpleIntegerProperty(num(c.getValue(), "year")));
        colRating.setCellValueFactory(c ->
                new SimpleDoubleProperty(dbl(c.getValue(), "rating")));
        colRead.setCellValueFactory(c ->
                new SimpleBooleanProperty(bool(c.getValue(), "read")));

        bookTable.setItems(data);

        // Al seleccionar una fila, rellenar el formulario
        bookTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) fillForm(newVal);
                });

        loadBooks();
    }

    /* ═══════════════════════════════════════════════════
       ACCIONES DE LOS BOTONES
       ═══════════════════════════════════════════════════ */

    @FXML
    private void onSave() {
        String json = buildJson();
        if (json == null) return;

        sendAsync("POST", API, json, response -> {
            if (response.statusCode() == 201) {
                status("✅ Libro guardado correctamente.");
                loadBooks();
                clearForm();
            } else {
                showApiError(response);
            }
        });
    }

    @FXML
    private void onUpdate() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Selecciona un libro de la tabla primero.");
            return;
        }
        String json = buildJson();
        if (json == null) return;

        sendAsync("PUT", API + "/" + selectedId, json, response -> {
            if (response.statusCode() == 200) {
                status("✅ Libro actualizado correctamente.");
                loadBooks();
                clearForm();
            } else {
                showApiError(response);
            }
        });
    }

    @FXML
    private void onDelete() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Selecciona un libro de la tabla primero.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Estás seguro de que quieres eliminar este libro?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmar eliminación");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                sendAsync("DELETE", API + "/" + selectedId, null, response -> {
                    if (response.statusCode() == 204) {
                        status("🗑 Libro eliminado.");
                        loadBooks();
                        clearForm();
                    } else {
                        showApiError(response);
                    }
                });
            }
        });
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadBooks();
            return;
        }
        String url = API + "/search?title=" + query;
        sendAsync("GET", url, null, response -> {
            if (response.statusCode() == 200) {
                List<Map<String, Object>> books = parseList(response.body());
                Platform.runLater(() -> {
                    data.setAll(books);
                    status("🔍 " + books.size() + " resultado(s) para \"" + query + "\"");
                });
            }
        });
    }

    @FXML
    private void onRefresh() {
        searchField.clear();
        loadBooks();
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    /* ═══════════════════════════════════════════════════
       HELPERS
       ═══════════════════════════════════════════════════ */

    private void loadBooks() {
        sendAsync("GET", API, null, response -> {
            if (response.statusCode() == 200) {
                List<Map<String, Object>> books = parseList(response.body());
                Platform.runLater(() -> {
                    data.setAll(books);
                    status("📚 " + books.size() + " libro(s) en la colección.");
                });
            } else {
                Platform.runLater(() ->
                        status("⚠️ No se pudo conectar al servidor. ¿Está el backend arrancado?"));
            }
        });
    }

    private String buildJson() {
        // Validaciones en cliente
        String title  = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre  = genreCombo.getValue();
        String yearS  = yearField.getText().trim();
        String isbn   = isbnField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || genre == null || yearS.isEmpty() || isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Todos los campos marcados con * son obligatorios.");
            return null;
        }
        int year;
        try {
            year = Integer.parseInt(yearS);
            if (year < 1450 || year > 2026) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "El año debe ser un número entre 1450 y 2026.");
            return null;
        }
        if (!isbn.matches("\\d{10}|\\d{13}")) {
            showAlert(Alert.AlertType.ERROR, "El ISBN debe tener exactamente 10 o 13 dígitos.");
            return null;
        }

        return String.format("""
                {
                  "title":"%s","author":"%s","genre":"%s",
                  "year":%d,"isbn":"%s","rating":%.1f,"read":%b
                }""",
                escape(title), escape(author), escape(genre),
                year, isbn, ratingSlider.getValue(), readCheck.isSelected());
    }

    private void fillForm(Map<String, Object> row) {
        selectedId = str(row, "id");
        titleField.setText(str(row, "title"));
        authorField.setText(str(row, "author"));
        genreCombo.setValue(str(row, "genre"));
        yearField.setText(String.valueOf(num(row, "year")));
        isbnField.setText(str(row, "isbn"));
        ratingSlider.setValue(dbl(row, "rating"));
        readCheck.setSelected(bool(row, "read"));
        status("Editando: " + str(row, "title"));
    }

    private void clearForm() {
        selectedId = null;
        titleField.clear();
        authorField.clear();
        genreCombo.setValue(null);
        yearField.clear();
        isbnField.clear();
        ratingSlider.setValue(5);
        readCheck.setSelected(false);
        bookTable.getSelectionModel().clearSelection();
        status("Formulario limpio.");
    }

    /* ── HTTP helpers ─────────────────────────────────── */

    @FunctionalInterface
    private interface ResponseHandler {
        void handle(HttpResponse<String> response);
    }

    private void sendAsync(String method, String url, String body, ResponseHandler handler) {
        HttpRequest.Builder rb = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Content-Type", "application/json");

        switch (method) {
            case "POST"   -> rb.POST(HttpRequest.BodyPublishers.ofString(body));
            case "PUT"    -> rb.PUT(HttpRequest.BodyPublishers.ofString(body));
            case "DELETE" -> rb.DELETE();
            default       -> rb.GET();
        }

        http.sendAsync(rb.build(), HttpResponse.BodyHandlers.ofString())
                .thenAccept(handler)
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            status("❌ Error de conexión: " + ex.getMessage()));
                    return null;
                });
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private void showApiError(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> err = mapper.readValue(resp.body(), Map.class);
                String msg = err.getOrDefault("error", resp.body()).toString();
                if (err.containsKey("details")) msg += "\n" + err.get("details");
                showAlert(Alert.AlertType.ERROR, msg);
                status("⚠️ Error del servidor (" + resp.statusCode() + ")");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error " + resp.statusCode());
            }
        });
    }

    /* ── Utilidades de mapa ───────────────────────────── */
    private String  str(Map<String, Object> m, String k)  { Object v = m.get(k); return v != null ? v.toString() : ""; }
    private int     num(Map<String, Object> m, String k)  { Object v = m.get(k); return v instanceof Number n ? n.intValue() : 0; }
    private double  dbl(Map<String, Object> m, String k)  { Object v = m.get(k); return v instanceof Number n ? n.doubleValue() : 0; }
    private boolean bool(Map<String, Object> m, String k) { Object v = m.get(k); return v instanceof Boolean b && b; }
    private String  escape(String s) { return s.replace("\"", "\\\""); }

    private void status(String msg) {
        Platform.runLater(() -> statusLabel.setText(msg));
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(type, msg, ButtonType.OK);
            a.setHeaderText(type == Alert.AlertType.ERROR ? "Error" : "Aviso");
            a.showAndWait();
        });
    }
}
