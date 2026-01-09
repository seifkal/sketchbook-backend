package com.sketchbook.sketchbook_backend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr; // Import the new library
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${storage.output-dir:uploads}")
    private String outputDir;

    private String uploadImage(byte[] imageBytes, String filename) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType("image/png") // Vital for the browser to treat it as an image
                .build();

        s3Client.putObject(putOb, RequestBody.fromBytes(imageBytes));

        // Get the URL (assuming standard S3 DNS naming)
        return s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build()).toExternalForm();
    }
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

        // 3. Scale image 16x (32x32 -> 512x512)
        BufferedImage scaledImage = Scalr.resize(rawImage, Scalr.Method.SPEED, width * 16);

        // 4. Convert BufferedImage to Byte Array (In-Memory)
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "png", os);
        byte[] imageBytes = os.toByteArray();

        // 5. Generate Filename and Upload
        String filename = UUID.randomUUID() + ".png";

        return uploadImage(imageBytes, filename);
    }
}