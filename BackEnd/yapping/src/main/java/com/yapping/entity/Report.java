package com.yapping.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    private Long id;

    // Enum cho target_type (Loại đối tượng bị báo cáo)
    public enum TargetType {
        POST,    // Bài đăng
        COMMENT  // Bình luận
    }

    // Enum cho reason (Lý do báo cáo)
    public enum Reason {
        SPAM,                   // Nội dung rác, quảng cáo
        HARASSMENT,             // Quấy rối, bắt nạt
        HATE_SPEECH,            // Ngôn từ thù địch
        INAPPROPRIATE_CONTENT,  // Nội dung không phù hợp
        INTELLECTUAL_PROPERTY,  // Vi phạm sở hữu trí tuệ
        MISINFORMATION,         // Thông tin sai lệch
        OTHER                   // Khác
    }

    // Enum cho status (Trạng thái báo cáo)
    public enum Status {
        PENDING,                // Chờ xử lý
        REVIEWING,              // Đang xem xét
        RESOLVED_ACTION_TAKEN,  // Đã giải quyết (có hành động)
        RESOLVED_NO_ACTION      // Đã giải quyết (không cần hành động)
    }

    @NotNull
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;
    
    @NotNull
    @Enumerated(EnumType.STRING) // Sử dụng EnumType.STRING để lưu tên enum dưới dạng chuỗi
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @NotNull
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private Reason reason; // Lý do báo cáo

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;    
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING; // Đặt giá trị mặc định trong Java

    @CreationTimestamp // Tự động đặt khi tạo
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt; // Thời gian tạo

    @UpdateTimestamp // Tự động đặt khi cập nhật
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // Thời gian cập nhật

}