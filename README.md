# Sistema de Gestión de Usuarios y Solicitudes

API REST con **Java 17 + Spring Boot 3** para gestionar usuarios (Administrador, Empleado y Cliente) y las solicitudes de atención que crean los clientes.

## Tecnologías

- Java 17 / Spring Boot 3.3.5 / Maven
- Spring Web, Spring Data JPA, Validation, Lombok
- MySQL (base de datos principal) y H2 (pruebas y perfil alternativo)
- Postman

## Cómo ejecutar

1. Tener MySQL corriendo en el puerto 3306 con usuario `root` y contraseña `root`. La base `gestiondb` se crea sola.
2. Ejecutar `GestionApplication` desde IntelliJ, o con:

        mvn spring-boot:run

La API queda en `http://localhost:8080`.

Al iniciar por primera vez se cargan datos de prueba: 1 administrador, 2 empleados, 2 clientes y 6 solicitudes.

**Sin MySQL:** usar el perfil `h2` (Active profiles: `h2` en IntelliJ). La consola de H2 queda en `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:gestiondb`, usuario `sa`).

**Pruebas automáticas:**

    mvn test

## Estructura

    src/main/java/com/gestion
    ├── model/          Usuario (abstracta), Administrador, Empleado, Cliente, Solicitud, EstadoSolicitud
    ├── repository/     UsuarioRepository, ClienteRepository, SolicitudRepository (JpaRepository)
    ├── dto/            SolicitudRequest
    ├── exception/      ManejadorErrores, RecursoNoEncontradoException
    ├── service/        UsuarioService, SolicitudService
    ├── controller/     UsuarioController, SolicitudController
    ├── estructuras/    GestorSolicitudes (lista, cola, pila, búsqueda y ordenamiento)
    └── config/         CargaDatosIniciales

## Requerimientos del enunciado

| Requerimiento | Dónde |
|---|---|
| Encapsulación | Atributos `private` con constructores, getters y setters en `model/` |
| Herencia | `Administrador`, `Empleado` y `Cliente` extienden `Usuario` (`@Inheritance(SINGLE_TABLE)`) |
| Polimorfismo | `obtenerDescripcion()` es abstracto en `Usuario` y cada rol lo sobrescribe |
| Validaciones | `@NotBlank` (nombre), `@Email` (correo), `@Positive` (salario), `@Min(1) @Max(5)` (nivelAcceso) |
| JPA | `@Entity`, `JpaRepository`, relación `@OneToMany` Cliente → Solicitudes |
| Lista | `ArrayList<Solicitud>` en `GestorSolicitudes` |
| Cola | `Queue<Solicitud>`: solicitudes pendientes en orden de llegada |
| Pila | `Stack<Solicitud>`: historial de solicitudes procesadas |
| Búsqueda | Lineal y binaria (`GestorSolicitudes`) |
| Ordenamiento | Burbuja (por prioridad) e inserción (por id) |
| Paginación | `/usuarios/paginado` y `/solicitudes/paginado` con `PageRequest` y `Sort` |
| if / else | `UsuarioService.crear`, `SolicitudService.crear`, búsqueda binaria |
| switch | `UsuarioService.crear` y `CargaDatosIniciales` |
| for / while | `CargaDatosIniciales`, ordenamientos, `GestorSolicitudes.procesarTodas` |

## Endpoints

### Usuarios

| Método | URL | Descripción |
|---|---|---|
| POST | `/usuarios` | Crear usuario (el campo `tipo` define el rol) |
| GET | `/usuarios` | Listar usuarios |
| GET | `/usuarios/{id}` | Buscar usuario por id |
| GET | `/usuarios/paginado?pagina=0&tamano=5&ordenarPor=nombre&direccion=asc` | Paginación y ordenamiento |
| GET | `/usuarios/{id}/solicitudes` | Solicitudes de un cliente |

Ejemplos de body para `POST /usuarios`:

    { "tipo": "ADMINISTRADOR", "nombre": "Mario Díaz", "correo": "mario@empresa.com", "nivelAcceso": 4 }
    { "tipo": "EMPLEADO", "nombre": "Julia Pérez", "correo": "julia@empresa.com", "cargo": "Soporte", "salario": 2800000 }
    { "tipo": "CLIENTE", "nombre": "Andrés López", "correo": "andres@correo.com", "puntosFidelidad": 0 }

### Solicitudes

| Método | URL | Descripción |
|---|---|---|
| POST | `/solicitudes` | Crear solicitud (solo clientes) |
| GET | `/solicitudes` | Listar solicitudes |
| GET | `/solicitudes/{id}` | Buscar solicitud por id |
| GET | `/solicitudes/paginado?pagina=0&tamano=5&ordenarPor=prioridad&direccion=desc` | Paginación |
| GET | `/solicitudes/cola` | Ver la cola de pendientes |
| POST | `/solicitudes/procesar` | Atender la siguiente de la cola |
| POST | `/solicitudes/procesar-todas` | Atender toda la cola |
| GET | `/solicitudes/historial` | Ver la pila de procesadas |
| GET | `/solicitudes/ordenadas` | Ordenadas por prioridad (burbuja) |
| GET | `/solicitudes/buscar/{id}?metodo=binaria` | Búsqueda binaria o lineal |

Ejemplo de body para `POST /solicitudes`:

    { "descripcion": "La aplicación no carga", "prioridad": 3, "clienteId": 4 }

Prioridad: 1 = baja, 2 = media, 3 = alta. Cada solicitud suma 10 puntos de fidelidad al cliente.

### Errores

- `400`: datos inválidos (indica el campo), correo repetido, usuario que no es cliente o cola vacía.
- `404`: usuario o solicitud inexistente.

## Pruebas con Postman

Importar `postman_collection.json` en Postman (**Import**). Incluye todas las peticiones, con casos válidos y casos de error.

## Autores

- Natalia Afanador
- Daniel Cogollo
