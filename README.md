# Automation OrangeHRM

Suite de automatización para [OrangeHRM Demo](https://opensource-demo.orangehrmlive.com) que cubre pruebas web (Selenium), pruebas de API (RestAssured) y pruebas de performance (Gatling).

**Stack:** Java 11 · Maven · JUnit 5 · Selenium 4 · RestAssured · Gatling 3.9

---

## Requisitos previos

- Java 11 o superior
- Maven 3.6 o superior
- Google Chrome instalado (para pruebas web)

---

## Configuración

Las credenciales y URLs se configuran en `src/test/resources/config.properties`:

```properties
base.url=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
api.base.url=https://opensource-demo.orangehrmlive.com
username=Admin
password=admin123
browser=chrome
```

---

## Sección 1 — Pruebas Web

Automatizan flujos de usuario completos en el navegador usando Selenium WebDriver.

### Casos de prueba

| ID    | Clase              | Descripción                                          |
|-------|--------------------|------------------------------------------------------|
| TC01  | `LoginTest`        | Login exitoso con credenciales válidas               |
| TC02  | `LoginTest`        | Login fallido con credenciales inválidas             |
| TC03  | `AddEmployeeTest`  | Agregar nuevo empleado con información básica        |
| TC04  | `AddEmployeeTest`  | Agregar empleado y subir foto de perfil              |
| TC05  | `DirectoryTest`    | Buscar empleado por nombre en el módulo Directory    |
| TC06  | `DirectoryTest`    | Búsqueda sin filtro muestra todos los empleados      |

### Cómo ejecutar

```bash
# Todos los tests web
mvn test -Dtest="LoginTest,AddEmployeeTest,DirectoryTest"

# Una clase específica
mvn test -Dtest=LoginTest

# Un caso específico
mvn test -Dtest=LoginTest#testSuccessfulLogin
```

### Reportes

Los reportes HTML de ExtentReports se generan en `reports/` al finalizar la ejecución.

---

## Sección 2 — Pruebas de API

Validan los endpoints REST de OrangeHRM usando RestAssured. No requieren navegador.

### Casos de prueba

| ID    | Clase              | Método HTTP | Endpoint                              | Descripción                                      |
|-------|--------------------|-------------|---------------------------------------|--------------------------------------------------|
| TC07  | `AuthApiTest`      | POST        | `/auth/validate`                      | Login exitoso establece sesión válida            |
| TC08  | `AuthApiTest`      | POST        | `/auth/validate`                      | Login fallido con credenciales inválidas         |
| TC09  | `EmployeeApiTest`  | GET         | `/api/v2/pim/employees`               | Listar empleados devuelve lista no vacía         |
| TC10  | `EmployeeApiTest`  | POST        | `/api/v2/pim/employees`               | Crear empleado vía API y validar datos           |
| TC11  | `EmployeeApiTest`  | GET         | `/api/v2/pim/employees/{id}`          | Obtener empleado por ID devuelve datos correctos |
| TC12  | `EmployeeApiTest`  | GET         | `/api/v2/pim/employees?nameOrId=...`  | Buscar empleado por nombre filtra correctamente  |

### Cómo ejecutar

```bash
# Todos los tests de API
mvn test -Dtest="AuthApiTest,EmployeeApiTest"

# Una clase específica
mvn test -Dtest=EmployeeApiTest

# Un caso específico
mvn test -Dtest=EmployeeApiTest#testCreateEmployee
```

---

## Sección 3 — Pruebas de Performance

Simulan múltiples usuarios concurrentes usando Gatling para medir tiempos de respuesta y estabilidad bajo carga.

### Simulaciones disponibles

| Simulación               | Usuarios | Ramp    | Sostenido | Límite p95 |
|--------------------------|----------|---------|-----------|------------|
| `LoginSimulation`        | 10       | 10 seg  | 30 seg    | < 3000 ms  |
| `EmployeeApiSimulation`  | 15       | 10 seg  | 20 seg    | < 2000 ms  |

**LoginSimulation** — prueba el flujo de autenticación bajo carga:
1. `GET /auth/login` — obtiene la página y extrae el token CSRF
2. `POST /auth/validate` — envía credenciales y establece sesión

**EmployeeApiSimulation** — prueba el flujo completo de la API de empleados:
1. `GET /auth/login` — extrae token CSRF
2. `POST /auth/validate` — establece sesión
3. `GET /api/v2/pim/employees` — lista todos los empleados
4. `GET /api/v2/pim/employees/{id}` — consulta empleado por ID
5. `GET /api/v2/pim/employees?nameOrId=Admin` — busca por nombre

### Cómo ejecutar

```bash
# Ejecutar todas las simulaciones
mvn gatling:test

# Ejecutar solo LoginSimulation
mvn gatling:test -Dgatling.simulationClass=com.orangehrm.performance.simulations.LoginSimulation

# Ejecutar solo EmployeeApiSimulation
mvn gatling:test -Dgatling.simulationClass=com.orangehrm.performance.simulations.EmployeeApiSimulation
```

Los reportes HTML se generan en `target/gatling/<nombre-simulacion>-<timestamp>/index.html`.

### Cómo leer los resultados

Al terminar cada simulación, Gatling imprime un resumen en consola y genera un reporte HTML.

#### Tabla de métricas globales

| Métrica    | Descripción                                                                                      |
|------------|--------------------------------------------------------------------------------------------------|
| Total      | Número total de requests ejecutados por todos los usuarios                                        |
| OK         | Requests que respondieron correctamente                                                           |
| KO         | Requests fallidos (error HTTP, timeout o validación que no pasó)                                 |
| Min        | El tiempo de respuesta más rápido registrado                                                     |
| p50        | Percentil 50 — la mitad de los usuarios recibió respuesta en este tiempo o menos                 |
| p75        | Percentil 75 — 3 de cada 4 usuarios recibieron respuesta en este tiempo o menos                  |
| **p95**    | **Percentil 95 — el indicador más importante. El 95% de los usuarios no esperó más de esto**     |
| p99        | Percentil 99 — detecta picos extremos que afectan a muy pocos usuarios                           |
| Max        | El tiempo de respuesta más lento registrado (puede ser un caso aislado)                          |
| Mean       | Promedio general de todos los tiempos de respuesta                                               |
| Std Dev    | Desviación estándar — qué tan estables son los tiempos. Más bajo = más consistente              |
| Throughput | Requests por segundo que el sistema procesó durante la prueba                                    |

> **¿Por qué p95 y no el máximo?**
> El máximo puede dispararse por un caso aislado (red inestable, GC pause) sin que el sistema tenga un problema real.
> El p95 refleja la experiencia del 95% de tus usuarios reales — es el indicador estándar de la industria.

#### Ejemplo de resultado — EmployeeApiSimulation (15 usuarios)

| Métrica    | Valor         | Análisis                                          |
|------------|---------------|---------------------------------------------------|
| Total      | 210           | 35 usuarios × 6 requests por escenario            |
| OK         | 210           | 0 errores                                         |
| KO         | 0             | sin fallos                                        |
| Min        | 323 ms        | el request más rápido                             |
| p50        | 982 ms        | la mitad de los usuarios esperó menos de 1 seg    |
| p75        | 1233 ms       | 3 de cada 4 usuarios esperaron menos de 1.2 seg   |
| **p95**    | **1600 ms**   | **cumple el límite de 2000 ms** ✅                |
| p99        | 1904 ms       | cerca del límite — señal de presión               |
| Max        | 2422 ms       | pico aislado, no representativo                   |
| Mean       | 1015 ms       | promedio general de 1 segundo                     |
| Std Dev    | 346 ms        | variabilidad moderada bajo carga concurrente      |
| Throughput | 5.676 req/seg | ritmo de procesamiento sostenido                  |

#### Distribución de tiempos

Gatling agrupa los tiempos en tres rangos para una lectura rápida:

| Rango           | Cantidad | Porcentaje | Interpretación                       |
|-----------------|----------|------------|--------------------------------------|
| t < 800 ms      | 60       | 29%        | Respuestas rápidas                   |
| 800 ms–1200 ms  | 92       | 44%        | Zona media — aceptable               |
| t >= 1200 ms    | 58       | 28%        | Respuestas lentas — hay presión      |
| Failed          | 0        | 0%         | Sin errores ✅                       |

#### Señales de alerta en los resultados

| Señal                                    | Qué significa                                              |
|------------------------------------------|------------------------------------------------------------|
| KO > 0                                   | Hay errores reales — revisar qué request falla             |
| p95 cercano o mayor al límite definido   | El sistema está al límite de su capacidad                  |
| Max muy alejado del p99                  | Hay timeouts o casos extremos que investigar               |
| Std Dev alta                             | Los tiempos son muy variables — sistema inestable          |
| Assertion en `false`                     | La prueba falla — no cumple el SLA definido                |
| % en `t >= 1200ms` superior al 30%       | Señal de degradación bajo la carga actual                  |
