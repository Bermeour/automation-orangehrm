package com.orangehrm.tests;

import com.aventstack.extentreports.Status;
import com.orangehrm.config.ConfigReader;
import com.orangehrm.utils.DriverManager;
import com.orangehrm.utils.ExtentManager;
import com.orangehrm.utils.ScreenshotUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public class BaseTest {

    @BeforeEach
    void setUp(TestInfo testInfo) {
        DriverManager.initDriver();
        DriverManager.getDriver().get(ConfigReader.get("base.url"));
        ExtentManager.setTest(
                ExtentManager.getInstance().createTest(testInfo.getDisplayName())
        );
        ExtentManager.getTest().log(Status.INFO, "Iniciando test: " + testInfo.getDisplayName());
    }

    @AfterEach
    void tearDown() {
        try {
            String base64 = ScreenshotUtil.captureAsBase64();
            ExtentManager.getTest().addScreenCaptureFromBase64String(base64, "Captura final del test");
        } catch (Exception ignored) {}
        DriverManager.quitDriver();
    }

    @AfterAll
    static void generateReport() {
        ExtentManager.flush();
    }

    protected void logStep(String message) {
        ExtentManager.getTest().log(Status.INFO, message);
    }

    protected void logPass(String message) {
        ExtentManager.getTest().log(Status.PASS, message);
    }

}
