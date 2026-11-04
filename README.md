# Tutorías API

Backend para publicar horarios de tutores y gestionar tutorías e inscripciones de estudiantes. Está construido con Java 21, Spring Boot, Spring Data JPA y PostgreSQL.

## Antes de iniciar

Necesitas Java 21, PostgreSQL y la contraseña del usuario de la base de datos.

1. Crea una base de datos llamada `tutorias_db` en PostgreSQL.
2. En PowerShell, define la contraseña para la terminal actual (reemplaza el texto de ejemplo y no lo guardes en Git):

   ```powershell
   $env:DB_PASSWORD = "TU_CONTRASENA_LOCAL"
   ```

3. Desde la carpeta del proyecto, inicia el backend:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   En IntelliJ, configura `DB_PASSWORD` en la configuración de ejecución de `TutoriasApiApplication`.

La conexión local está definida en `src/main/resources/application.properties`. Hibernate crea o actualiza las tablas al iniciar (`ddl-auto=update`). No subas contraseñas al repositorio.

## Lombok

Lombok ya está incluido como dependencia de compilación. En IntelliJ activa **Settings > Build, Execution, Deployment > Compiler > Annotation Processors > Enable annotation processing** y recarga Maven si el editor no reconoce los métodos generados.

- Los servicios y controladores usan `@RequiredArgsConstructor` para crear sus constructores de inyección a partir de los campos `final`.
- Las entidades usan `@Getter` y un constructor vacío protegido para JPA. Sus cambios se hacen con métodos como `cancelar()` y `desactivar()`.
- Los mensajes de inicio y persistencia usan `@Slf4j` para escribir al registro de la aplicación.
- No uses `@Data` ni `@Setter` en las entidades: podrían permitir cambios que salten las reglas del proyecto.

## Comprobar que funciona

Al terminar el inicio, la consola imprime la dirección de la API y un ejemplo de ruta. La raíz `http://localhost:8080` no es una página web; puede mostrar 404 y eso es normal.

- `GET http://localhost:8080/api/estado` debe informar que el servicio está disponible.
- `GET http://localhost:8080/api/tutores` debe devolver una lista JSON. `[]` significa que todavía no se han registrado tutores.

## Probar con Postman

Importa `postman/tutorias-api.postman_collection.json` y ejecuta las solicitudes en orden. La colección guarda los IDs que va creando y usa una fecha futura para la disponibilidad y la tutoría. Incluye una solicitud que intenta duplicar una inscripción y espera `409 Conflict`.

## Rutas disponibles

| Recurso | Operaciones |
|---|---|
| Estado | `GET /api/estado` |
| Áreas | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` en `/api/areas` |
| Tutores | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` en `/api/tutores` |
| Estudiantes | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` en `/api/estudiantes` |
| Disponibilidad | `GET /api/tutores/{id}/disponibilidad`, `POST /api/disponibilidades`, `PUT /api/disponibilidades/{id}`, `DELETE /api/disponibilidades/{id}` |
| Tutorías | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `PATCH /{id}/estado`, `DELETE /{id}` en `/api/tutorias` |
| Participantes | `GET` y `POST /api/tutorias/{id}/participantes`; `DELETE /api/participantes/{id}` |

La eliminación de una disponibilidad la desactiva. La eliminación de una tutoría la cancela para conservar su historial.

## Reglas de reservas

- La tutoría debe ser futura y quedar dentro de la disponibilidad activa del tutor.
- Un tutor no puede tener tutorías programadas que se crucen.
- La inscripción se rechaza si la tutoría no está programada, empezó, ya alcanzó el cupo o el estudiante ya está inscrito.
- Se permite retirar al estudiante antes del inicio.
- Solo se marca una tutoría como `REALIZADA` después de su hora de finalización. Los estados disponibles son `PROGRAMADA`, `REALIZADA` y `CANCELADA`.
- No se guardan notas sensibles de desempeño.

La disponibilidad es semanal y recurrente; este modelo no contempla festivos ni excepciones por una fecha puntual. Las fechas usan hora local y no incluyen zona horaria, igual que la columna `TIMESTAMP` de PostgreSQL.

## Pruebas

Ejecuta la suite automatizada:

```powershell
.\mvnw.cmd test
```

Las pruebas usan H2 temporal; no escriben en la base local `tutorias_db`.

## Documentos del proyecto

- `ESQUEMA_BASE_DATOS.md`: tablas, relaciones y reglas de persistencia.
- `docs/especificacion-api.md`: rutas y contrato actual de las solicitudes y respuestas.

El esquema actual usa áreas académicas. Una propuesta anterior habla de materias como recurso separado; si el equipo/docente confirma ese cambio, hay que actualizar juntos el modelo, la base de datos, la API y Postman.

## Flujo de trabajo con GitHub

1. Actualiza `develop` antes de comenzar una tarea.
2. Crea una rama corta para esa tarea, por ejemplo `feature/estudiantes`.
3. Guarda los cambios en un commit y sube solo esa rama.
4. Abre un PR hacia `develop` y espera la revisión indicada en el repositorio (`.github/CODEOWNERS`).

No se deben subir cambios directamente a `develop` o `main`.
