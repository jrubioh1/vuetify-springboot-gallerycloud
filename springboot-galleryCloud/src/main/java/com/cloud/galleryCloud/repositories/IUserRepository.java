package com.cloud.galleryCloud.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.cloud.galleryCloud.entities.User;

public interface IUserRepository extends CrudRepository<User, Long> {
    
    public Optional<User> findByEmail(String email);
    public Optional<User> findByUsername(String username);
    public Optional<User> findByEmailAndPassword(String email, String password);
    public Optional<User> findByUsernameAndPassword(String username, String password);
    public boolean existsByEmail(String email);
    public boolean existsByUsername(String username);


}
