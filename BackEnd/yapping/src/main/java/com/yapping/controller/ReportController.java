package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.report.CreateReportDTO;
import com.yapping.dto.report.ReportDTO;
import com.yapping.entity.Report;
import com.yapping.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createReport(@Valid @RequestBody CreateReportDTO createReportDTO) {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        ReportDTO createdReport = reportService.createReport(createReportDTO, userId);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.CREATED.value(),
                true,
                "Báo cáo đã được tạo thành công",
                createdReport
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String targetType) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Convert string parameters to enums
        Report.Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Report.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    "Invalid status value: " + status,
                    null
                ));
            }
        }
        
        Report.TargetType targetTypeEnum = null;
        if (targetType != null && !targetType.isEmpty()) {
            try {
                targetTypeEnum = Report.TargetType.valueOf(targetType.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    "Invalid targetType value: " + targetType,
                    null
                ));
            }
        }
        
        // Use filtered method from service
        Page<ReportDTO> reports = reportService.getReportsWithFilters(statusEnum, targetTypeEnum, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Danh sách báo cáo đã được truy xuất thành công",
                Map.of(
                        "content", reports.getContent(),
                        "totalPages", reports.getTotalPages(),
                        "totalElements", reports.getTotalElements(),
                        "currentPage", reports.getNumber(),
                        "size", reports.getSize()
                )
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getReportById(@PathVariable Long id) {
        ReportDTO report = reportService.getReportById(id);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Báo cáo đã được truy xuất thành công",
                report
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse> getReportsByStatus(
            @PathVariable Report.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReportDTO> reports = reportService.getReportsByStatus(status, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Danh sách báo cáo với trạng thái " + status + " đã được truy xuất thành công",
                Map.of(
                        "content", reports.getContent(),
                        "totalPages", reports.getTotalPages(),
                        "totalElements", reports.getTotalElements(),
                        "currentPage", reports.getNumber(),
                        "size", reports.getSize()
                )
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reason/{reason}")
    public ResponseEntity<ApiResponse> getReportsByReason(
            @PathVariable Report.Reason reason,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReportDTO> reports = reportService.getReportsByReason(reason, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Danh sách báo cáo với lý do " + reason + " đã được truy xuất thành công",
                Map.of(
                        "content", reports.getContent(),
                        "totalPages", reports.getTotalPages(),
                        "totalElements", reports.getTotalElements(),
                        "currentPage", reports.getNumber(),
                        "size", reports.getSize()
                )
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/target/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse> getReportsByTarget(
            @PathVariable Report.TargetType targetType,
            @PathVariable Long targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReportDTO> reports = reportService.getReportsByTarget(targetType, targetId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Danh sách báo cáo cho " + targetType + " với ID " + targetId + " đã được truy xuất thành công",
                Map.of(
                        "content", reports.getContent(),
                        "totalPages", reports.getTotalPages(),
                        "totalElements", reports.getTotalElements(),
                        "currentPage", reports.getNumber(),
                        "size", reports.getSize()
                )
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getCurrentUserReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReportDTO> reports = reportService.getReportsByReporter(userId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Danh sách báo cáo của người dùng hiện tại đã được truy xuất thành công",
                Map.of(
                        "content", reports.getContent(),
                        "totalPages", reports.getTotalPages(),
                        "totalElements", reports.getTotalElements(),
                        "currentPage", reports.getNumber(),
                        "size", reports.getSize()
                )
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestReports() {
        List<ReportDTO> latestReports = reportService.getLatestReports();
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Danh sách báo cáo mới nhất đã được truy xuất thành công",
                latestReports
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateReportStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        
        String statusStr = statusUpdate.get("status");
        String adminNotes = statusUpdate.get("adminNotes");
        
        if (statusStr == null || statusStr.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(
                HttpStatus.BAD_REQUEST.value(),
                false,
                "Status is required",
                null
            ));
        }
        
        try {
            Report.Status newStatus = Report.Status.valueOf(statusStr.toUpperCase());
            ReportDTO updatedReport = reportService.updateReportStatusWithNotes(id, newStatus, adminNotes);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Trạng thái báo cáo đã được cập nhật thành công",
                    updatedReport
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(
                HttpStatus.BAD_REQUEST.value(),
                false,
                "Invalid status value: " + statusStr,
                null
            ));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Báo cáo đã bị xóa thành công",
                null
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse> countReportsByTarget(
            @PathVariable Report.TargetType targetType,
            @PathVariable Long targetId) {
        
        Long count = reportService.countReportsByTarget(targetType, targetId);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Số lượng báo cáo cho " + targetType + " với ID " + targetId + " đã được truy xuất thành công",
                Map.of("count", count)
        );
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getReportStatistics() {
        Map<String, Object> stats = reportService.getReportStatistics();
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Thống kê báo cáo đã được truy xuất thành công",
                stats
        );
        
        return ResponseEntity.ok(response);
    }
}
