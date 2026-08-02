package com.orangehrm.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Employee API Tests")
public class EmployeeApiTest extends ApiBaseTest {

    @Test
    @DisplayName("TC09 - Listar empleados devuelve lista no vacia con status 200")
    void testGetAllEmployees() {
        logRequest("GET", "/web/index.php/api/v2/pim/employees");

        logStep("Ejecutando GET de todos los empleados");
        Response response = employeeApi.getAll();
        logResponse(response);

        logStep("Validando respuesta");
        logValidation("Status code es 200", response.statusCode() == 200);
        assertEquals(200, response.statusCode(), "El status code debe ser 200");

        int total = response.jsonPath().getList("data").size();
        logValidation("Lista 'data' no esta vacia (" + total + " registros)", total > 0);
        assertTrue(total > 0, "La lista de empleados no debe estar vacia");

        int totalMeta = response.jsonPath().getInt("meta.total");
        logStep("Total de empleados en el sistema: " + totalMeta);
        logPass("Listado de empleados obtenido correctamente - " + total + " registros en pagina");
    }

    @Test
    @DisplayName("TC10 - Crear empleado via API devuelve status 200 y datos correctos")
    void testCreateEmployee() {
        String firstName  = "ApiTest";
        String middleName = "";
        String lastName   = "User" + System.currentTimeMillis();

        logRequest("POST", "/web/index.php/api/v2/pim/employees");
        logStep("Payload: firstName=" + firstName + ", lastName=" + lastName);

        Response response = employeeApi.create(firstName, middleName, lastName);
        logResponse(response);

        logStep("Validando respuesta de creacion");
        logValidation("Status code es 200", response.statusCode() == 200);
        assertEquals(200, response.statusCode(), "El status code al crear empleado debe ser 200");

        String returnedFirst = response.jsonPath().getString("data.firstName");
        String returnedLast  = response.jsonPath().getString("data.lastName");
        int empNumber        = response.jsonPath().getInt("data.empNumber");

        logValidation("firstName retornado coincide: '" + returnedFirst + "'", firstName.equals(returnedFirst));
        logValidation("lastName retornado coincide: '" + returnedLast + "'", lastName.equals(returnedLast));
        logValidation("empNumber asignado: " + empNumber, empNumber > 0);

        assertEquals(firstName, returnedFirst, "El firstName retornado debe coincidir con el enviado");
        assertEquals(lastName, returnedLast, "El lastName retornado debe coincidir con el enviado");
        assertNotNull(response.jsonPath().get("data.empNumber"), "El empNumber no debe ser nulo");

        logPass("Empleado creado correctamente con empNumber: " + empNumber);
    }

    @Test
    @DisplayName("TC11 - Obtener empleado por ID devuelve datos correctos")
    void testGetEmployeeById() {
        logStep("Creando empleado previo para obtener un ID valido");
        String lastName = "ById" + System.currentTimeMillis();
        Response created = employeeApi.create("ApiById", "", lastName);
        assertEquals(200, created.statusCode(), "Creacion previa del empleado fallo");
        int empNumber = created.jsonPath().getInt("data.empNumber");
        logStep("Empleado creado con empNumber: " + empNumber);

        logRequest("GET", "/web/index.php/api/v2/pim/employees/" + empNumber);
        Response response = employeeApi.getById(empNumber);
        logResponse(response);

        logStep("Validando datos del empleado obtenido");
        logValidation("Status code es 200", response.statusCode() == 200);
        assertEquals(200, response.statusCode(), "El status code al buscar por ID debe ser 200");

        int returnedEmpNumber = response.jsonPath().getInt("data.empNumber");
        String returnedFirst  = response.jsonPath().getString("data.firstName");

        logValidation("empNumber coincide: " + returnedEmpNumber, returnedEmpNumber == empNumber);
        logValidation("firstName coincide: '" + returnedFirst + "'", "ApiById".equals(returnedFirst));

        assertEquals(empNumber, returnedEmpNumber, "El empNumber retornado debe coincidir con el buscado");
        assertEquals("ApiById", returnedFirst, "El firstName debe coincidir");

        logPass("Empleado obtenido por ID correctamente: empNumber=" + empNumber);
    }

    @Test
    @DisplayName("TC12 - Buscar empleado por nombre devuelve resultados filtrados")
    void testSearchEmployeeByName() {
        String uniqueName = "SearchApi" + System.currentTimeMillis();
        logStep("Creando empleado con nombre unico para busqueda: " + uniqueName);
        Response created = employeeApi.create(uniqueName, "", "TestSearch");
        assertEquals(200, created.statusCode(), "Creacion previa del empleado fallo");

        logRequest("GET", "/web/index.php/api/v2/pim/employees?nameOrId=" + uniqueName);
        Response response = employeeApi.searchByName(uniqueName);
        logResponse(response);

        logStep("Validando resultados de busqueda");
        logValidation("Status code es 200", response.statusCode() == 200);
        assertEquals(200, response.statusCode(), "El status code de busqueda debe ser 200");

        int resultCount = response.jsonPath().getList("data").size();
        logValidation("Al menos un resultado encontrado (" + resultCount + ")", resultCount > 0);
        assertTrue(resultCount > 0, "La busqueda por nombre debe retornar al menos un resultado");

        String foundFirst = response.jsonPath().getString("data[0].firstName");
        logValidation("Nombre encontrado contiene el termino buscado: '" + foundFirst + "'",
                foundFirst != null && foundFirst.contains(uniqueName));
        assertTrue(foundFirst != null && foundFirst.contains(uniqueName),
                "El nombre encontrado debe contener el termino buscado");

        logPass("Busqueda por nombre exitosa - " + resultCount + " resultado(s) para '" + uniqueName + "'");
    }
}
