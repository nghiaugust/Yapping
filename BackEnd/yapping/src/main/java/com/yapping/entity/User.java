package com.yapping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
// ràng buộc duy nhat
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
})
public class User {

    // Định nghĩa Enum cho Status
    public enum Status {
        ACTIVE, SUSPENDED, DELETED, PENDING_VERIFICATION
    }

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 100)
    private String fullName;

    //mô tả bản thân
    @Lob
    @Column(name = "bio")
    private String bio;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    // xác thực kiểu như tích xanh
    @ColumnDefault("0")
    @Column(name = "is_verified")
    private Boolean isVerified;

    // trạng thái tài khoản
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length=30)
    private Status status = Status.ACTIVE;

    // Thêm field FCM token
    @Column(name = "fcm_token")
    private String fcmToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false) // Không cho phép cập nhật sau khi tạo
    private Instant createdAt;

    @UpdateTimestamp // Tự động gán giá trị khi tạo và cập nhật
    @Column(name = "updated_at")
    private Instant updatedAt;
}