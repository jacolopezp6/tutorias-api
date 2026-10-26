# Esquema de la base de datos

## 1. Propósito

El esquema representa la información principal del sistema de reservas de tutorías académicas. Organiza las áreas de conocimiento, los tutores que ofrecen sesiones, la disponibilidad semanal de cada tutor, las tutorías programadas y los estudiantes inscritos en ellas.

El modelo se compone de seis tablas: `areas`, `tutores`, `estudiantes`, `disponibilidades`, `tutorias` y `participantes`.

## 2. Vista general de las relaciones

- Un área puede agrupar a varios tutores. Cada tutor pertenece a una sola área.
- Un tutor puede publicar varias franjas de disponibilidad.
- Un tutor puede ofrecer varias tutorías; cada tutoría tiene un tutor responsable.
- Una tutoría puede tener varios participantes.
- Un estudiante puede participar en varias tutorías.
- La tabla `participantes` resuelve la relación de muchos a muchos entre estudiantes y tutorías.

```text
areas 1 ─── N tutores
                     ├── 1 ─── N disponibilidades
                     └── 1 ─── N tutorias ─── 1 ─── N participantes N ─── 1 estudiantes
```

## 3. Descripción de las tablas

### 3.1 `areas`

Catálogo de áreas académicas disponibles para clasificar la oferta de tutorías.

| Campo | Tipo | Obligatorio | Restricciones y descripción |
|---|---|---:|---|
| `id` | `INTEGER` | Sí | Clave primaria autogenerada. Identifica el área. |
| `nombre` | `VARCHAR(100)` | Sí | Nombre del área. Debe ser único para evitar duplicados en el catálogo. |

**Relación:** un área puede estar asociada con muchos tutores; cada tutor referencia una sola área mediante `tutores.area_id`.

### 3.2 `tutores`

Personas que publican disponibilidad y ofrecen tutorías.

| Campo | Tipo | Obligatorio | Restricciones y descripción |
|---|---|---:|---|
| `id` | `INTEGER` | Sí | Clave primaria autogenerada. |
| `nombre` | `VARCHAR(120)` | Sí | Nombre del tutor. |
| `correo` | `VARCHAR(160)` | Sí | Correo de contacto, único por tutor. |
| `area_id` | `INTEGER` | Sí | Clave foránea hacia `areas.id`; determina el área académica del tutor. |

**Relaciones:** cada tutor pertenece a un área y puede tener múltiples disponibilidades y tutorías.

### 3.3 `estudiantes`

Estudiantes que pueden inscribirse en las tutorías ofrecidas.

| Campo | Tipo | Obligatorio | Restricciones y descripción |
|---|---|---:|---|
| `id` | `INTEGER` | Sí | Clave primaria autogenerada. |
| `nombre` | `VARCHAR(120)` | Sí | Nombre del estudiante. |
| `correo` | `VARCHAR(160)` | Sí | Correo de contacto, único por estudiante. |

**Relación:** un estudiante puede tener varias inscripciones, registradas como filas en `participantes`.

### 3.4 `disponibilidades`

Franjas semanales recurrentes durante las cuales un tutor puede ofrecer tutorías. Cada registro pertenece a un tutor.

