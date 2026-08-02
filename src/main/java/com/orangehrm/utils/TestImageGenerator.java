package com.orangehrm.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestImageGenerator {

    public static String generate(String outputPath) {
        try {
            Files.createDirectories(Paths.get(outputPath).getParent());

            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setColor(new Color(70, 130, 180));
            g2d.fillRect(0, 0, 200, 200);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics fm = g2d.getFontMetrics();
            String text = "TEST";
            int x = (200 - fm.stringWidth(text)) / 2;
            int y = (200 - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(text, x, y);
            g2d.dispose();

            File outputFile = new File(outputPath);
            ImageIO.write(image, "png", outputFile);
            return outputFile.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException("No se pudo generar la imagen de prueba: " + outputPath, e);
        }
    }
}
