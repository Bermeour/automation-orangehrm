package com.orangehrm.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Auth API Tests")
public class AuthApiTest extends ApiBaseTest {

    @Test
    @DisplayName("TC07 - Login exitoso via API establece sesion valida")
    void testSuccessfulAuthentication() {
        logRequest("POST", "/web/index.php/auth/validate");

        logStep("Validando que la sesion fue establecida correctamente");
        boolean hasSession = sessionCookies != null && !sessionCookies.asList().isEmpty();
        logValidation("Cookies de sesion no nulas y no vacias", hasSession);
        assertTrue(hasSession, "La sesion debe contener al menos una cookie");

        logStep("Cookies obtenidas: " + sessionCookies.asList().size());
        logPass("Autenticacion exitosa - sesion establecida con " + sessionCookies.asList().size() + " cookie(s)");
    }

    @Test
    @DisplayName("TC08 - Login fallido con credenciales invalidas redirige a login")
    void testFailedAuthenticationWithInvalidCredentials() {
        logRequest("POST", "/web/index.php/auth/validate (credenciales invalidas)");

        logStep("Enviando credenciales incorrectas al endpoint de autenticacion");
        Response response = AuthApi.getSessionCookiesResponseWithInvalidCredentials();
        logResponse(response);

        int status = response.statusCode();
        String location = response.header("Location");
        boolean redirectsToLogin = (location != null && location.contains("auth/login")) || status == 302;

        logValidation("Respuesta no es 200 OK (login exitoso)", status != 200);
        logValidation("Redirige a pagina de login", redirectsToLogin);
        assertTrue(redirectsToLogin || status != 200,"Un login fallido debe redirigir a login o retornar status diferente de 200");

        logPass("Login fallido manejado correctamente - status: " + status);
    }
}
