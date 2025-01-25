package com.cloud.galleryCloud.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloud.galleryCloud.entities.Image;
import com.cloud.galleryCloud.entities.User;
import com.cloud.galleryCloud.repositories.IImagesRepository;
import com.cloud.galleryCloud.repositories.IUserRepository;
import com.cloud.galleryCloud.services.interfaces.IImageService;


@Service
public class ImageServiceImpl implements IImageService {

    private static final String UPLOAD_DIRECTORY = "uploads/";

    @Autowired
    private IImagesRepository imageRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    @Transactional()
    public Image uploadImage(MultipartFile file, Long userId) throws IOException {
        // Crear directorio de subida si no existe
        File uploadDir = new File(UPLOAD_DIRECTORY);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    
        // Generar un nombre único para el archivo
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIRECTORY + fileName);
    
        // Guardar el archivo físicamente
        Files.write(filePath, file.getBytes());
    
        try {
            // Obtener el usuario por ID
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        
    
            // Crear la entidad Image con la URL pública
            Image image = new Image(file.getOriginalFilename(), fileName, LocalDateTime.now(), user);
    
            // Guardar la información en la base de datos
            return imageRepository.save(image);
    
        } catch (Exception e) {
            // Si ocurre un error, eliminar el archivo previamente guardado
            Files.deleteIfExists(filePath);
            throw new RuntimeException("Error al guardar la imagen en la base de datos", e);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<Image> getAllImages() {
        return (List<Image>) imageRepository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }
    @Override
    @Transactional()
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada, id " + id));

        // Eliminar archivo físicamente
        File file = new File(image.getUrlString());
        if (file.exists()) {
            file.delete();
        }

        // Eliminar entrada en la base de datos
        imageRepository.delete(image);
    }
    @Override
@Transactional()
public Optional<Image> updateImage(Long id, String newName, MultipartFile newFile) throws IOException {
    Image image = imageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Imagen no encontrada, id " + id));

    // Backup del archivo original en caso de error
    String oldFilePath = image.getUrlString();
    String oldName = image.getName();
    Path newFilePath = null;

    try {
        // Actualizar nombre si se proporciona
        if (newName != null && !newName.isEmpty()) {
            image.setName(newName);
        }

        // Actualizar archivo si se proporciona
        if (newFile != null) {
            // Generar la nueva ruta del archivo
            String newFileName = System.currentTimeMillis() + "_" + newFile.getOriginalFilename();
            newFilePath = Paths.get(UPLOAD_DIRECTORY + newFileName);

            // Guardar el nuevo archivo físicamente
            Files.write(newFilePath, newFile.getBytes());

            // Eliminar archivo antiguo si el nuevo archivo se guarda con éxito
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && !oldFile.delete()) {
                throw new IOException("Fallo al eliminar el viejo archivo" + oldFilePath);
            }

            // Actualizar la ruta en la base de datos
            image.setUrlString(newFilePath.toString());
        }

        // Guardar la entidad actualizada en la base de datos
        return Optional.of(imageRepository.save(image));

    } catch (Exception e) {
        // En caso de error, restaurar el estado anterior
        if (newFilePath != null) {
            Files.deleteIfExists(newFilePath); // Eliminar el nuevo archivo si existe
        }
        image.setUrlString(oldFilePath); // Restaurar la ruta antigua
        image.setName(oldName); // Restaurar el nombre antiguo

        throw new RuntimeException("Error al actualizar la imagen: " + e.getMessage(), e);
    }
}
    @Override
    @Transactional(readOnly = true)
    public List<Image> getImagesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado, id" + userId));
        return imageRepository.findByUser(user);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Image> getAllImagesPaginator(Pageable pageable) {
        return imageRepository.findAllPaginator(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Image> getImagesByYearPaginator(int year, Pageable pageable) {
        return imageRepository.findByYearPaginator(year, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Image> getImagesByYearAndMonthPaginator(int year, int month, Pageable pageable) {
        return imageRepository.findByYearAndMonthPaginator(year, month, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, List<String>> getAvailableYearsAndMonths() {
        Map<Integer, List<String>> groupedYearsMonths = new LinkedHashMap<>();

        // Obtener años únicos
        List<Integer> years = imageRepository.findDistinctYears();

        for (Integer year : years) {
            // Obtener meses únicos para cada año
            List<Integer> months = imageRepository.findDistinctMonthsByYear(year);

            // Convertir los meses a nombres de meses
            List<String> monthNames = months.stream()
                    .map(month -> new java.text.DateFormatSymbols().getMonths()[month - 1])
                    .collect(Collectors.toList());

            groupedYearsMonths.put(year, monthNames);
        }

        return groupedYearsMonths;
    }
}