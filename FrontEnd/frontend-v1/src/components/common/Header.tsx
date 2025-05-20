// src/components/common/Header.tsx
import { Input, Avatar, Space } from "antd";
import { SearchOutlined, UserOutlined } from "@ant-design/icons";
import Logo from "./Logo";
import Notifications from "../user/Notifications";
import Followers from "../user/Followers";
import React from "react";

interface HeaderProps {
  onRefreshContent: () => void;
  username?: string;
  avatarUrl?: string;
}

const Header: React.FC<HeaderProps> = ({ onRefreshContent, username, avatarUrl }) => {
  return (
    <div
      style={{
        background: "linear-gradient(to right, #167d7f, #29a0b1, #98D7C2, #DDFFE7)",
        padding: "0 16px",
        height: "64px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        position: "fixed",
        width: "100%",
        zIndex: 1000,
      }}
    >
      <Logo
        size="medium"
        text="yapping"
        style={{ width: "200px", color: "#ffffff", textShadow: "0 0 2px rgba(0, 0, 0, 0.5)" }} // Chữ trắng, viền nhẹ
        onRefreshContent={onRefreshContent}
      />
      <Input
        placeholder="Tìm kiếm..."
        prefix={<SearchOutlined style={{ color: "#29a0b1" }} />}
        style={{
          width: "300px",
          borderRadius: "20px",
          background: "#ffffff",
          border: "1px solid #29a0b1",
          transition: "all 0.3s",
        }}
        onMouseEnter={(e) => (e.currentTarget.style.borderColor = "#167d7f")}
        onMouseLeave={(e) => (e.currentTarget.style.borderColor = "#29a0b1")}
      />
      <Space size="large">
        <Notifications />
        <Followers />
        <Space>
          <span style={{ fontWeight: 500, color: "#ffffff", textShadow: "0 0 2px rgba(0, 0, 0, 0.3)" }}>
            {username ?? "User"}
          </span>
          <Avatar
            src={avatarUrl}
            icon={!avatarUrl && <UserOutlined />}
            size={40}
            style={{ border: "2px solid #167d7f", cursor: "pointer", backgroundColor: "#98D7C2" }}
          />
        </Space>
      </Space>
    </div>
  );
};

export default Header;