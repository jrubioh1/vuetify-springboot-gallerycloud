package com.cloud.galleryCloud.validators;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageValidator {

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío o no se proporcionó.");
        }

        // Validar tamaño máximo
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido de 5 MB.");
        }

        // Validar formatos permitidos
        String contentType = file.getContentType();
        if (contentType == null || 
           !(contentType.equals("image/jpeg") || 
             contentType.equals("image/jpg") || 
             contentType.equals("image/png") || 
             contentType.equals("image/gif"))) {
            throw new IllegalArgumentException("Formato de archivo no permitido. Solo se aceptan JPEG, PNG y GIF.");
        }
    }
}