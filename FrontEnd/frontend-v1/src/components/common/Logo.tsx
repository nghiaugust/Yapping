// src/components/common/Logo.tsx
import React from "react";
import { useNavigate } from "react-router-dom";

interface LogoProps {
  size?: "small" | "medium" | "large";
  text?: string;
  style?: React.CSSProperties;
  reloadOnClick?: boolean; // Tải lại toàn bộ trang
  onRefreshContent?: () => void; // Callback để làm mới nội dung
}

const Logo: React.FC<LogoProps> = ({
  size = "medium",
  text = "yapping",
  style,
  reloadOnClick = false,
  onRefreshContent,
}) => {
  const navigate = useNavigate();

  const sizeStyles = {
    small: { height: "48px", fontSize: "16px", imgHeight: "24px" },
    medium: { height: "64px", fontSize: "20px", imgHeight: "40px" },
    large: { height: "80px", fontSize: "24px", imgHeight: "48px" },
  }[size];

  const handleClick = () => {
    if (reloadOnClick) {
      // Tải lại toàn bộ trang hiện tại
      window.location.reload();
    } else if (onRefreshContent) {
      // Gọi callback để làm mới nội dung
      onRefreshContent();
    } else {
      // Chuyển hướng về trang chủ
      navigate("/");
    }
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        fontWeight: "bold",
        color: "#000000",
        cursor: "pointer",
        background: "transparent", // Đảm bảo background trong suốt
        ...sizeStyles,
        ...style,
      }}
      onClick={handleClick}
      onMouseEnter={(e) => (e.currentTarget.style.background = "rgba(245, 245, 245, 0.3)")} // Hiệu ứng hover mờ
      onMouseLeave={(e) => (e.currentTarget.style.background = "transparent")} // Trở về trong suốt
    >
      {text}
      {/* Nếu dùng hình ảnh logo:
      <img src="/logo.png" alt="Web Logo" style={{ height: sizeStyles.imgHeight }} />
      */}
    </div>
  );
};

export default Logo;