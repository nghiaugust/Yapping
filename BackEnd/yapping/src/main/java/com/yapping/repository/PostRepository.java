package com.yapping.repository;

import com.yapping.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findByUserId(@Param("userId") Long userId);
    // Lấy danh sách bài đăng, sắp xếp theo createdAt giảm dần
    List<Post> findAllByOrderByCreatedAtDesc();

    // Lấy bài đăng công khai, sắp xếp theo createdAt giảm dần
    List<Post> findByVisibilityOrderByCreatedAtDesc(Post.Visibility visibility);

    // Lấy bài đăng trong khoảng thời gian, sắp xếp theo createdAt
    List<Post> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end);

    @EntityGraph(value = "Post.withUserAndParentPost")
    Optional<Post> findById(Long id);

    Page<Post> getPostsByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByVisibility(Post.Visibility visibility, Pageable pageable);
}
