package dev.joseluisgs.walaspringboot.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public void initialize(ValidImage constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Permitir archivos vacíos, usar @NotNull para validar esto
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        // Validar formato de imagen
        boolean isValidFormat = contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif");

        if (!isValidFormat) {
            return false;
        }

        // Validar tamaño (máximo 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB en bytes
        return file.getSize() <= maxSize;
    }
}
