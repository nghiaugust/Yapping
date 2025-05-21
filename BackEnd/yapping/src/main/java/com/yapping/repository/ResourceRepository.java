package com.yapping.repository;

import com.yapping.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // Tìm tài liệu theo ID người dùng
    List<Resource> findByUserId(Long userId);
    
    // Tìm tài liệu theo ID bài đăng
    List<Resource> findByPostId(Long postId);
    
    // Tìm tài liệu theo ID thể loại
    List<Resource> findByCategoryId(Long categoryId);
    
    // Tìm tài liệu theo ID thể loại phụ
    List<Resource> findBySubCategoryId(Long subCategoryId);
    
    // Tìm tài liệu theo tiêu đề (tìm kiếm mờ)
    List<Resource> findByTitleContainingIgnoreCase(String title);
    
    // Phân trang tài liệu theo lượt xem giảm dần
    Page<Resource> findAllByOrderByViewCountDesc(Pageable pageable);
    
    // Phân trang tài liệu theo ngày tạo giảm dần
    Page<Resource> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Tìm tài liệu theo người dùng và phân trang
    Page<Resource> findByUserId(Long userId, Pageable pageable);
    
    // Tìm tài liệu theo thể loại và phân trang
    Page<Resource> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Tìm tài liệu theo thể loại phụ và phân trang
    Page<Resource> findBySubCategoryId(Long subCategoryId, Pageable pageable);
    
    // Tìm tài liệu theo thể loại và thể loại phụ
    List<Resource> findByCategoryIdAndSubCategoryId(Long categoryId, Long subCategoryId);
    
    // Tìm kiếm tài liệu nâng cao
    @Query("SELECT r FROM Resource r WHERE " +
           "(:title IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:categoryId IS NULL OR r.category.id = :categoryId) AND " +
           "(:subCategoryId IS NULL OR r.subCategory.id = :subCategoryId) AND " +
           "(:userId IS NULL OR r.user.id = :userId)")
    Page<Resource> searchResources(
            @Param("title") String title,
            @Param("categoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId,
            @Param("userId") Long userId,
            Pageable pageable);
}
