package com.yapping.repository;

import com.yapping.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name); //optional thường dùng cho find để xử lý những trường hợp không tìm được
}