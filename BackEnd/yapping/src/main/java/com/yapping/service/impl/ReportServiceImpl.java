package com.yapping.service.impl;

import com.yapping.dto.report.CreateReportDTO;
import com.yapping.dto.report.ReportDTO;
import com.yapping.entity.Report;
import com.yapping.entity.User;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;    @Override
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
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy báo cáo với ID: " + reportId));
        
        report.setStatus(newStatus);
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

    // Phương thức hỗ trợ chuyển đổi từ đối tượng Report sang ReportDTO
    private ReportDTO mapToDTO(Report report, String reporterUsername) {
        return ReportDTO.builder()
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
                .build();
    }
}
