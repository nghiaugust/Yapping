package com.yapping.service.impl;

import com.yapping.dto.report.CreateReportDTO;
import com.yapping.dto.report.ReportDTO;
import com.yapping.entity.Comment;
import com.yapping.entity.Post;
import com.yapping.entity.Report;
import com.yapping.entity.User;
import com.yapping.repository.CommentRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.ReportRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.ReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;    @Override
    @Transactional
    public ReportDTO createReport(CreateReportDTO createReportDTO, Long reporterId) {
        // Kiểm tra người dùng tồn tại
        if (!userRepository.existsById(reporterId)) {
            throw new EntityNotFoundException("Không tìm thấy người dùng với ID: " + reporterId);
        }
        
        // Lấy username của người báo cáo
        String reporterUsername = userRepository.findById(reporterId)
                .map(User::getUsername)
                .orElse(null);

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(createReportDTO.getTargetType());
        report.setTargetId(createReportDTO.getTargetId());
        report.setReason(createReportDTO.getReason());
        report.setDescription(createReportDTO.getDescription());
        report.setStatus(Report.Status.PENDING); // Trạng thái mặc định là chờ xử lý

        Report savedReport = reportRepository.save(report);
        return mapToDTO(savedReport, reporterUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy báo cáo với ID: " + id));
        
        // Tìm thêm thông tin username của reporter nếu cần
        String reporterUsername = null;
        if (report.getReporterId() != null) {
            reporterUsername = userRepository.findById(report.getReporterId())
                    .map(User::getUsername)
                    .orElse(null);
        }
        
        return mapToDTO(report, reporterUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByReporter(Long reporterId, Pageable pageable) {
        // Kiểm tra người dùng tồn tại
        if (!userRepository.existsById(reporterId)) {
            throw new EntityNotFoundException("Không tìm thấy người dùng với ID: " + reporterId);
        }
        
        return reportRepository.findByReporterId(reporterId, pageable)
                .map(report -> {
                    String username = userRepository.findById(reporterId)
                            .map(User::getUsername)
                            .orElse(null);
                    return mapToDTO(report, username);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByTarget(Report.TargetType targetType, Long targetId, Pageable pageable) {
        return reportRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable)
                .map(report -> {
                    String username = null;
                    if (report.getReporterId() != null) {
                        username = userRepository.findById(report.getReporterId())
                                .map(User::getUsername)
                                .orElse(null);
                    }
                    return mapToDTO(report, username);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByStatus(Report.Status status, Pageable pageable) {
        return reportRepository.findByStatus(status, pageable)
                .map(report -> {
                    String username = null;
                    if (report.getReporterId() != null) {
                        username = userRepository.findById(report.getReporterId())
                                .map(User::getUsername)
                                .orElse(null);
                    }
                    return mapToDTO(report, username);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsWithFilters(Report.Status status, Report.TargetType targetType, Pageable pageable) {
        Page<Report> reports;
        
        if (status != null && targetType != null) {
            // Lọc theo cả status và targetType
            reports = reportRepository.findByStatusAndTargetType(status, targetType, pageable);
        } else if (status != null) {
            // Chỉ lọc theo status
            reports = reportRepository.findByStatus(status, pageable);
        } else if (targetType != null) {
            // Chỉ lọc theo targetType
            reports = reportRepository.findByTargetType(targetType, pageable);
        } else {
            // Không lọc gì, lấy tất cả
            reports = reportRepository.findAll(pageable);
        }
        
        return reports.map(report -> {
            String username = null;
            if (report.getReporterId() != null) {
                username = userRepository.findById(report.getReporterId())
                        .map(User::getUsername)
                        .orElse(null);
            }
            return mapToDTO(report, username);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByReason(Report.Reason reason, Pageable pageable) {
        return reportRepository.findByReason(reason, pageable)
                .map(report -> {
                    String username = null;
                    if (report.getReporterId() != null) {
                        username = userRepository.findById(report.getReporterId())
                                .map(User::getUsername)
                                .orElse(null);
                    }
                    return mapToDTO(report, username);
                });
    }

    @Override
    @Transactional
    public ReportDTO updateReportStatus(Long reportId, Report.Status newStatus) {
        return updateReportStatusWithNotes(reportId, newStatus, null);
    }

    @Override
    @Transactional
    public ReportDTO updateReportStatusWithNotes(Long reportId, Report.Status newStatus, String adminNotes) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy báo cáo với ID: " + reportId));
        
        report.setStatus(newStatus);
        if (adminNotes != null) {
            report.setAdminNotes(adminNotes);
        }
        Report updatedReport = reportRepository.save(report);
        
        String username = null;
        if (updatedReport.getReporterId() != null) {
            username = userRepository.findById(updatedReport.getReporterId())
                    .map(User::getUsername)
                    .orElse(null);
        }
        
        return mapToDTO(updatedReport, username);
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new EntityNotFoundException("Không tìm thấy báo cáo với ID: " + reportId);
        }
        reportRepository.deleteById(reportId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO> getLatestReports() {
        return reportRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(report -> {
                    String username = null;
                    if (report.getReporterId() != null) {
                        username = userRepository.findById(report.getReporterId())
                                .map(User::getUsername)
                                .orElse(null);
                    }
                    return mapToDTO(report, username);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReportsByTarget(Report.TargetType targetType, Long targetId) {
        return reportRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getReportStatistics() {
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(Report.Status.PENDING);
        long reviewingReports = reportRepository.countByStatus(Report.Status.REVIEWING);
        long resolvedActionTakenReports = reportRepository.countByStatus(Report.Status.RESOLVED_ACTION_TAKEN);
        long resolvedNoActionReports = reportRepository.countByStatus(Report.Status.RESOLVED_NO_ACTION);
        
        // Thống kê theo thời gian (tuần này và tháng này)
        // Bạn có thể thêm logic phức tạp hơn ở đây
        long reportsThisWeek = reportRepository.countReportsFromLastWeek();
        long reportsThisMonth = reportRepository.countReportsFromLastMonth();
        
        Map<String, Object> stats = Map.of(
            "totalReports", totalReports,
            "pendingReports", pendingReports,
            "reviewingReports", reviewingReports,
            "resolvedReports", resolvedActionTakenReports,
            "dismissedReports", resolvedNoActionReports,
            "reportsThisWeek", reportsThisWeek,
            "reportsThisMonth", reportsThisMonth
        );
        
        return stats;
    }

    // Phương thức hỗ trợ chuyển đổi từ đối tượng Report sang ReportDTO
    private ReportDTO mapToDTO(Report report, String reporterUsername) {
        ReportDTO.ReportDTOBuilder builder = ReportDTO.builder()
                .id(report.getId())
                .reporterId(report.getReporterId())
                .reporterUsername(reporterUsername)
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .adminNotes(report.getAdminNotes());
        
        // Lấy thông tin chi tiết về đối tượng bị báo cáo
        try {
            if (report.getTargetType() == Report.TargetType.POST) {
                postRepository.findById(report.getTargetId()).ifPresent(post -> {
                    builder.targetContent(post.getContent())
                           .targetAuthorId(post.getUser().getId())
                           .targetAuthorUsername(post.getUser().getUsername());
                });
            } else if (report.getTargetType() == Report.TargetType.COMMENT) {
                commentRepository.findById(report.getTargetId()).ifPresent(comment -> {
                    builder.targetContent(comment.getContent())
                           .targetAuthorId(comment.getUser().getId())
                           .targetAuthorUsername(comment.getUser().getUsername());
                });
            }
        } catch (Exception e) {
            // Nếu không thể lấy thông tin (đối tượng đã bị xóa), để giá trị null
            System.err.println("Không thể lấy thông tin chi tiết cho report " + report.getId() + ": " + e.getMessage());
        }
        
        return builder.build();
    }
}
