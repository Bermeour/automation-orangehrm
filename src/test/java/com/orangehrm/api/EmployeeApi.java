package com.orangehrm.api;

import com.orangehrm.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class EmployeeApi {

    private static final String EMPLOYEES_ENDPOINT = "/web/index.php/api/v2/pim/employees";
    private final Cookies cookies;

    public EmployeeApi(Cookies cookies) {
        this.cookies = cookies;
    }

    public Response getAll() {
        return RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(cookies)
                    .contentType("application/json")
                .when()
                    .get(EMPLOYEES_ENDPOINT);
    }

   /* public Response getAll(int limit, int offset) {
        return RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(cookies)
                    .contentType("application/json")
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                .when()
                    .get(EMPLOYEES_ENDPOINT);
    }*/

    public Response getById(int empNumber) {
        return RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(cookies)
                    .contentType("application/json")
                .when()
                    .get(EMPLOYEES_ENDPOINT + "/" + empNumber);
    }

    public Response create(String firstName, String middleName, String lastName) {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", firstName);
        body.put("middleName", middleName);
        body.put("lastName", lastName);

        return RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(cookies)
                    .contentType("application/json")
                    .body(body)
                .when()
                    .post(EMPLOYEES_ENDPOINT);
    }

    public Response searchByName(String name) {
        return RestAssured
                .given()
                    .baseUri(ConfigReader.get("api.base.url"))
                    .cookies(cookies)
                    .contentType("application/json")
                    .queryParam("nameOrId", name)
                .when()
                    .get(EMPLOYEES_ENDPOINT);
    }
}
