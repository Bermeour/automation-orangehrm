package com.orangehrm.performance.simulations;

import com.orangehrm.config.ConfigReader;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoginSimulation extends Simulation {

    private static final String BASE_URL  = ConfigReader.get("api.base.url");
    private static final String USERNAME  = ConfigReader.get("username");
    private static final String PASSWORD  = ConfigReader.get("password");

    // Número de usuarios y tiempos configurables
    private static final int USERS       = 10;
    private static final int RAMP_SECS   = 10;
    private static final int SUSTAIN_SECS = 30;

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Gatling/OrangeHRM-PerformanceTest");

    // El token CSRF aparece como: token="&quot;VALOR&quot;" en el HTML
    ScenarioBuilder loginScenario = scenario("Login Flow")
            .exec(
                http("GET - Login Page")
                    .get("/web/index.php/auth/login")
                    .check(
                        status().is(200),
                        regex("token=\"&quot;([^&]+)&quot;\"").saveAs("csrfToken")
                    )
            )
            .pause(1)
            .exec(
                http("POST - Validate Credentials")
                    .post("/web/index.php/auth/validate")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .formParam("_token", "#{csrfToken}")
                    .formParam("username", USERNAME)
                    .formParam("password", PASSWORD)
                    .check(status().is(200))
            );

    {
        setUp(
            loginScenario.injectOpen(
                rampUsers(USERS).during(RAMP_SECS),
                constantUsersPerSec(2).during(SUSTAIN_SECS)
            )
        )
        .protocols(httpProtocol)
        .assertions(
            global().responseTime().percentile(95).lt(3000),
            global().failedRequests().percent().lt(5.0)
        );
    }
}
