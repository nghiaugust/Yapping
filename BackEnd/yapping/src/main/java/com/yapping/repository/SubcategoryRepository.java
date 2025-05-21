package com.yapping.repository;

import com.yapping.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    List<Subcategory> findByCategoryIdOrderByNameAsc(Long categoryId);
    Optional<Subcategory> findByNameAndCategoryId(String name, Long categoryId);
    boolean existsByNameAndCategoryId(String name, Long categoryId);
}
