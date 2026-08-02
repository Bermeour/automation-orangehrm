package com.orangehrm.tests;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.pages.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Directory Tests")
public class DirectoryTest extends BaseTest {

    // Nombre unico por ejecucion para evitar colisiones con datos de otros tests
    private static final String FIRST_NAME = "Test" + System.currentTimeMillis();
    private static final String LAST_NAME  = "AutoDir";

    @Test
    @DisplayName("TC05 - Buscar empleado por nombre en el modulo Directory")
    public void testSearchEmployeeInDirectory() {
        logStep("Iniciando sesion como administrador");
        DashboardPage dashboard = new LoginPage().loginAs(
                ConfigReader.get("username"),
                ConfigReader.get("password")
        );

        logStep("Creando empleado unico de prueba: " + FIRST_NAME + " " + LAST_NAME);
        AddEmployeePage addPage = dashboard.navigateToPim().clickAddEmployee();
        addPage.enterFirstName(FIRST_NAME)
               .enterLastName(LAST_NAME)
               .saveEmployee();

        logStep("Esperando sincronizacion PIM -> Directory");
        //se que no es buena idea el uso
        try {Thread.sleep(3000);} catch (InterruptedException ignored) {}

        logStep("Navegando al modulo Directory");
        DirectoryPage directoryPage = new DashboardPage().navigateToDirectory();
        assertTrue(directoryPage.isLoaded(), "El modulo Directory no se cargo");

        logStep("Buscando empleado por nombre: " + FIRST_NAME);
        directoryPage.searchByEmployeeName(FIRST_NAME);

        logStep("Validando que el empleado aparece en los resultados");
        assertTrue(directoryPage.getResultCount() > 0,
                "No se encontraron resultados para: " + FIRST_NAME);

        logStep("Validando que el nombre coincide en la tarjeta del directorio");
        assertTrue(directoryPage.containsEmployee(FIRST_NAME, LAST_NAME),
                "El empleado '" + FIRST_NAME + " " + LAST_NAME + "' no aparece en el directorio");

        logPass("Empleado encontrado correctamente en Directory: " + FIRST_NAME + " " + LAST_NAME);
    }

    @Test
    @DisplayName("TC06 - Busqueda sin filtro muestra todos los empleados")
    public void testSearchWithoutFilterShowsAllEmployees() {
        logStep("Iniciando sesion como administrador");
        DashboardPage dashboard = new LoginPage().loginAs(
                ConfigReader.get("username"),
                ConfigReader.get("password")
        );

        logStep("Navegando al modulo Directory");
        DirectoryPage directoryPage = dashboard.navigateToDirectory();
        assertTrue(directoryPage.isLoaded(), "El modulo Directory no se cargo");

        logStep("Ejecutando busqueda sin filtros");
        directoryPage.searchByEmployeeName("");

        logStep("Validando que se muestran empleados");
        int count = directoryPage.getResultCount();
        assertTrue(count > 0, "No se muestran empleados al buscar sin filtros");

        logPass("Busqueda sin filtro muestra " + count + " empleados correctamente");
    }
}
