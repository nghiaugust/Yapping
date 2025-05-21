package com.yapping.service.impl;

import com.yapping.dto.category.CategoryDTO;
import com.yapping.dto.category.CreateCategoryDTO;
import com.yapping.entity.Category;
import com.yapping.repository.CategoryRepository;
import com.yapping.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + id));
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO) {
        // Kiểm tra tên thể loại đã tồn tại chưa
        if (categoryRepository.existsByName(createCategoryDTO.getName())) {
            throw new IllegalArgumentException("Thể loại với tên này đã tồn tại");
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(createCategoryDTO, category);
        category.setCreatedAt(Instant.now());
        
        // ID tự tăng hoặc tạo trước khi lưu
        if (category.getId() == null) {
            Long maxId = categoryRepository.findAll().stream()
                .map(Category::getId)
                .max(Long::compareTo)
                .orElse(0L);
            category.setId(maxId + 1);
        }
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CreateCategoryDTO updateCategoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + id));
        
        // Kiểm tra xem tên mới có bị trùng với thể loại khác không
        if (!category.getName().equals(updateCategoryDTO.getName()) && 
            categoryRepository.existsByName(updateCategoryDTO.getName())) {
            throw new IllegalArgumentException("Thể loại với tên này đã tồn tại");
        }
        
        // Cập nhật thông tin
        category.setName(updateCategoryDTO.getName());
        category.setDescription(updateCategoryDTO.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy thể loại với ID: " + id);
        }
        
        // Xóa thể loại (các thể loại phụ sẽ tự động bị xóa nhờ annotation @OnDelete)
        categoryRepository.deleteById(id);
    }
    
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        BeanUtils.copyProperties(category, categoryDTO);
        return categoryDTO;
    }
}
