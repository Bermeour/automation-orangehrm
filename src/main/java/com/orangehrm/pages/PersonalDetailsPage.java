package com.orangehrm.pages;

import org.openqa.selenium.By;

public class PersonalDetailsPage extends BasePage {

    private final By firstNameField  = By.name("firstName");
    private final By middleNameField = By.name("middleName");
    private final By lastNameField   = By.name("lastName");
    private final By employeeIdField = By.xpath("//label[text()='Employee Id']/following::input[1]");
    private final By pageTitle       = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    private final By successToast    = By.cssSelector(".oxd-toast-content--success");

   /* public boolean isLoaded() {
        return isDisplayed(pageTitle);
    }*/

    public String getFirstName() {
        return waitForVisible(firstNameField).getAttribute("value");
    }

    public String getMiddleName() {
        return waitForVisible(middleNameField).getAttribute("value");
    }

    public String getLastName() {
        return waitForVisible(lastNameField).getAttribute("value");
    }

    public String getEmployeeId() {
        return waitForVisible(employeeIdField).getAttribute("value");
    }

   /* public boolean isSuccessToastDisplayed() {
        return isDisplayed(successToast);
    }*/
}
