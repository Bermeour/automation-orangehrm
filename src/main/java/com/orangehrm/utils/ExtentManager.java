package com.orangehrm.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.orangehrm.config.ConfigReader;

public class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = ConfigReader.get("reports.path") + "AutomationReport.html";
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setEncoding("UTF-8");
            spark.config().setDocumentTitle("OrangeHRM - Reporte de Automatizacion");
            spark.config().setReportName("Pruebas E2E y API - OrangeHRM");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Aplicacion", "OrangeHRM");
            extent.setSystemInfo("Entorno", "QA");
            extent.setSystemInfo("Browser", ConfigReader.get("browser"));
            extent.setSystemInfo("Tester", "David Bermeo");
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void setTest(ExtentTest extentTest) {
        test.set(extentTest);
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
