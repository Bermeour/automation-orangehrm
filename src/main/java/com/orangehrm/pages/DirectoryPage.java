package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DirectoryPage extends BasePage {

    private final By pageTitle         = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    private final By employeeNameInput = By.xpath("//label[text()='Employee Name']/following::input[1]");
    private final By autocompleteOption = By.cssSelector(".oxd-autocomplete-option");
    private final By searchButton      = By.cssSelector("button[type='submit']");
    private final By employeeCards     = By.cssSelector(".orangehrm-directory-card");
   // private final By employeeCardNames = By.cssSelector(".orangehrm-directory-card-header");
    //private final By noRecordsFound    = By.xpath("//*[contains(text(),'No Records Found')]");

    public boolean isLoaded() {
        return isDisplayed(pageTitle);
    }

    public DirectoryPage searchByEmployeeName(String name) {
        waitForClickable(employeeNameInput);
        type(employeeNameInput, name);
        // Esperar a que aparezca la opcion en el autocomplete y seleccionarla
        try {
            WebElement option = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(autocompleteOption));
            option.click();
        } catch (Exception e) {
            // Si no hay sugerencia en el autocomplete, el campo queda vacio;validacion para un log futuro
        }
        click(searchButton);
        waitForResultsToLoad();
        return this;
    }

    private void waitForResultsToLoad() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
        }
    }

    public int getResultCount() {
        List<WebElement> cards = driver.findElements(employeeCards);
        return cards.size();
    }

  /*  public String getFirstResultName() {
        List<WebElement> names = driver.findElements(employeeCardNames);
        if (names.isEmpty()) {
            return "";
        }
        return names.get(0).getText().trim();
    }

    public boolean isNoResultsDisplayed() {
        return isDisplayed(noRecordsFound);
    }*/

    public boolean containsEmployee(String firstName, String lastName) {
        List<WebElement> cards = driver.findElements(employeeCards);
        return cards.stream().anyMatch(card -> {
            String cardText = card.getText().toLowerCase();
            return cardText.contains(firstName.toLowerCase())
                    && cardText.contains(lastName.toLowerCase());
        });
    }
}
