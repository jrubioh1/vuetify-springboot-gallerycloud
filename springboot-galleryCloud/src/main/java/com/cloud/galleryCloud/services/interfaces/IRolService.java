package com.cloud.galleryCloud.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.cloud.galleryCloud.entities.Rol;

public interface IRolService {
    List<Rol> findAll();
    Optional<Rol> findById(Long id);
    Optional<Rol> findByName(String name);
    Rol save(Rol rol);
    Optional<Rol>  update(Long id, Rol rol);
    Optional<Rol> delete(Long id);


}
