package com.orangehrm.performance.simulations;

import com.orangehrm.config.ConfigReader;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class EmployeeApiSimulation extends Simulation {

    private static final String BASE_URL  = ConfigReader.get("api.base.url");
    private static final String USERNAME  = ConfigReader.get("username");
    private static final String PASSWORD  = ConfigReader.get("password");

    private static final String EMPLOYEES_URL = "/web/index.php/api/v2/pim/employees";

    private static final int USERS        = 15;
    private static final int RAMP_SECS    = 10;
    private static final int SUSTAIN_SECS = 20;

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Gatling/OrangeHRM-PerformanceTest");

    ScenarioBuilder employeeScenario = scenario("Employee API Flow")

            // 1. Obtener token de la página de login
            .exec(
                http("GET - Login Page")
                    .get("/web/index.php/auth/login")
                    .check(
                        status().is(200),
                        regex("token=\"&quot;([^&]+)&quot;\"").saveAs("csrfToken")
                    )
            )
            .pause(1)

            // 2. Autenticarse (las cookies de sesión las mantiene Gatling automáticamente)
            .exec(
                http("POST - Login")
                    .post("/web/index.php/auth/validate")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .formParam("_token", "#{csrfToken}")
                    .formParam("username", USERNAME)
                    .formParam("password", PASSWORD)
                    .check(status().is(200))
            )
            .pause(1)

            // 3. Listar empleados y extraer el ID del primero
            .exec(
                http("GET - All Employees")
                    .get(EMPLOYEES_URL)
                    .check(
                        status().is(200),
                        jsonPath("$.data[0].empNumber").optional().saveAs("empNumber")
                    )
            )
            .pause(1)

            // 4. Consultar el empleado por ID solo si se encontró uno
            .doIf(session -> session.contains("empNumber")).then(
                exec(
                    http("GET - Employee by ID")
                        .get(EMPLOYEES_URL + "/#{empNumber}")
                        .check(status().is(200))
                )
            )
            .pause(1)

            // 5. Buscar empleado por nombre
            .exec(
                http("GET - Search Employee by Name")
                    .get(EMPLOYEES_URL)
                    .queryParam("nameOrId", "Admin")
                    .check(status().is(200))
            );

    {
        setUp(
            employeeScenario.injectOpen(
                rampUsers(USERS).during(RAMP_SECS),
                constantUsersPerSec(1).during(SUSTAIN_SECS)
            )
        )
        .protocols(httpProtocol)
        .assertions(
            global().responseTime().percentile(95).lt(2000),
            global().failedRequests().percent().lt(5.0)
        );
    }
}