| Campo | Tipo | Obligatorio | Restricciones y descripción |
|---|---|---:|---|
| `id` | `INTEGER` | Sí | Clave primaria autogenerada. |
| `tutor_id` | `INTEGER` | Sí | Clave foránea hacia `tutores.id`. |
| `dia_semana` | `VARCHAR(15)` | Sí | Día de la semana guardado como texto: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY` o `SUNDAY`. |
| `hora_inicio` | `TIME` | Sí | Hora inicial de la franja. |
| `hora_fin` | `TIME` | Sí | Hora final de la franja. |
| `activa` | `BOOLEAN` | Sí | Indica si la franja se encuentra disponible. Se inicializa como verdadera en la entidad. |

**Relación:** un tutor puede tener varias franjas, por ejemplo, diferentes días u horarios.

**Validación de dominio:** la aplicación debe verificar que la hora inicial sea anterior a la hora final y que la tutoría solicitada quede dentro de una franja activa del tutor.

### 3.5 `tutorias`

Sesiones académicas que pueden reservar los estudiantes. Cada tutoría tiene un tutor, un tema, un horario, un límite de participantes y un estado.

| Campo | Tipo | Obligatorio | Restricciones y descripción |
|---|---|---:|---|
| `id` | `INTEGER` | Sí | Clave primaria autogenerada. |
| `tutor_id` | `INTEGER` | Sí | Clave foránea hacia `tutores.id`; identifica al tutor responsable. |
| `tema` | `VARCHAR(160)` | Sí | Tema académico de la sesión. |
| `fecha_hora_inicio` | `TIMESTAMP` | Sí | Fecha y hora de inicio. Corresponde a `LocalDateTime` en Java y no contiene zona horaria. |
| `fecha_hora_fin` | `TIMESTAMP` | Sí | Fecha y hora de finalización. Corresponde a `LocalDateTime` en Java y no contiene zona horaria. |
| `limite_participantes` | `INTEGER` | Sí | Máximo de estudiantes que pueden inscribirse. |
| `estado` | `VARCHAR(20)` | Sí | Estado textual de la tutoría: `PROGRAMADA`, `REALIZADA` o `CANCELADA`. La entidad inicia con `PROGRAMADA`. |

**Relaciones:** cada tutoría pertenece a un tutor y puede tener muchas inscripciones en `participantes`.

**Validaciones de dominio:** la aplicación debe comprobar que el inicio preceda al fin, que el horario esté dentro de la disponibilidad del tutor, que no se cruce con otra tutoría de ese mismo tutor y que no se exceda el límite de participantes.

### 3.6 `participantes`

Registro de la inscripción de un estudiante en una tutoría. Esta tabla representa la relación de muchos a muchos entre `estudiantes` y `tutorias`.

| Campo | Tipo | Obligatorio | Restricciones y descripción |
|---|---|---:|---|
| `id` | `INTEGER` | Sí | Clave primaria autogenerada. |
| `tutoria_id` | `INTEGER` | Sí | Clave foránea hacia `tutorias.id`. |
| `estudiante_id` | `INTEGER` | Sí | Clave foránea hacia `estudiantes.id`. |
| `fecha_inscripcion` | `TIMESTAMP` | Sí | Fecha y hora en que se registra la inscripción. Corresponde a `LocalDateTime` y se inicializa desde Java. |

**Restricción de unicidad:** la combinación de `tutoria_id` y `estudiante_id` es única. Así, el mismo estudiante no puede aparecer dos veces inscrito en la misma tutoría.

## 4. Claves y restricciones

- Cada tabla tiene una clave primaria `id` de tipo `INTEGER` autogenerado.
- Las relaciones entre tablas se representan mediante claves foráneas: `tutores.area_id`, `disponibilidades.tutor_id`, `tutorias.tutor_id`, `participantes.tutoria_id` y `participantes.estudiante_id`.
- Los campos requeridos por las entidades se almacenan como obligatorios (`NOT NULL`).
- El nombre de área y los correos de tutores y estudiantes son únicos.
- La pareja tutoría-estudiante es única en `participantes`.
- Los enums de Java se almacenan como cadenas, lo que hace legibles sus valores en la base de datos.

## 5. Reglas que pertenecen a la capa de negocio

El diseño de las entidades y sus restricciones de persistencia cubre la estructura y algunas reglas sencillas de integridad. Las siguientes comprobaciones deben implementarse en los servicios de la aplicación:

1. La tutoría debe ocurrir durante una franja activa de disponibilidad del tutor.
2. El tutor no puede tener dos tutorías cuyos intervalos de tiempo se solapen.
3. La cantidad de participantes no puede superar `limite_participantes`.
4. Un estudiante no puede duplicar su inscripción en la misma tutoría. La unicidad en la base de datos actúa como protección adicional.
5. Solo se deben permitir los estados `PROGRAMADA`, `REALIZADA` y `CANCELADA`, y sus transiciones deben controlarse desde la aplicación.
6. El retiro de un participante solo debe permitirse antes del inicio de la tutoría.
7. No deben almacenarse notas sensibles sobre el desempeño de los estudiantes.

## 6. Consideraciones del modelo actual

- La disponibilidad se modela como una franja semanal recurrente; no tiene fechas de vigencia ni excepciones por días festivos.
- `LocalDateTime` se persiste como un timestamp sin zona horaria. La aplicación y PostgreSQL deben manejar los horarios con una convención consistente.
- El límite de participantes se guarda en la tutoría, pero el conteo y su cumplimiento requieren lógica de servicio y transacciones.
- La base puede impedir inscripciones duplicadas con una restricción única, pero la validación previa mejora la respuesta que recibe el usuario.
- No se incluyen entidades de credenciales, autenticación, pagos ni evaluaciones, porque no forman parte de las entidades actuales.

