package org.example.gestionlibreria.fx;

// Esta clase sirve para evitar el error de modulos de JavaFX
// En vez de ejecutar MainApp directamente, ejecutamos esta
// Como no extiende Application, Java no pide los modulos y funciona bien

public class Launcher {

    public static void main(String[] args) {
        MainApp.main(args);
    }
}
