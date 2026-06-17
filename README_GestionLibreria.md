# GestionLibreria 📚

![Java](https://img.shields.io/badge/Java%2017-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=flat-square&logo=mongodb&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-EE6E0A?style=flat-square&logo=java&logoColor=white)

Gestor de biblioteca personal con dos partes en el mismo proyecto: una **API REST en Spring Boot** con persistencia en **MongoDB**, y un **cliente de escritorio en JavaFX** que consume esa API para gestionar la colección de libros con una interfaz gráfica.

## ✨ Funcionalidades

- CRUD completo de libros: título, autor, género, año, ISBN, nota (0–10) y estado de lectura (leído / no leído)
- Búsqueda por título, autor o género desde la propia API
- Validaciones de entrada (campos obligatorios, rango de año, rango de nota) tanto en el modelo como en el DTO
- Cliente de escritorio en JavaFX con tabla de libros, formulario de alta/edición, buscador, confirmación antes de eliminar y mensajes de estado/error

## 🧱 Stack técnico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Backend | Spring Boot 3.2, Spring Web, Spring Validation |
| Base de datos | MongoDB (Spring Data MongoDB) |
| Cliente de escritorio | JavaFX 17 |
| Boilerplate | Lombok |

## 🏗️ Arquitectura

El proyecto combina dos partes en un único módulo Maven:

- **API REST** (`controller`, `service`, `repository`, `model`, `dto`, `exception`): un único recurso, `Libro`, con su repositorio `MongoRepository`, su servicio y su controlador en `/api/books`.
- **Cliente JavaFX** (`fx`): una ventana de escritorio (`MainApp` + `LibroViewController`) que llama a la API anterior por HTTP (`java.net.http.HttpClient`) para listar, crear, actualizar, eliminar y buscar libros. `Launcher` es una clase auxiliar para arrancar la app FX sin problemas de módulos de Java.

## 📡 Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/books` | Lista todos los libros |
| GET | `/api/books/{id}` | Obtiene un libro por id |
| POST | `/api/books` | Crea un libro nuevo |
| PUT | `/api/books/{id}` | Actualiza un libro existente |
| DELETE | `/api/books/{id}` | Elimina un libro |
| GET | `/api/books/search?titulo=&autor=&genero=` | Busca por título, autor o género |

## ⚙️ Configuración

Por defecto, la app espera un MongoDB local en `mongodb://localhost:27017/gestion_libreria` (`application.properties`). Si usas Docker, basta con:

```bash
docker run -d -p 27017:27017 --name mongo-libreria mongo
```

El CORS está abierto (`@CrossOrigin(origins = "*")`) a propósito, para que el cliente JavaFX pueda conectarse sin fricciones en local.

## 🚀 Instalación y ejecución

```bash
git clone https://github.com/HibaSam611/gestionLibreriaSpring.git
cd gestionLibreriaSpring
./mvnw spring-boot:run
```

Esto levanta la API en `http://localhost:8080`. Para abrir el cliente de escritorio, ejecuta la clase `Launcher` (en `src/main/java/org/example/gestionlibreria/fx`) desde tu IDE con el backend ya arrancado.

## 👤 Autoría

Proyecto personal desarrollado por **Hiba Samraoui** como ejercicio del módulo de Acceso a Datos / Programación, combinando una API REST con MongoDB y un cliente de escritorio en JavaFX.

## 📄 Licencia

Proyecto desarrollado con fines académicos. Sin licencia de uso comercial.
