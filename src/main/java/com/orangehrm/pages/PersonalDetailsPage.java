package com.orangehrm.pages;

import org.openqa.selenium.By;

public class PersonalDetailsPage extends BasePage {

    private final By firstNameField  = By.name("firstName");
    private final By middleNameField = By.name("middleName");
    private final By lastNameField   = By.name("lastName");

    public String getFirstName() {
        return waitForVisible(firstNameField).getAttribute("value");
    }

    public String getMiddleName() {
        return waitForVisible(middleNameField).getAttribute("value");
    }

    public String getLastName() {
        return waitForVisible(lastNameField).getAttribute("value");
    }

}
