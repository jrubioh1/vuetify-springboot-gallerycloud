package com.cloud.galleryCloud.repositories;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;


import com.cloud.galleryCloud.entities.Rol;



public interface IRolRepository extends CrudRepository<Rol, Long> {

    Optional<Rol>  findByName(String name);


}
