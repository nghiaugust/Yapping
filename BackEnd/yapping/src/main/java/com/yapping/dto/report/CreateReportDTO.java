package com.yapping.dto.report;

import com.yapping.entity.Report;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportDTO {
    @NotNull(message = "Loại đối tượng không được để trống")
    private Report.TargetType targetType;
    
    @NotNull(message = "ID đối tượng không được để trống")
    private Long targetId;
    
    @NotNull(message = "Lý do báo cáo không được để trống")
    private Report.Reason reason;
    
    private String description; // Mô tả chi tiết (không bắt buộc)
}