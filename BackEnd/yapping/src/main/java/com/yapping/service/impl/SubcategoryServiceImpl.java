package com.yapping.service.impl;

import com.yapping.dto.category.SubcategoryDTO;
import com.yapping.dto.category.CreateSubcategoryDTO;
import com.yapping.entity.Category;
import com.yapping.entity.Subcategory;
import com.yapping.repository.CategoryRepository;
import com.yapping.repository.SubcategoryRepository;
import com.yapping.service.SubcategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {

    @Autowired
    private SubcategoryRepository subcategoryRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryDTO> getAllSubcategories() {
        return subcategoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryDTO> getSubcategoriesByCategoryId(Long categoryId) {
        // Kiểm tra category tồn tại
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Không tìm thấy thể loại chính với ID: " + categoryId);
        }
        
        return subcategoryRepository.findByCategoryIdOrderByNameAsc(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubcategoryDTO getSubcategoryById(Long id) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại phụ với ID: " + id));
        return convertToDTO(subcategory);
    }

    @Override
    @Transactional
    public SubcategoryDTO createSubcategory(CreateSubcategoryDTO createSubcategoryDTO) {
        // Kiểm tra category tồn tại
        Category category = categoryRepository.findById(createSubcategoryDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại chính với ID: " + createSubcategoryDTO.getCategoryId()));
        
        // Kiểm tra tên thể loại phụ trong cùng thể loại chính đã tồn tại chưa
        if (subcategoryRepository.existsByNameAndCategoryId(
                createSubcategoryDTO.getName(), createSubcategoryDTO.getCategoryId())) {
            throw new IllegalArgumentException("Thể loại phụ với tên này đã tồn tại trong thể loại chính này");
        }
        
        Subcategory subcategory = new Subcategory();
        BeanUtils.copyProperties(createSubcategoryDTO, subcategory);
        subcategory.setCategory(category);
        subcategory.setCreatedAt(Instant.now());
        
        // ID tự tăng hoặc tạo trước khi lưu
        if (subcategory.getId() == null) {
            Long maxId = subcategoryRepository.findAll().stream()
                .map(Subcategory::getId)
                .max(Long::compareTo)
                .orElse(0L);
            subcategory.setId(maxId + 1);
        }
        
        Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
        return convertToDTO(savedSubcategory);
    }

    @Override
    @Transactional
    public SubcategoryDTO updateSubcategory(Long id, CreateSubcategoryDTO updateSubcategoryDTO) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại phụ với ID: " + id));
        
        // Nếu thay đổi category, kiểm tra category mới tồn tại
        Category category;
        if (!subcategory.getCategory().getId().equals(updateSubcategoryDTO.getCategoryId())) {
            category = categoryRepository.findById(updateSubcategoryDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại chính với ID: " + updateSubcategoryDTO.getCategoryId()));
            subcategory.setCategory(category);
        }
        
        // Kiểm tra xem tên mới có bị trùng với thể loại phụ khác trong cùng thể loại chính không
        if (!subcategory.getName().equals(updateSubcategoryDTO.getName()) && 
            subcategoryRepository.existsByNameAndCategoryId(
                updateSubcategoryDTO.getName(), updateSubcategoryDTO.getCategoryId())) {
            throw new IllegalArgumentException("Thể loại phụ với tên này đã tồn tại trong thể loại chính này");
        }
        
        // Cập nhật thông tin
        subcategory.setName(updateSubcategoryDTO.getName());
        subcategory.setDescription(updateSubcategoryDTO.getDescription());
        
        Subcategory updatedSubcategory = subcategoryRepository.save(subcategory);
        return convertToDTO(updatedSubcategory);
    }

    @Override
    @Transactional
    public void deleteSubcategory(Long id) {
        if (!subcategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy thể loại phụ với ID: " + id);
        }
        
        subcategoryRepository.deleteById(id);
    }
    
    private SubcategoryDTO convertToDTO(Subcategory subcategory) {
        SubcategoryDTO subcategoryDTO = new SubcategoryDTO();
        BeanUtils.copyProperties(subcategory, subcategoryDTO);
        
        // Thêm thông tin về category
        subcategoryDTO.setCategoryId(subcategory.getCategory().getId());
        subcategoryDTO.setCategoryName(subcategory.getCategory().getName());
        
        return subcategoryDTO;
    }
}
