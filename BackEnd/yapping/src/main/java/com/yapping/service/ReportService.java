package com.yapping.service;

import com.yapping.dto.report.CreateReportDTO;
import com.yapping.dto.report.ReportDTO;
import com.yapping.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {
    // Tạo một báo cáo mới
    ReportDTO createReport(CreateReportDTO createReportDTO, Long reporterId);
    
    // Lấy báo cáo theo ID
    ReportDTO getReportById(Long id);
    
    // Lấy danh sách báo cáo theo người báo cáo
    Page<ReportDTO> getReportsByReporter(Long reporterId, Pageable pageable);
    
    // Lấy danh sách báo cáo theo loại đối tượng và ID
    Page<ReportDTO> getReportsByTarget(Report.TargetType targetType, Long targetId, Pageable pageable);
    
    // Lấy danh sách báo cáo theo trạng thái
    Page<ReportDTO> getReportsByStatus(Report.Status status, Pageable pageable);
    
    // Lấy danh sách báo cáo theo lý do
    Page<ReportDTO> getReportsByReason(Report.Reason reason, Pageable pageable);
    
    // Cập nhật trạng thái báo cáo
    ReportDTO updateReportStatus(Long reportId, Report.Status newStatus);
    
    // Xóa báo cáo
    void deleteReport(Long reportId);
    
    // Lấy danh sách báo cáo mới nhất
    List<ReportDTO> getLatestReports();
    
    // Đếm số báo cáo cho một đối tượng
    Long countReportsByTarget(Report.TargetType targetType, Long targetId);
}