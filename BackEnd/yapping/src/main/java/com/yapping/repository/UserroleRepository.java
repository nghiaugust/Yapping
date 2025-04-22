package com.yapping.repository;

import com.yapping.entity.Userrole;
import com.yapping.entity.UserroleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserroleRepository extends JpaRepository<Userrole, UserroleId> {
    List<Userrole> findByUserId(Long userId);
}
