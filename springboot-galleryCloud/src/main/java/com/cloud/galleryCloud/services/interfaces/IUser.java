package com.cloud.galleryCloud.services.interfaces;

import com.cloud.galleryCloud.entities.Image;
import com.cloud.galleryCloud.entities.User;
import java.util.List;
import java.util.Optional;

public interface IUser {

    /**
     * Crea un nuevo usuario.
     * 
     * @param user La entidad `User` a crear.
     * @return La entidad `User` creada.
     */
    User createUser(User user);

    /**
     * Obtiene todos los usuarios registrados.
     * 
     * @return Una lista de entidades `User`.
     */
    List<User> getAllUsers();

    /**
     * Busca un usuario por su ID.
     * 
     * @param id El ID del usuario a buscar.
     * @return Un `Optional` con el usuario encontrado, o vacío si no existe.
     */
    Optional<User> getUserById(Long id);

    /**
     * Actualiza los datos de un usuario.
     * 
     * @param id El ID del usuario a actualizar.
     * @param user Los datos actualizados del usuario.
     * @return La entidad `User` actualizada.
     */
    Optional<User> updateUser(Long id, User user);

    /**
     * Elimina un usuario por su ID.
     * 
     * @param id El ID del usuario a eliminar.
     */
    void deleteUser(Long id);

    /**
     * Obtiene las imágenes asociadas a un usuario específico.
     * 
     * @param userId El ID del usuario.
     * @return Una lista de imágenes asociadas al usuario.
     */
    List<Image> getUserImages(Long userId);
}