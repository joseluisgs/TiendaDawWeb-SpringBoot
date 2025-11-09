package dev.joseluisgs.walaspringboot.services;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 600;

    public byte[] redimensionarImagen(MultipartFile file) throws IOException {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            if (originalImage == null) {
                throw new IOException("No se pudo leer la imagen");
            }

            // Si la imagen ya es pequeña, no redimensionar
            if (originalImage.getWidth() <= MAX_WIDTH && originalImage.getHeight() <= MAX_HEIGHT) {
                return file.getBytes();
            }

            // Redimensionar manteniendo el aspect ratio
            BufferedImage resizedImage = Scalr.resize(originalImage,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.FIT_TO_WIDTH,
                    MAX_WIDTH,
                    MAX_HEIGHT,
                    Scalr.OP_ANTIALIAS);

            // Convertir a bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String formatName = getFormatName(file.getContentType());
            ImageIO.write(resizedImage, formatName, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            logger.info("Imagen redimensionada de {}x{} a {}x{}",
                    originalImage.getWidth(), originalImage.getHeight(),
                    resizedImage.getWidth(), resizedImage.getHeight());

            return imageInByte;
        } catch (IOException e) {
            logger.error("Error al redimensionar imagen: " + e.getMessage());
            throw e;
        }
    }

    private String getFormatName(String contentType) {
        if (contentType == null) {
            return "jpg";
        }
        switch (contentType) {
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/jpeg":
            case "image/jpg":
            default:
                return "jpg";
        }
    }
}
