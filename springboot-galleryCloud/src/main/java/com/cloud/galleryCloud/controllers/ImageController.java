package com.cloud.galleryCloud.controllers;

import com.cloud.galleryCloud.entities.Image;
import com.cloud.galleryCloud.repositories.IImagesRepository;
import com.cloud.galleryCloud.services.interfaces.IImageService;
import com.cloud.galleryCloud.validators.ImageValidator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/images")
public class ImageController {


    @Autowired
    private IImageService imageService;
    public String URL_API;

    @Autowired
    private ImageValidator imageValidator;
    

    @Value("${url.api}")
    public void setURL_API(String URL_API) {
        this.URL_API = URL_API;
    }
    

/**
 * Endpoint para subir múltiples imágenes.
 *
 * @param files  Archivos de las imágenes a subir.
 * @param userId ID del usuario asociado.
 * @return Lista de imágenes creadas.
 */
@PostMapping("/upload")
public ResponseEntity<List<Image>> uploadImages(@RequestParam("files") List<MultipartFile> files,
                                                @RequestParam(defaultValue="1") Long userId) {
    System.out.println("!!!SE ESTA ASIGNADO AL USUARIO CON ID: "+userId+"!! \n ¡¡ESTA POR DEFAUL HASTA QUE SE IMPLEMENTE EL LOGIN Y COJA EL ID DEL USUARIO LOGEADO!!!");
    try {
        List<Image> images = files.stream().map(file -> {
            try {
                // Validar cada archivo
                imageValidator.validate(file);

                // Subir la imagen
                return imageService.uploadImage(file, userId);
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar la imagen: " + file.getOriginalFilename(), e);
            }
        }).map(this::parseUrl).toList();

        return ResponseEntity.ok(images);
    } catch (RuntimeException e) {
        return ResponseEntity.status(500).body(null); // Retorna error si algo falla
    }
}

    /**
     * Endpoint para obtener todas las imágenes.
     *
     * @return Lista de imágenes.
     */
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageService.getAllImages();
        images= images.stream().map(this::parseUrl).toList();
        return ResponseEntity.ok(images);
  
    }
    /**
 * Endpoint consolidado para obtener imágenes paginadas, con filtros opcionales por año y mes.
 *
 * @param year  (Opcional) Año para filtrar las imágenes.
 * @param month (Opcional) Mes para filtrar las imágenes.
 * @param page  Número de la página.
 * @param size  Tamaño de la página.
 * @return Página de imágenes filtradas.
 */
