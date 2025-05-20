// src/components/user/SidebarUser.tsx
import { Menu, Layout, Tooltip } from "antd";
import { useNavigate, useLocation } from "react-router-dom";
import { HomeOutlined, UserOutlined, EditOutlined, FileTextOutlined, SearchOutlined, BookOutlined } from "@ant-design/icons";

const { Sider } = Layout;

const SidebarUser: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const iconColor = "#ffffff"; // Màu icon mặc định

  return (
    <Sider
      width={80}
      style={{
        background: "linear-gradient(to bottom,rgb(24, 128, 129), #DDFFE7)",
        boxShadow: "2px 0 8px rgba(0, 0, 0, 0.1)",
        position: "fixed", // Cố định sidebar để đồng bộ với header
        height: "calc(100vh - 64px)", // Trừ chiều cao header
        top: "64px", // Đặt dưới header
        zIndex: 999,
      }}
    >
      <Menu className="sidebar-menu"
        mode="vertical"
        selectedKeys={[location.pathname]}
        onClick={({ key }) => void navigate(key)}
        items={[
          {
            key: "/",
            label: (
              <Tooltip title="Trang Chủ" placement="right" color="#167d7f">
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                  <HomeOutlined style={{ fontSize: "30px", color: iconColor }} />
                </div>
              </Tooltip>
            ),
          },
          {
            key: "/profile",
            label: (
              <Tooltip title="Hồ Sơ Người Dùng" placement="right" color="#167d7f">
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                  <UserOutlined style={{ fontSize: "30px", color: iconColor }} />
                </div>
              </Tooltip>
            ),
          },
          {
            key: "/create-post",
            label: (
              <Tooltip title="Tạo Bài Đăng" placement="right" color="#167d7f">
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                  <EditOutlined style={{ fontSize: "30px", color: iconColor }} />
                </div>
              </Tooltip>
            ),
          },
          {
            key: "/resources",
            label: (
              <Tooltip title="Tài Liệu" placement="right" color="#167d7f">
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                  <FileTextOutlined style={{ fontSize: "30px", color: iconColor }} />
                </div>
              </Tooltip>
            ),
          },
          {
            key: "/search",
            label: (
              <Tooltip title="Tìm Kiếm" placement="right" color="#167d7f">
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                  <SearchOutlined style={{ fontSize: "30px", color: iconColor }} />
                </div>
              </Tooltip>
            ),
          },
          {
            key: "/bookmarks",
            label: (
              <Tooltip title="Đã Đánh Dấu" placement="right" color="#167d7f">
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                  <BookOutlined style={{ fontSize: "30px", color: iconColor }} />
                </div>
              </Tooltip>
            ),
          },
        ]}
        style={{
          background: "transparent", // Trong suốt để lộ gradient của Sider
          borderRight: 0,
        }}
      />
    </Sider>
  );
};

export default SidebarUser;