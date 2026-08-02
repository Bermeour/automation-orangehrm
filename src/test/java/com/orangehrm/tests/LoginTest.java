package com.orangehrm.tests;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Login Tests")
public class LoginTest extends BaseTest {

    @Test
    @DisplayName("TC01 - Login exitoso con credenciales validas")
    public void testSuccessfulLogin() {
        logStep("Ingresando credenciales de administrador");
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboard = loginPage.loginAs(
                ConfigReader.get("username"),
                ConfigReader.get("password")
        );

        logStep("Validando que el Dashboard se cargue correctamente");
        assertTrue(dashboard.isLoaded(), "El dashboard no se cargo tras el login");
        logPass("Login exitoso - Dashboard visible");
    }

    @Test
    @DisplayName("TC02 - Login fallido con credenciales invalidas")
    public void testFailedLoginWithInvalidCredentials() {
        logStep("Ingresando credenciales incorrectas");
        LoginPage loginPage = new LoginPage();
        loginPage.enterUsername("invalid_user")
                 .enterPassword("wrong_password")
                 .clickLogin();

        logStep("Validando mensaje de error");
        String errorMsg = loginPage.getErrorMessage();
        assertFalse(errorMsg.isEmpty(), "No se mostro mensaje de error con credenciales invalidas");
        logPass("Mensaje de error mostrado correctamente: " + errorMsg);
    }
}
