package com.yapping.repository;

import com.yapping.entity.Userrole;
import com.yapping.entity.UserroleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserroleRepository extends JpaRepository<Userrole, UserroleId> {
    List<Userrole> findByUserId(Long userId);
    @Modifying
    @Query("DELETE FROM Userrole ur WHERE ur.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
