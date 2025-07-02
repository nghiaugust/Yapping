package com.yapping.repository;

import com.yapping.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Tìm báo cáo theo người báo cáo
    Page<Report> findByReporterId(Long reporterId, Pageable pageable);
    
    // Tìm báo cáo theo loại đối tượng và ID
    Page<Report> findByTargetTypeAndTargetId(Report.TargetType targetType, Long targetId, Pageable pageable);
    
    // Tìm báo cáo theo trạng thái
    Page<Report> findByStatus(Report.Status status, Pageable pageable);
    
    // Tìm báo cáo theo targetType
    Page<Report> findByTargetType(Report.TargetType targetType, Pageable pageable);
    
    // Tìm báo cáo theo status và targetType
    Page<Report> findByStatusAndTargetType(Report.Status status, Report.TargetType targetType, Pageable pageable);
    
    // Tìm báo cáo theo lý do
    Page<Report> findByReason(Report.Reason reason, Pageable pageable);
    
    // Đếm số báo cáo theo loại đối tượng và ID
    Long countByTargetTypeAndTargetId(Report.TargetType targetType, Long targetId);
    
    // Đếm báo cáo theo trạng thái
    Long countByStatus(Report.Status status);
    
    // Đếm báo cáo tuần này
    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdAt >= :weekStart")
    Long countReportsFromLastWeek(@Param("weekStart") Instant weekStart);
    
    // Đếm báo cáo tháng này  
    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdAt >= :monthStart")
    Long countReportsFromLastMonth(@Param("monthStart") Instant monthStart);
    
    // Phương thức helper để gọi từ service với tham số thời gian
    default Long countReportsFromLastWeek() {
        Instant weekStart = Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);
        return countReportsFromLastWeek(weekStart);
    }
    
    default Long countReportsFromLastMonth() {
        Instant monthStart = Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS);
        return countReportsFromLastMonth(monthStart);
    }

    // Lấy 10 báo cáo mới nhất
    List<Report> findTop10ByOrderByCreatedAtDesc();
}