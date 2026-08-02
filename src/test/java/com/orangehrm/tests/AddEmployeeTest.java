package com.orangehrm.tests;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.pages.AddEmployeePage;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.PersonalDetailsPage;
import com.orangehrm.utils.TestImageGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Add Employee Tests")
public class AddEmployeeTest extends BaseTest {

    private static final String FIRST_NAME  = "Carlos";
    private static final String MIDDLE_NAME = "Andres";
    private static final String LAST_NAME   = "Gomez";
    private static final String PHOTO_PATH  = "src/test/resources/testdata/profile_photo.png";

    @Test
    @DisplayName("TC03 - Agregar nuevo empleado con informacion basica")
    public void testAddNewEmployee() {
        logStep("Iniciando sesion como administrador");
        DashboardPage dashboard = new LoginPage().loginAs(
                ConfigReader.get("username"),
                ConfigReader.get("password")
        );
        assertTrue(dashboard.isLoaded(), "El dashboard no se cargo");

        logStep("Navegando al modulo PIM");
        AddEmployeePage addEmployeePage = dashboard.navigateToPim().clickAddEmployee();
        assertTrue(addEmployeePage.isLoaded(), "La pagina Add Employee no se cargo");

        logStep("Completando formulario con datos del empleado");
        addEmployeePage
                .enterFirstName(FIRST_NAME)
                .enterMiddleName(MIDDLE_NAME)
                .enterLastName(LAST_NAME);

        logStep("Guardando el empleado");
        PersonalDetailsPage personalDetails = addEmployeePage.saveEmployee();

        logStep("Validando que los datos del empleado se guardaron correctamente");
        assertEquals(FIRST_NAME, personalDetails.getFirstName(),
                "El nombre del empleado no coincide");
        assertEquals(MIDDLE_NAME, personalDetails.getMiddleName(),
                "El segundo nombre del empleado no coincide");
        assertEquals(LAST_NAME, personalDetails.getLastName(),
                "El apellido del empleado no coincide");

        logPass("Empleado creado correctamente: " + FIRST_NAME + " " + MIDDLE_NAME + " " + LAST_NAME);
    }

    @Test
    @DisplayName("TC04 - Agregar empleado y subir foto de perfil")
    public void testAddEmployeeWithProfilePhoto() {
        logStep("Generando imagen de prueba para foto de perfil");
        TestImageGenerator.generate(PHOTO_PATH);

        logStep("Iniciando sesion como administrador");
        DashboardPage dashboard = new LoginPage().loginAs(
                ConfigReader.get("username"),
                ConfigReader.get("password")
        );

        logStep("Navegando al modulo PIM - Add Employee");
        AddEmployeePage addEmployeePage = dashboard.navigateToPim().clickAddEmployee();

        logStep("Completando datos del empleado");
        addEmployeePage
                .enterFirstName(FIRST_NAME)
                .enterMiddleName(MIDDLE_NAME)
                .enterLastName(LAST_NAME);

        logStep("Subiendo foto de perfil");
        addEmployeePage.uploadProfilePhoto(PHOTO_PATH);

        logStep("Guardando el empleado");
        PersonalDetailsPage personalDetails = addEmployeePage.saveEmployee();

        logStep("Validando datos guardados");
        assertEquals(FIRST_NAME, personalDetails.getFirstName(), "El nombre no coincide");
        assertEquals(LAST_NAME, personalDetails.getLastName(), "El apellido no coincide");

        logPass("Empleado creado con foto de perfil: " + FIRST_NAME + " " + LAST_NAME);
    }
}
