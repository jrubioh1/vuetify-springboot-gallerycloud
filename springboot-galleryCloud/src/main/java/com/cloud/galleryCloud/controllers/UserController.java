package com.cloud.galleryCloud.controllers;

import com.cloud.galleryCloud.entities.Image;
import com.cloud.galleryCloud.entities.User;
import com.cloud.galleryCloud.services.interfaces.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUser userService;

    /**
     * Endpoint para crear un nuevo usuario.
     *
     * @param user Datos del usuario a crear.
     * @return El usuario creado.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Endpoint para obtener todos los usuarios.
     *
     * @return Lista de usuarios.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint para obtener un usuario por su ID.
     *
     * @param id ID del usuario a buscar.
     * @return El usuario encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para actualizar un usuario.
     *
     * @param id   ID del usuario a actualizar.
     * @param user Datos actualizados del usuario.
     * @return El usuario actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Optional<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> updatedUser = userService.updateUser(id, user);
        if (updatedUser.isPresent()) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para eliminar un usuario.
     *
     * @param id ID del usuario a eliminar.
     * @return Respuesta de éxito o error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para obtener imágenes asociadas a un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de imágenes asociadas al usuario.
     */
    @GetMapping("/{userId}/images")
    public ResponseEntity<List<Image>> getUserImages(@PathVariable Long userId) {
        try {
            List<Image> images = userService.getUserImages(userId);
            return ResponseEntity.ok(images);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}