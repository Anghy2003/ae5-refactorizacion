# Ae5 | Refactorización respaldada por pruebas unitarias

Repositorio: https://github.com/Anghy2003/ae5-refactorizacion (rama
`ae5-refactorizacion`). La rama `main` conserva el código inicial (estado
final del Laboratorio 1); esta rama contiene la red de seguridad y las cinco
refactorizaciones, una por commit.

## Ciclo que seguí en cada paso

```
PRUEBA VERDE → CAMBIO PEQUEÑO → PRUEBA VERDE → COMMIT → SIGUIENTE CAMBIO
```

Después de cada refactorización ejecuté `sh docs/ae5/verificar.sh <paso>`, que
hace dos comprobaciones y guarda ambas salidas en `docs/ae5/`:

1. `mvn clean test` debe terminar en `BUILD SUCCESS` (`tests-<paso>.txt`).
2. La salida de `LineaBase` (los nueve escenarios del Lab 1) debe ser **byte a
   byte idéntica** a la original (`salida-<paso>.txt` frente a
   `salida-linea-base-original.txt`).

Las siete salidas comparten el mismo MD5 (`comparacion-salidas.txt`): el
comportamiento observable no cambió en ningún paso.

## Paso 1 | Estado inicial y red de seguridad

| Commit | Qué representa |
|---|---|
| `chore: agregar JUnit 5 y Surefire para la red de seguridad de Ae5` | El `pom.xml` heredado no tenía dependencias. |
| `test: caracterizar el comportamiento heredado de ServicioReservas y Reserva` | 17 pruebas AAA verdes **sobre el código heredado intacto**. |
| `docs: conservar copia del codigo inicial para la comparacion de Ae5` | `docs/ae5/codigo-inicial/`. |

Las 17 pruebas iniciales congelan LB-01 a LB-09 del Laboratorio 1 más cuatro
casos que agregué al escribirlas: correo nulo, `fin` anterior a `inicio`, dos
defectos simultáneos, y `"a@"` como correo aceptado. Dos de ellas vigilan lo
que ninguna prueba de retorno detecta: `reservaValidaGuardaYNotificaEnEseOrden()`
captura `System.out` y afirma las dos líneas exactas y su orden, y
`reservaRechazadaNoGuardaNiNotifica()` afirma que no se imprime nada.

## Refactorización 1 | Decompose Conditional y Extract Method

| Elemento | Detalle |
|---|---|
| Problema de diseño | Problemas 2, 3 y 7 de la matriz del Lab 1: `procesar()` tomaba cinco decisiones seguidas, el `2` de la anticipación y el `40`/`0.85` eran números mágicos, y el flujo principal quedaba escondido detrás de cuatro `return 0`. |
| Código antes | Cuatro `if` anónimos en las líneas 18–35 y el cálculo en las 37–41, todo dentro del mismo método. |
| Técnica aplicada | Constantes con nombre (`HORAS_MINIMAS_ANTICIPACION`, `TARIFA_BASE`, `FACTOR_DESCUENTO_VIP`); `esProcesable()` con un predicado por regla (`tieneCorreoValido`, `tienePeriodoValido`, `cumpleAnticipacionMinima`); `calcularTotal()` separado. |
| Prueba que protege | `dosHorasDeAnticipacionPermitenProcesar()` y `unaHoraDeAnticipacionNoPermiteProcesar()` fijan la frontera inclusiva; `correoInvalidoYUnaHoraSiguenRetornando0()` fija el orden de evaluación. |
| Resultado después | `procesar()` se lee como el flujo real: si no es procesable, 0; total; guardar; notificar; confirmar. 17 verdes, salida idéntica. |
| Commit | `refactor: nombrar las reglas y descomponer las validaciones de procesar` |

## Refactorización 2 | Extract Class: `RepositorioReservas` y `NotificadorReservas`

