package com.orangehrm.api;

import com.aventstack.extentreports.Status;
import com.orangehrm.config.ConfigReader;
import com.orangehrm.utils.ExtentManager;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public class ApiBaseTest {

    protected static Cookies sessionCookies;
    protected static EmployeeApi employeeApi;

    @BeforeAll
    static void setUpApi() {
        RestAssured.baseURI = ConfigReader.get("api.base.url");
        sessionCookies = AuthApi.getSessionCookies();
        employeeApi = new EmployeeApi(sessionCookies);
    }

    @BeforeEach
    void setUpTest(TestInfo testInfo) {
        ExtentManager.setTest(
                ExtentManager.getInstance().createTest(testInfo.getDisplayName())
        );
        logStep("Iniciando test de API: " + testInfo.getDisplayName());
    }

    @AfterEach
    void tearDownTest() {
        // nada que cerrar en API tests
    }

    @AfterAll
    static void generateReport() {
        ExtentManager.flush();
    }

    protected void logStep(String message) {
        ExtentManager.getTest().log(Status.INFO, message);
    }

    protected void logPass(String message) {
        ExtentManager.getTest().log(Status.PASS, message);
    }

    protected void logFail(String message) {
        ExtentManager.getTest().log(Status.FAIL, message);
    }

    protected void logRequest(String method, String endpoint) {
        ExtentManager.getTest().log(Status.INFO,
                "<b>REQUEST:</b> " + method + " " + ConfigReader.get("api.base.url") + endpoint);
    }

    protected void logResponse(Response response) {
        String contentType = response.contentType() != null ? response.contentType() : "";
        String body;

        // Si el response es HTML (ej: pagina de login al fallar auth), no lo embebemos
        if (contentType.contains("text/html")) {
            body = "[Respuesta HTML - contenido omitido para evitar interferencias en el reporte]";
        } else {
            body = escapeHtml(response.getBody().asPrettyString());
            if (body.length() > 2000) {
                body = body.substring(0, 2000) + "\n... [truncado]";
            }
        }

        ExtentManager.getTest().log(Status.INFO,
                "<b>RESPONSE STATUS:</b> " + response.statusCode()
                + " | <b>Content-Type:</b> " + contentType
                + "<br><b>BODY:</b><pre>" + body + "</pre>");
    }

    private String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    protected void logValidation(String description, boolean passed) {
        if (passed) {
            ExtentManager.getTest().log(Status.PASS, "✔ " + description);
        } else {
            ExtentManager.getTest().log(Status.FAIL, "✘ " + description);
        }
    }
}
