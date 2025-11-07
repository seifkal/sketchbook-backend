package com.sketchbook.sketchbook_backend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
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
        List<List<String>> pixels = objectMapper.readValue(pixelData, new TypeReference<>() {});

        int height = pixels.size();
        int width = pixels.get(0).size();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // fill image with color values from pixel data array
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, Integer.parseInt(pixels.get(y).get(x), 16));
            }
        }

        //create output directory if it doesnt exist
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        //generate random filename and save image to disk
        String filename = UUID.randomUUID() + ".png";
        File outputFile = new File(dir, filename);
        ImageIO.write(image, "png", outputFile);

        return filename;
    }
}
