package org.example.gestionlibreria.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Lanza la interfaz gráfica JavaFX.
 * El backend Spring Boot debe estar corriendo por separado
 * (ejecutar BookvaultApplication.main() primero).
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book_view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("BookVault – Gestor de Colección de Libros");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(550);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
