package com.yapping.service;

import com.yapping.dto.category.CategoryDTO;
import com.yapping.dto.category.CreateCategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long id);
    CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO);
    CategoryDTO updateCategory(Long id, CreateCategoryDTO updateCategoryDTO);
    void deleteCategory(Long id);
}
