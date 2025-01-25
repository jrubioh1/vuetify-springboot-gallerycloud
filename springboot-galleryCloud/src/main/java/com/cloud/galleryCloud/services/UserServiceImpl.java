package com.cloud.galleryCloud.services;

import com.cloud.galleryCloud.entities.Image;
import com.cloud.galleryCloud.entities.User;
import com.cloud.galleryCloud.repositories.IImagesRepository;
import com.cloud.galleryCloud.repositories.IUserRepository;
import com.cloud.galleryCloud.services.interfaces.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUser {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IImagesRepository imagesRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        // Crear un nuevo usuario
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        // Obtener todos los usuarios
        return (List<User>)userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        // Buscar un usuario por ID
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<User> updateUser(Long id, User updatedUser) {
        // Actualizar los datos de un usuario
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            // Actualizar otros campos según tu modelo
            return userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // Eliminar usuario
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Image> getUserImages(Long userId) {
        // Obtener imágenes asociadas a un usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        return imagesRepository.findByUser(user);
    }
}