package com.yapping.service;

import com.yapping.dto.category.SubcategoryDTO;
import com.yapping.dto.category.CreateSubcategoryDTO;

import java.util.List;

public interface SubcategoryService {
    List<SubcategoryDTO> getAllSubcategories();
    List<SubcategoryDTO> getSubcategoriesByCategoryId(Long categoryId);
    SubcategoryDTO getSubcategoryById(Long id);
    SubcategoryDTO createSubcategory(CreateSubcategoryDTO createSubcategoryDTO);
    SubcategoryDTO updateSubcategory(Long id, CreateSubcategoryDTO updateSubcategoryDTO);
    void deleteSubcategory(Long id);
}
