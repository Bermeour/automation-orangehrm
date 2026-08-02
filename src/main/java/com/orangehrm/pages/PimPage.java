package com.orangehrm.pages;

import org.openqa.selenium.By;

public class PimPage extends BasePage {

    private final By addEmployeeButton = By.xpath("//a[normalize-space()='Add Employee']");
    /*private final By employeeListTitle = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    private final By searchNameInput   = By.xpath("//label[text()='Employee Name']/following::input[1]");
    private final By searchButton      = By.cssSelector("button[type='submit']");
    private final By firstResultName   = By.xpath("//div[@class='oxd-table-body']//div[@role='row'][1]//div[2]");
*/
   /* public boolean isLoaded() {
        return isDisplayed(employeeListTitle);
    }*/

    public AddEmployeePage clickAddEmployee() {
        click(addEmployeeButton);
        return new AddEmployeePage();
    }

   /* public PimPage searchEmployeeByName(String name) {
        type(searchNameInput, name);
        click(searchButton);
        return this;
    }

    public String getFirstResultName() {
        return getText(firstResultName);
    }*/
}
