package com.yapping.dto.report;

import com.yapping.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id; // ID báo cáo
    private Long reporterId; // ID người báo cáo
    private String reporterUsername; // Tên người báo cáo (tùy chọn)
    private Report.TargetType targetType; // Loại đối tượng bị báo cáo
    private Long targetId; // ID của đối tượng bị báo cáo
    private Report.Reason reason; // Lý do báo cáo
    private String description; // Mô tả chi tiết (không bắt buộc)
    private Report.Status status; // Trạng thái xử lý báo cáo
    private Instant createdAt; // Thời gian tạo
    private Instant updatedAt; // Thời gian cập nhật
}