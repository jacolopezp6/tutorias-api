# Contrato actual de la API de tutorías

Este documento describe las rutas que implementa el backend y las reglas que aplica. El modelo sigue el esquema de seis tablas documentado en `ESQUEMA_BASE_DATOS.md`: `areas`, `tutores`, `estudiantes`, `disponibilidades`, `tutorias` y `participantes`.

> **Nombres del proyecto:** una propuesta anterior hablaba de `materias`; el esquema aprobado en el repositorio tiene `areas` y la tutoría guarda un `tema`. Por eso la API actual usa `/api/areas` y `tema`. Si el docente pide materias como entidad separada, primero se debe actualizar el esquema y este contrato de forma coordinada.

## Respuestas comunes

- `200 OK`: consulta o actualización exitosa.
- `201 Created`: registro creado.
- `204 No Content`: eliminación, retiro o cancelación exitosa.
- `400 Bad Request`: datos incompletos o inválidos.
- `404 Not Found`: no existe el registro indicado.
- `409 Conflict`: duplicado o conflicto con una regla del proyecto.

Los errores devuelven `fechaHora`, `codigo`, `error` y `mensaje`. Las fechas se envían como `LocalDateTime` ISO 8601, por ejemplo `2026-12-10T10:00:00`, usando hora local de Colombia; el esquema no guarda zona horaria.

## Estado del servicio

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/estado` | Comprueba que la API esté disponible. |

## Áreas

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/areas` | Lista áreas. |
| GET | `/api/areas/{id}` | Consulta un área. |
| POST | `/api/areas` | Crea un área con `nombre`. |
| PUT | `/api/areas/{id}` | Cambia el nombre. |
| DELETE | `/api/areas/{id}` | Elimina si no tiene tutores asociados. |

El nombre del área es obligatorio y no puede repetirse.

## Tutores

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/tutores` | Lista tutores. |
| GET | `/api/tutores/{id}` | Consulta un tutor. |
| GET | `/api/tutores/{id}/disponibilidad` | Consulta las franjas activas del tutor. |
| POST | `/api/tutores` | Crea un tutor con `nombre`, `correo` y `areaId`. |
| PUT | `/api/tutores/{id}` | Actualiza esos datos. |
| DELETE | `/api/tutores/{id}` | Elimina si no está relacionado con disponibilidades o tutorías. |

El correo debe ser válido y único. Una tutoría o disponibilidad relacionada impide borrar el tutor y produce `409 Conflict`.

## Estudiantes

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/estudiantes` | Lista estudiantes. |
| GET | `/api/estudiantes/{id}` | Consulta un estudiante. |
| POST | `/api/estudiantes` | Crea un estudiante con `nombre` y `correo`. |
| PUT | `/api/estudiantes/{id}` | Actualiza sus datos. |
| DELETE | `/api/estudiantes/{id}` | Elimina si no conserva inscripciones. |

El correo debe ser válido y único. No se guardan notas privadas ni evaluaciones del estudiante.

## Disponibilidades

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/tutores/{id}/disponibilidad` | Lista las franjas activas de un tutor. |
| GET | `/api/disponibilidades?tutorId={id}` | La misma consulta usando el recurso de disponibilidades. |
| POST | `/api/disponibilidades` | Crea una franja: `tutorId`, `diaSemana`, `horaInicio` y `horaFin`. |
| PUT | `/api/disponibilidades/{id}` | Cambia una franja. |
| DELETE | `/api/disponibilidades/{id}` | La desactiva; no borra el registro histórico. |

`diaSemana` acepta `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY` o `SUNDAY`. La hora inicial debe ser anterior a la hora final y las franjas activas del tutor no se pueden cruzar.

## Tutorías

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/tutorias` | Lista tutorías; acepta filtros opcionales `tutorId` y `estado`. |
| GET | `/api/tutorias/{id}` | Consulta una tutoría y su número de inscritos. |
| POST | `/api/tutorias` | Publica una sesión futura. |
| PUT | `/api/tutorias/{id}` | Actualiza una tutoría programada. |
| PATCH | `/api/tutorias/{id}/estado` | Cambia su estado. |
| DELETE | `/api/tutorias/{id}` | La cancela; conserva el registro. |

El `POST` recibe `tutorId`, `tema`, `fechaHoraInicio`, `fechaHoraFin` y `limiteParticipantes`. El estado inicial lo asigna el servidor como `PROGRAMADA`.

Al publicar o cambiar una sesión, el servicio verifica que:

1. El inicio sea anterior al fin y ambos estén en el futuro.
2. Toda la sesión quede dentro de una disponibilidad activa del tutor para ese día.
3. El tutor no tenga otra sesión programada que se cruce.
4. El límite sea de al menos una persona y no baje del número de inscritos actual.

Estados disponibles: `PROGRAMADA`, `REALIZADA`, `CANCELADA`. Solo se puede marcar `REALIZADA` después de la hora de fin. Una sesión cancelada o realizada no vuelve a programada. `DELETE` y `PATCH` conservan el historial.

## Participantes

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/tutorias/{id}/participantes` | Lista estudiantes inscritos. |
| POST | `/api/tutorias/{id}/participantes` | Inscribe usando `estudianteId`. |
| DELETE | `/api/participantes/{id}` | Retira una inscripción antes de que inicie la tutoría. |

La inscripción se rechaza si la sesión no está programada, ya empezó, el estudiante ya está inscrito o se alcanzó el cupo. La base de datos también impide repetir la pareja tutoría-estudiante.

## Fuera del alcance actual

- Inicio de sesión, contraseñas y permisos de usuario: el modelo del proyecto todavía no define autenticación.
- Fechas especiales o festivos que anulen una disponibilidad semanal.
- Una entidad separada de materias: está pendiente confirmar si el docente quiere reemplazar o complementar el catálogo de áreas.
