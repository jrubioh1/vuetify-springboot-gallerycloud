package com.cloud.galleryCloud.services;

import com.cloud.galleryCloud.entities.Image;
import com.cloud.galleryCloud.entities.User;
import com.cloud.galleryCloud.repositories.IImagesRepository;
import com.cloud.galleryCloud.repositories.IRolRepository;
import com.cloud.galleryCloud.repositories.IUserRepository;
import com.cloud.galleryCloud.services.interfaces.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUser {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IImagesRepository imagesRepository;

    @Autowired IRolRepository rolRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        user.setRoles(Arrays.asList(rolRepository.findByName("ROLE_USER").orElseThrow()));
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
    public Optional<User> updateUser(Long id, User user) {
         // Verificamos si el usuario existe antes de actualizar
         if (userRepository.existsById(user.getId())) {
            User updatedUser = userRepository.save(user);
            return Optional.of(updatedUser);
        }
        return Optional.empty();
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        return imagesRepository.findByUser(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
        
    }
}