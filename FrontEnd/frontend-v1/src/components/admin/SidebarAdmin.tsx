// src/components/admin/SidebarAdmin.tsx
import { Menu, Layout, Tooltip } from "antd";
import { useNavigate, useLocation } from "react-router-dom";
import { UserOutlined, LogoutOutlined } from "@ant-design/icons";

const { Sider } = Layout;

const SidebarAdmin: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Sider width={80}
      style={{
        background: "linear-gradient(to bottom,rgb(24, 128, 129), #DDFFE7)",
        boxShadow: "2px 0 8px rgba(0, 0, 0, 0.1)",
        position: "fixed", // Cố định sidebar để đồng bộ với header
        height: "calc(100vh - 64px)", // Trừ chiều cao header
        top: "64px", // Đặt dưới header
        zIndex: 999,
      }}>
      <Menu
        mode="vertical"
        selectedKeys={[location.pathname]}
        onClick={({ key }) => void navigate(key)}
        items={[
          {
            key: "/admin/accounts",
            label: (
              <Tooltip title="Quản Lý Tài Khoản" placement="right">
                <UserOutlined style={{ fontSize: "30px", color: "#167d7f" }} />
              </Tooltip>
            ),
          },
          {
            key: "/login",
            label: (
              <Tooltip title="Đăng Xuất" placement="right">
                <LogoutOutlined style={{ fontSize: "30px", color: "#000" }} />
              </Tooltip>
            ),
          },
        ]}
        style={{ background: "#ffffff", borderRight: 0, textAlign: "center" }}
      />
    </Sider>
  );
};

export default SidebarAdmin;