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
    
    // Admin methods
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p FROM Post p WHERE p.post_type = :postType")
    Page<Post> findByPostType(@Param("postType") Post.Type postType, Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p FROM Post p WHERE p.visibility = :visibility AND p.post_type = :postType")
    Page<Post> findByVisibilityAndPostType(@Param("visibility") Post.Visibility visibility, @Param("postType") Post.Type postType, Pageable pageable);
    
    // Thống kê methods
    @Query("SELECT COUNT(p) FROM Post p WHERE p.post_type = :postType")
    Long countByPostType(@Param("postType") Post.Type postType);
    
    Long countByVisibility(Post.Visibility visibility);
    
    @Query("SELECT COUNT(DISTINCT p.id) FROM Post p WHERE EXISTS (SELECT 1 FROM Media m WHERE m.post.id = p.id)")
    Long countPostsWithMedia();
    
    @Query("SELECT COALESCE(SUM(p.likeCount), 0) FROM Post p")
    Long sumAllLikes();
    
    @Query("SELECT COALESCE(SUM(p.commentCount), 0) FROM Post p")
    Long sumAllComments();
    
    @Query("SELECT COALESCE(SUM(p.repostCount), 0) FROM Post p")
    Long sumAllReposts();
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt >= :start")
    Long countPostsToday(@Param("start") Instant start);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt >= :weekStart")
    Long countPostsThisWeek(@Param("weekStart") Instant weekStart);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt >= :monthStart")
    Long countPostsThisMonth(@Param("monthStart") Instant monthStart);
}
