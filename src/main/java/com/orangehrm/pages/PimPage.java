package com.orangehrm.pages;

import org.openqa.selenium.By;

public class PimPage extends BasePage {

    private final By addEmployeeButton = By.xpath("//a[normalize-space()='Add Employee']");

    public AddEmployeePage clickAddEmployee() {
        click(addEmployeeButton);
        return new AddEmployeePage();
    }

}
