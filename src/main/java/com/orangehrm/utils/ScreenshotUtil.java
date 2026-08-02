package com.orangehrm.utils;

import com.orangehrm.config.ConfigReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ScreenshotUtil {

    public static String captureAsBase64() {
        return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
    }

  /*  public static String captureToFile(String testName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = testName + "_" + timestamp + ".png";
        String filePath = ConfigReader.get("screenshots.path") + fileName;

        File screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
        try {
            Files.createDirectories(Paths.get(ConfigReader.get("screenshots.path")));
            Files.copy(screenshot.toPath(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el screenshot: " + filePath, e);
        }
        return new File(filePath).getAbsolutePath();
    }*/
}