| Elemento | Detalle |
|---|---|
| Problema de diseño | Problema 1 de la matriz: persistencia y notificación eran dos `System.out.println` dentro del método de negocio, sin punto de sustitución. Dos de las seis razones de cambio del servicio pertenecían a Infraestructura. |
| Código antes | Líneas 43–49: `System.out.println("Guardando reserva " + ...)` y `System.out.println("Correo enviado a " + ...)`. |
| Técnica aplicada | Extract Class a `infraestructura/RepositorioReservas.guardar()` y `infraestructura/NotificadorReservas.enviarConfirmacion()`, inyectadas por constructor; el constructor sin argumentos conserva el cableado por consola para `Main` y `LineaBase`. |
| Prueba que protege | `reservaValidaGuardaYNotificaEnEseOrden()` (mensajes exactos y orden guardar → notificar → confirmar) y `reservaRechazadaNoGuardaNiNotifica()`. Agregué `RepositorioReservasTest` y `NotificadorReservasTest` para el texto de cada mensaje. |
| Resultado después | El servicio solo coordina; cambiar a base de datos o correo real toca una clase cada uno. 19 verdes, salida idéntica. |
| Commit | `refactor: extraer RepositorioReservas y NotificadorReservas del servicio` |

## Refactorización 3 | Data Clumps → Value Object `PeriodoReserva`

| Elemento | Detalle |
|---|---|
| Problema de diseño | Problema 5: `inicio` y `fin` viajaban siempre juntos (constructor, getters, validación) y compartían la regla `fin > inicio`, que vivía fuera del dato. |
| Código antes | `Reserva(id, correo, inicio, fin, tipo)` con dos `LocalDateTime` sueltos; `tienePeriodoValido()` en el servicio. |
| Técnica aplicada | `record PeriodoReserva(inicio, fin)` con `esValido()`; `Reserva` recibe el periodo y expone `tienePeriodoValido()`; el servicio deja de conocer las fechas. |
| Decisión de alcance | El constructor **no lanza excepción** ante un periodo inválido. El contrato heredado (LB-04) exige retornar 0, y convertirlo en excepción sería un cambio funcional, no una refactorización. La invariante está en un solo lugar; el canal de rechazo se decide aparte. |
| Prueba que protege | `periodoConFinIgualAlInicioRetorna0YNoConfirma()`, `periodoConFinAnteriorAlInicioRetorna0YNoConfirma()` y `PeriodoReservaTest` (4 casos). |
| Resultado después | Cinco parámetros pasan a cuatro; la regla del periodo tiene dueño. 23 verdes, salida idéntica. |
| Commit | `refactor: agrupar inicio y fin en el objeto de valor PeriodoReserva` |

## Refactorización 4 | Value Object `Correo`

| Elemento | Detalle |
|---|---|
| Problema de diseño | Problema 6: la regla del correo (`contains("@")`) era una regla del dato implementada en el servicio, con Shotgun Surgery latente. |
| Código antes | `String correo` en `Reserva`; `tieneCorreoValido()` en el servicio comparando el `String`. |
| Técnica aplicada | `record Correo(valor)` con `esValido()` y `toString()` que devuelve el valor (así el mensaje `Correo enviado a …` no cambia); `Reserva.tieneCorreoValido()` delega. |
| Decisión de alcance | La regla se conserva **exactamente** como era, incluido lo que acepta (`"a@"`). Endurecerla es un cambio funcional. |
| Prueba que protege | `correoSinArrobaRetorna0YNoConfirma()`, `correoNuloRetorna0YNoConfirma()`, `correoConSoloArrobaSigueSiendoAceptado()` y `CorreoTest` (5 casos). |
| Resultado después | La validez del correo tiene un solo dueño reutilizable. 28 verdes, salida idéntica. |
| Commit | `refactor: introducir Correo como objeto de valor` |

## Refactorización 5 | Extract Class `PoliticaPrecios` + Move Method `Reserva.esVip()`

