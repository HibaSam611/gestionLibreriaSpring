package org.example.gestionlibreria.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Clase que abre la ventana de JavaFX
// IMPORTANTE: primero hay que arrancar GestionLibreriaApplication (el backend)
// y luego ejecutar esta clase (o mejor, ejecutar Launcher.java)

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Libro_view.fxml"));
        stage.setTitle("GestionLibreria - Gestor de Libros");
        stage.setScene(new Scene(root, 1050, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
