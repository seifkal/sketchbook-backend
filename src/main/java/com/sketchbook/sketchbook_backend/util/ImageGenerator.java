package com.sketchbook.sketchbook_backend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.imgscalr.Scalr; // Import the new library
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class ImageGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${storage.output-dir:uploads}")
    private String outputDir;

    public String generateImage(String pixelData) throws IOException {
        // parse JSON array of pixel colors
        List<List<String>> pixels = objectMapper.readValue(pixelData, new TypeReference<>() {});
        int height = pixels.size();
        int width = pixels.get(0).size();

        // create raw 32x32 image
        BufferedImage rawImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Convert Hex string (e.g., "#FF0000") to Color
                rawImage.setRGB(x, y, Color.decode(pixels.get(y).get(x)).getRGB());
            }
        }


        // scale image 8x (32x32 -> 512x512
        BufferedImage scaledImage = Scalr.resize(rawImage, Scalr.Method.SPEED, width * 16);

        // write to disk
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        String filename = UUID.randomUUID() + ".png";
        File outputFile = new File(dir, filename);

        ImageIO.write(scaledImage, "png", outputFile);

        return filename;
    }
}