| Elemento | Detalle |
|---|---|
| Problema de diseño | Problema 4 y la razón de cambio n.º 4 (Finanzas): la tarifa y el descuento vivían en el servicio y el servicio interpretaba el `String tipo`. |
| Código antes | `calcularTotal()` privado en el servicio con `"VIP".equals(reserva.getTipo())`. |
| Técnica aplicada | Extract Class a `PoliticaPrecios.calcularTotal()`, inyectada por constructor; Move Method: `Reserva.esVip()` encapsula la comparación exacta sin alterarla. |
| Decisión de alcance | No introduje el enum `TipoReserva`: obligaría a decidir qué pasa con `"vip"` y `"PREMIUM"` (LB-08 y LB-09), y esa decisión es funcional. |
| Prueba que protege | `reservaVipValidaRetorna34YQuedaConfirmada()`, `tipoVipEnMinusculaNoAplicaDescuento()`, `tipoDesconocidoCobraTarifaBase()` y `PoliticaPreciosTest` (3 casos). |
| Resultado después | `ServicioReservas` conserva una sola regla propia (la anticipación) y coordina tres colaboradores. 31 verdes, salida idéntica. |
| Commit | `refactor: extraer PoliticaPrecios y mover la interpretacion del tipo a Reserva` |

## Comparación antes / después

| Dimensión | Antes (`main`) | Después (`ae5-refactorizacion`) | Evidencia |
|---|---|---|---|
| Responsabilidades | `procesar()` valida, calcula, persiste, notifica y confirma; seis razones de cambio | El servicio coordina; correo, periodo, precio, persistencia y notificación tienen clase propia; dos razones de cambio (flujo y anticipación) | `codigo-inicial/` vs `codigo-final/` |
| Cohesión | Una clase de negocio con `println` de infraestructura | `domain`, `service` e `infraestructura` separados; 4 clases → 8 | `git diff --stat main..ae5-refactorizacion` |
| Acoplamiento | El servicio conocía `String`, `LocalDateTime`, consola y tarifa | El servicio depende de `Reserva` y de tres colaboradores inyectables | `ServicioReservas.java` final |
| Datos del dominio | `String correo`, `inicio`+`fin` sueltos, reglas en el servicio | `Correo` y `PeriodoReserva` con su propia invariante; `Reserva.esVip()` | `Correo.java`, `PeriodoReserva.java` |
| Condicionales | 5 `if` y 2 números mágicos en `procesar()` (30 líneas) | 1 `if` en `procesar()` (11 líneas); reglas con nombre | métricas en el informe |
| Pruebas | 0 | 31 pruebas AAA en 7 clases; suite verde en cada paso | `tests-00…05.txt` |
| Comportamiento | LB-01…LB-09 | LB-01…LB-09, mismo MD5 en las siete ejecuciones | `comparacion-salidas.txt` |
| Git | 11 commits de diagnóstico | + 9 commits incrementales: 1 chore, 1 test, 5 refactor, 2 docs | `git log --oneline main..ae5-refactorizacion` |

## Lo que dejé fuera a propósito

- **Distinguir la causa del rechazo** (excepciones o `Result`): cambia el contrato.
- **Enum `TipoReserva`**: exige decidir el destino de `"vip"` y `"PREMIUM"`.
- **Strategy para precios**: con un solo descuento sería un patrón sin necesidad.
- **Mocks**: los colaboradores ya son inyectables, así que el terreno queda
  preparado, pero las pruebas actuales verifican la consola real.

## Costo de las decisiones

La inyección por constructor agrega un constructor de tres argumentos y obliga a
leer cuatro clases para entender lo que antes estaba en una. Es el precio de
que cada razón de cambio tenga un solo lugar. `PeriodoReserva` y `Correo` sin
excepción en el constructor son objetos de valor «débiles»: aceptan existir en
estado inválido para no romper el contrato de retorno 0. Esa es la deuda que
elegí conservar de forma explícita en lugar de resolverla a escondidas.
