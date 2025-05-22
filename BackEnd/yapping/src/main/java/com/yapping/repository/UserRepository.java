package com.yapping.repository;

import com.yapping.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Tìm tất cả người dùng đang theo dõi người dùng có ID được chỉ định
    @Query("SELECT u FROM User u JOIN Follow f ON u.id = f.follower.id WHERE f.followed.id = :userId")
    List<User> findFollowersByUserId(@Param("userId") Long userId);
}
