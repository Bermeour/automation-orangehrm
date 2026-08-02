package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;

public class AddEmployeePage extends BasePage {

    private final By firstNameField    = By.name("firstName");
    private final By middleNameField   = By.name("middleName");
    private final By lastNameField     = By.name("lastName");
    private final By saveButton        = By.cssSelector("button[type='submit']");
    private final By profilePicInput   = By.cssSelector("input[type='file']");
    private final By savePhotoButton   = By.xpath("//button[normalize-space()='Save']");

    public boolean isLoaded() {
        return isDisplayed(firstNameField);
    }

    public AddEmployeePage enterFirstName(String firstName) {
        type(firstNameField, firstName);
        return this;
    }

    public AddEmployeePage enterMiddleName(String middleName) {
        type(middleNameField, middleName);
        return this;
    }

    public AddEmployeePage enterLastName(String lastName) {
        type(lastNameField, lastName);
        return this;
    }

    public AddEmployeePage uploadProfilePhoto(String imagePath) {
        String absolutePath = new File(imagePath).getAbsolutePath();
        // Remover restricciones del input oculto via JS y enviar la ruta del archivo
        WebElement input = driver.findElement(profilePicInput);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.cssText='display:block !important; visibility:visible !important; opacity:1 !important;';",
                input
        );
        input.sendKeys(absolutePath);

        // Confirma la foto en el modal que aparec
        click(savePhotoButton);
        return this;
    }

    public PersonalDetailsPage saveEmployee() {
        click(saveButton);
        return new PersonalDetailsPage();
    }
}
