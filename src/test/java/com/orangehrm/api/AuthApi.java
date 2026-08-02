package com.orangehrm.api;

import com.orangehrm.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;

public class AuthApi {

    public static Cookies getSessionCookies() {
        // 1. GET a la pagina de login para obtener la cookie inicial de sesion
        Cookies initialCookies = RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .redirects().follow(false)
                .when()
                    .get("/web/index.php/auth/login")
                .then()
                    .extract().response().getDetailedCookies();

        // 2. POST con credenciales usando la cookie inicial
        Response loginResponse = RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(initialCookies)
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("_token", getToken(initialCookies))
                    .formParam("username", ConfigReader.get("username"))
                    .formParam("password", ConfigReader.get("password"))
                    .redirects().follow(true)
                .when()
                    .post("/web/index.php/auth/validate");

        return loginResponse.getDetailedCookies();
    }

    public static Response getSessionCookiesResponseWithInvalidCredentials() {
        Cookies initialCookies = RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .redirects().follow(false)
                .when()
                    .get("/web/index.php/auth/login")
                .then()
                    .extract().response().getDetailedCookies();

        return RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(initialCookies)
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("_token", getToken(initialCookies))
                    .formParam("username", "invalid_user")
                    .formParam("password", "wrong_password")
                    .redirects().follow(false)
                .when()
                    .post("/web/index.php/auth/validate");
    }

    private static String getToken(Cookies cookies) {
        Response loginPage = RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(cookies)
                .when()
                    .get("/web/index.php/auth/login");

        String body = loginPage.getBody().asString();
        // El token  esta en el atributo del componente Vue: token="&quot;VALOR&quot;"
        String marker = "token=\"&quot;";
        int start = body.indexOf(marker);
        if (start == -1) return "";
        start += marker.length();
        int end = body.indexOf("&quot;\"", start);
        if (end == -1) return "";
        return body.substring(start, end);
    }
}
