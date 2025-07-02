// src/components/common/Header.tsx
import { Input, Avatar, Space, Popover } from "antd";
import { SearchOutlined, UserOutlined, HomeOutlined, EditOutlined, FileTextOutlined, BookOutlined, DownOutlined } from "@ant-design/icons";
import Logo from "./Logo";
import Notifications from "../user/Notifications";
import Followers from "../user/Followers";
import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { getCurrentUser, UserProfile } from "../../service/user/userService";
import { getProfilePictureUrl } from "../../utils/constants";
import UserProfilePopover from "../user/UserProfilePopover";

interface HeaderProps {
  onRefreshContent: () => void;
}

const Header: React.FC<HeaderProps> = ({ onRefreshContent }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [currentUser, setCurrentUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [popoverVisible, setPopoverVisible] = useState(false);
  
  useEffect(() => {
    const fetchUser = async () => {
      try {
        setLoading(true);
        const response = await getCurrentUser();
        if (response.success) {
          setCurrentUser(response.data);
        }
      } catch (error) {
        console.error("Lỗi khi lấy thông tin người dùng:", error);
      } finally {
        setLoading(false);
      }
    };
      void fetchUser();
  }, []);
  
  const handleLogout = () => {
    // Xóa token
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_role');
      // Chuyển hướng về trang đăng nhập
    void navigate('/login');
  };
  
  return (    
  <div
      style={{
        //background: "linear-gradient(to right, #167d7f, #29a0b1, #98D7C2, #DDFFE7)",
        background: "linear-gradient(to right, #5680E9, #84CEEB, #5AB9EA, #8860D0)",

        padding: "0 16px",
        height: "64px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        position: "fixed",
        width: "100%",
        zIndex: 1000,
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.2)",
      }}
    >
      <div style={{ display: "flex", alignItems: "center" }}>
        <Logo
          size="medium"
          text="yapping"
          style={{ width: "160px", color: "#ffffff", textShadow: "0 0 2px rgba(0, 0, 0, 0.5)" }} // Chữ trắng, viền nhẹ
          onRefreshContent={onRefreshContent}
        />
        <Space size="middle">
          <HomeOutlined 
            style={{ 
              fontSize: "22px", 
              color: location.pathname === "/" ? "#C1C8E4" : "#ffffff",
              cursor: "pointer" 
            }} 
            onClick={() => void navigate("/")}
          />
          <EditOutlined 
            style={{ 
              fontSize: "22px", 
              color: location.pathname === "/create-post" ? "#C1C8E4" : "#ffffff",
              cursor: "pointer" 
            }} 
            onClick={() => void navigate("/create-post")}
          />
          <FileTextOutlined 
            style={{ 
              fontSize: "22px", 
              color: location.pathname === "/resources" ? "#C1C8E4" : "#ffffff",
              cursor: "pointer" 
            }} 
            onClick={() => void navigate("/resources")}
          />
          <BookOutlined 
            style={{ 
              fontSize: "22px", 
              color: location.pathname === "/bookmarks" ? "#C1C8E4" : "#ffffff",
              cursor: "pointer" 
            }} 
            onClick={() => void navigate("/bookmarks")}
          />
        </Space>
      </div>
      <Input
        placeholder="Tìm kiếm..."
        prefix={<SearchOutlined style={{ color: "#5AB9EA" }} />}
        style={{
          width: "300px",
          borderRadius: "20px",
          background: "#ffffff",
          border: "1px solid #5AB9EA",
          transition: "all 0.3s",
        }}
        onMouseEnter={(e) => (e.currentTarget.style.borderColor = "#8860D0")}
        onMouseLeave={(e) => (e.currentTarget.style.borderColor = "#5AB9EA")}
      />      <Space size="large">
        <Notifications />
        {currentUser && <Followers userId={currentUser.id} />}
        <Popover 
          content={<UserProfilePopover user={currentUser} loading={loading} onLogout={handleLogout} />}
          trigger="click"
          visible={popoverVisible}
          onVisibleChange={setPopoverVisible}
          placement="bottomRight"
          overlayStyle={{ width: 300 }}
        >
          <Space style={{ cursor: 'pointer' }}>
            <span style={{ fontWeight: 500, color: "#ffffff", textShadow: "0 0 2px rgba(0, 0, 0, 0.3)" }}>
              {currentUser?.fullName ?? "User"}
            </span>
            <div style={{ position: 'relative' }}>              <Avatar
                src={getProfilePictureUrl(currentUser?.profilePicture)}
                icon={!currentUser?.profilePicture && <UserOutlined />}
                size={40}
                style={{ border: "2px solid #8860D0", cursor: "pointer", backgroundColor: "#C1C8E4" }}
              />
              <DownOutlined 
                style={{ 
                  position: 'absolute',
                  right: -2,
                  bottom: -2,
                  fontSize: 12,
                  color: '#ffffff',
                  backgroundColor: '#8860D0',
                  borderRadius: '50%',
                  padding: '2px',
                }} 
              />
            </div>
          </Space>
        </Popover>
      </Space>
    </div>
  );
};

export default Header;