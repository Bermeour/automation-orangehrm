package com.orangehrm.pages;

import org.openqa.selenium.By;

public class DashboardPage extends BasePage {

    private final By dashboardTitle  = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    private final By pimMenuOption   = By.xpath("//span[text()='PIM']");
    private final By directoryOption = By.xpath("//span[text()='Directory']");

    public boolean isLoaded() {
        return isDisplayed(dashboardTitle);
    }

    public String getTitle() {
        return getText(dashboardTitle);
    }

    public PimPage navigateToPim() {
        click(pimMenuOption);
        return new PimPage();
    }

    public DirectoryPage navigateToDirectory() {
        click(directoryOption);
        return new DirectoryPage();
    }
}
