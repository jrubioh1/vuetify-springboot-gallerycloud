package com.cloud.galleryCloud.repositories;


import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cloud.galleryCloud.entities.Image;
import java.util.List;
import com.cloud.galleryCloud.entities.User;


public interface IImagesRepository extends CrudRepository<Image, Long> {
    List<Image> findByUser(User user);
    // Buscar imágenes por año y mes
    @Query("SELECT i FROM Image i WHERE YEAR(i.createdAt) = :year AND MONTH(i.createdAt) = :month")
    Optional<ArrayList<Image>> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Buscar imágenes solo por año
    @Query("SELECT i FROM Image i WHERE YEAR(i.createdAt) = :year")
    Optional<ArrayList<Image>> findByYear(@Param("year") int year);

    @Query("SELECT i FROM Image i WHERE EXTRACT(YEAR FROM i.createdAt) = :year AND EXTRACT(MONTH FROM i.createdAt) = :month ORDER BY i.createdAt DESC")
    Page<Image> findByYearAndMonthPaginator(@Param("year") int year, @Param("month") int month, Pageable pageable);

    @Query("SELECT i FROM Image i WHERE EXTRACT(YEAR FROM i.createdAt) = :year ORDER BY i.createdAt DESC")
    Page<Image> findByYearPaginator(@Param("year") int year, Pageable pageable);

    @Query("SELECT i FROM Image i ORDER BY i.createdAt DESC")
    Page<Image> findAllPaginator(Pageable pageable);

    // Obtener años únicos ordenados
    @Query("SELECT DISTINCT EXTRACT(YEAR FROM i.createdAt) AS year FROM Image i ORDER BY year DESC")
    List<Integer> findDistinctYears();

    // Obtener meses únicos de un año específico
    @Query("SELECT DISTINCT EXTRACT(MONTH FROM i.createdAt) AS month FROM Image i WHERE EXTRACT(YEAR FROM i.createdAt) = :year ORDER BY month DESC")
    List<Integer> findDistinctMonthsByYear(@Param("year") int year);

    @Query("SELECT YEAR(i.createdAt), MONTH(i.createdAt), COUNT(i.id) " +
       "FROM Image i GROUP BY YEAR(i.createdAt), MONTH(i.createdAt) " +
       "ORDER BY YEAR(i.createdAt), MONTH(i.createdAt)")
        List<Object[]> getPhotosStatistics();
    
}