@GetMapping("/paginator")
public ResponseEntity<Map<String, Object>> getFilteredImages(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    int nextPage = page + 1;
    Pageable pageable = PageRequest.of(page, size);
    Page<Image> imagesPage;

    // Filtrar según los parámetros
    if (year != null && month != null) {
        imagesPage = imageService.getImagesByYearAndMonthPaginator(year, month, pageable);
    } else if (year != null) {
        imagesPage = imageService.getImagesByYearPaginator(year, pageable);
    } else {
        imagesPage = imageService.getAllImagesPaginator(pageable);
    }

    // Mapear las URLs completas de las imágenes
    List<Image> images = imagesPage.getContent();

    // Agrupar imágenes por año y mes
    Map<Integer, Map<Integer, List<Image>>> groupedImages = new TreeMap<>();
    
    for (Image img : images) {
        int imgYear = img.getYear(); 
        int imgMonth = img.getMonth(); 

        groupedImages
            .computeIfAbsent(imgYear, k -> new TreeMap<>())
            .computeIfAbsent(imgMonth, k -> new ArrayList<>())
            .add(img);
    }

    // Construir la URL de la siguiente página si hay más elementos
    System.out.println("page: "+page);
    String nextUrl = null;
    if (imagesPage.hasNext()) {
        System.out.println(nextPage);
        nextUrl = URL_API+String.format("api/images/paginator?page=%d",nextPage);
        if (year != null) nextUrl += "&year=" + year;
        if (month != null) nextUrl += "&month=" + month;
    }
    

    // Construir la respuesta JSON
    Map<String, Object> response = new HashMap<>();
    response.put("results", groupedImages);
    response.put("next", nextUrl); 
    return ResponseEntity.ok(response);
}
    /**
     * Endpoint para obtener una imagen por ID.
     *
     * @param id ID de la imagen.
     * @return La imagen encontrada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        Optional<Image> image = imageService.getImageById(id);
        if (image.isPresent()) {
            return ResponseEntity.ok( parseUrl(image.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para actualizar una imagen.
     *
     * @param id      ID de la imagen a actualizar.
     * @param newName Nuevo nombre de la imagen (opcional).
     * @param newFile Nuevo archivo de la imagen (opcional).
     * @return La imagen actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable Long id,
                                                       @RequestParam(value = "newName", required = false) String newName,
                                                       @RequestParam(value = "newFile", required = false) MultipartFile newFile) {
        try {
            Optional<Image> updatedImage = imageService.updateImage(id, newName, newFile);
            return ResponseEntity.ok(parseUrl(updatedImage.get()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Endpoint para eliminar una imagen por ID.
     *
     * @param id ID de la imagen.
     * @return Respuesta de éxito o error.
     */
    @DeleteMapping
    public ResponseEntity<String> deleteImage(@RequestParam("id") List<Long> ids) {
        System.out.println(ids.toString());
        try {
            ids.forEach(id -> imageService.deleteImage(id));
            return ResponseEntity.ok("Imagen eliminada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Error al eliminar la imagen.");
        }
    }

    /**
     * Endpoint para obtener imágenes por usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de imágenes asociadas al usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Image>> getImagesByUser(@PathVariable Long userId) {
        try {
            List<Image> images = imageService.getImagesByUser(userId);
            images= images.stream().map(this::parseUrl).toList();
            return ResponseEntity.ok(images);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/available-years-months")
    public ResponseEntity<Map<Integer, List<String>>> getAvailableYearsAndMonths() {
        try {
            Map<Integer, List<String>> groupedYearsMonths = imageService.getAvailableYearsAndMonths();
            return ResponseEntity.ok(groupedYearsMonths);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    private Image parseUrl(Image image){
        image.setUrlString(URL_API + image.getUrlString());
        return image;
    }

    @GetMapping("/photos-tree-statistics")
public ResponseEntity<List<Map<String, Object>>> getPhotosTreeStatistics() {
    List<Map<String, Object>> data = new ArrayList<>();
    Map<Integer, Map<String, Object>> years = new TreeMap<>();

    // Obtener las estadísticas de imágenes agrupadas por año y mes
    List<Object[]> photosData = imageService.getPhotosStatistics();

    for (Object[] item : photosData) {
        Integer year = (Integer) item[0];
        Integer month = (Integer) item[1];
        Long count = (Long) item[2];

        // Si el año no existe, lo agregamos al mapa
        years.putIfAbsent(year, new HashMap<>());
        Map<String, Object> yearData = years.get(year);
        yearData.putIfAbsent("name", year.toString());
        yearData.putIfAbsent("children", new ArrayList<>());

        // Agregar los meses con la cantidad de imágenes
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> children = (List<Map<String, Object>>) yearData.get("children");
        Map<String, Object> monthData = new HashMap<>();
        monthData.put("name", Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        monthData.put("value", count);
        children.add(monthData);
    }

    data.addAll(years.values());
    return ResponseEntity.ok(data);
}

@PostMapping("/download-selected")
    public ResponseEntity<?> downloadSelectedImages(@RequestParam List<Long> id) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No images selected");
        }

        try {
            // Buscar imágenes por ID
            List<Image> images = imageService.findAllById(id);
            if (images.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No images found");
            }

            // Crear archivo ZIP en memoria
            ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
            try (ZipOutputStream zipOut = new ZipOutputStream(zipOutputStream)) {
                for (Image image : images) {
                    String url = "uploads/"+(image.getUrlString().replace(URL_API, ""));
                    File imageFile = new File(url); // Ruta física de la imagen
                    if (imageFile.exists()) {
                        FileInputStream fis = new FileInputStream(imageFile);
                        ZipEntry zipEntry = new ZipEntry(imageFile.getName());
                        zipOut.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) >= 0) {
                            zipOut.write(buffer, 0, length);
                        }
                        zipOut.closeEntry();
                        fis.close();
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found: " + image.getName());
                    }
                }
            }

            // Preparar la respuesta con el archivo ZIP
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(zipOutputStream.toByteArray()));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=selected_images.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zipOutputStream.size())
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading images: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadImage(@RequestParam("id") Long id) {
        try {
            // Buscar imagen por ID
            Image image = imageService.getImageById(id).orElseThrow();
            if (image == null) {
                return ResponseEntity.status(404).body("Image not found");
            }
            String url = "uploads/"+(image.getUrlString().replace(URL_API, ""));
            // Crear respuesta con el archivo
            File file = new File(url);
            if (!file.exists()) {
                return ResponseEntity.status(404).body("File not found on server");
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (FileNotFoundException e) {
            return ResponseEntity.status(500).body("File not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error downloading image: " + e.getMessage());
        }
    }

}
