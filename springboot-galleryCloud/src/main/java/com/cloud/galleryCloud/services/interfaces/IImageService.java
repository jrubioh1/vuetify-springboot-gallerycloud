package com.cloud.galleryCloud.services.interfaces;

import com.cloud.galleryCloud.entities.Image;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IImageService {

    /**
     * Sube una nueva imagen, guarda el archivo físicamente y registra la información en la base de datos.
     *
     * @param file   Archivo de la imagen a subir.
     * @param name   Nombre de la imagen.
     * @param userId ID del usuario asociado a la imagen.
     * @return La entidad `Image` guardada.
     * @throws IOException Si ocurre un error al guardar el archivo.
     */
    Image uploadImage(MultipartFile file, Long userId) throws IOException;

    /**
     * Recupera todas las imágenes almacenadas en la base de datos.
     *
     * @return Una lista de entidades `Image`.
     */
    List<Image> getAllImages();

    /**
     * Recupera una imagen específica por su ID.
     *
     * @param id ID de la imagen a recuperar.
     * @return Un `Optional` que contiene la entidad `Image`, si existe.
     */
    Optional<Image> getImageById(Long id);

    /**
     * Elimina una imagen de la base de datos y el archivo físico asociado.
     *
     * @param id ID de la imagen a eliminar.
     */
    void deleteImage(Long id);

    /**
     * Actualiza los datos de una imagen, incluyendo su archivo físico si se proporciona uno nuevo.
     *
     * @param id      ID de la imagen a actualizar.
     * @param newName Nuevo nombre de la imagen (opcional).
     * @param newFile Nuevo archivo de imagen (opcional).
     * @return La entidad `Image` actualizada.
     * @throws IOException Si ocurre un error al guardar el archivo.
     */
    Optional<Image> updateImage(Long id, String newName, MultipartFile newFile) throws IOException;

    /**
     * Recupera todas las imágenes asociadas a un usuario específico.
     *
     * @param userId ID del usuario cuyas imágenes se desean recuperar.
     * @return Una lista de entidades `Image` asociadas al usuario.
     */
    List<Image> getImagesByUser(Long userId);


    Page<Image> getAllImagesPaginator(Pageable pageable);
    Page<Image> getImagesByYearPaginator(int year, Pageable pageable);
    Page<Image> getImagesByYearAndMonthPaginator(int year, int month, Pageable pageable);

    Map<Integer, List<String>> getAvailableYearsAndMonths();

}