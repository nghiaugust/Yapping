package com.yapping.repository;

import com.yapping.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Tìm báo cáo theo người báo cáo
    Page<Report> findByReporterId(Long reporterId, Pageable pageable);
    
    // Tìm báo cáo theo loại đối tượng và ID
    Page<Report> findByTargetTypeAndTargetId(Report.TargetType targetType, Long targetId, Pageable pageable);
    
    // Tìm báo cáo theo trạng thái
    Page<Report> findByStatus(Report.Status status, Pageable pageable);
    
    // Tìm báo cáo theo lý do
    Page<Report> findByReason(Report.Reason reason, Pageable pageable);
    
    // Đếm số báo cáo theo loại đối tượng và ID
    Long countByTargetTypeAndTargetId(Report.TargetType targetType, Long targetId);
      // Lấy 10 báo cáo mới nhất
    List<Report> findTop10ByOrderByCreatedAtDesc();
}