// src/components/admin/SidebarAdmin.tsx
import { Layout, Menu, Typography, Avatar, Divider } from "antd";
import { useNavigate, useLocation } from "react-router-dom";
import { 
  UserOutlined, 
  LogoutOutlined, 
  DashboardOutlined, 
  SettingOutlined,
  TeamOutlined,
  FileTextOutlined,
  EditOutlined
} from "@ant-design/icons";
import { useState } from "react";
import "../../assets/styles/sidebar-admin.css";

const { Sider } = Layout;
const { Text } = Typography;

interface SidebarAdminProps {
  onCollapseChange?: (collapsed: boolean) => void;
}

const SidebarAdmin: React.FC<SidebarAdminProps> = ({ onCollapseChange }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);

  const handleCollapse = (collapsed: boolean) => {
    setCollapsed(collapsed);
    onCollapseChange?.(collapsed);
  };

  const handleLogout = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_role');
    void navigate('/login');
  };

  const menuItems = [
    {
      key: "/admin/accounts",
      icon: <TeamOutlined />,
      label: "Quản Lý Tài Khoản",
    },
    {
      key: "/admin/posts",
      icon: <EditOutlined />,
      label: "Quản Lý Bài Đăng",
    },
    {
      key: "/admin/reports",
      icon: <FileTextOutlined />,
      label: "Quản Lý Báo Cáo",
    },
    {
      key: "/admin/dashboard",
      icon: <DashboardOutlined />,
      label: "Bảng Điều Khiển",
    },
    {
      key: "/admin/settings",
      icon: <SettingOutlined />,
      label: "Cài Đặt",
    },
  ];

  return (
    <Sider 
      collapsible
      collapsed={collapsed}
      onCollapse={handleCollapse}
      width={280}
      collapsedWidth={80}
      style={{
        background: "linear-gradient(180deg, #f8faff 0%, #f1f5ff 50%, #eef2ff 100%)",
        boxShadow: "4px 0 20px rgba(86, 128, 233, 0.08)",
        position: "fixed",
        height: "100vh",
        left: 0,
        top: 0,
        zIndex: 1000,
        borderRight: "1px solid rgba(86, 128, 233, 0.1)",
      }}
    >
      {/* Header của Sidebar */}
      <div style={{
        padding: collapsed ? "20px 12px" : "24px 20px",
        textAlign: "center",
        borderBottom: "1px solid rgba(86, 128, 233, 0.15)",
        background: "rgba(86, 128, 233, 0.05)",
        backdropFilter: "blur(10px)",
      }}>
        <Avatar 
          size={collapsed ? 40 : 60} 
          style={{ 
            background: "linear-gradient(45deg, #5680E9, #84CEEB)",
            marginBottom: collapsed ? 0 : 12,
            border: "3px solid rgba(86, 128, 233, 0.2)",
            boxShadow: "0 4px 12px rgba(86, 128, 233, 0.15)"
          }}
          icon={<UserOutlined />} 
        />
        {!collapsed && (
          <>
            <div style={{ marginTop: 8 }}>
              <Text style={{ 
                color: "#5680E9", 
                fontSize: "16px", 
                fontWeight: "600",
                textShadow: "none"
              }}>
                Admin Panel
              </Text>
            </div>
            <div style={{ marginTop: 4 }}>
              <Text style={{ 
                color: "rgba(86, 128, 233, 0.7)", 
                fontSize: "12px"
              }}>
                Quản trị hệ thống
              </Text>
            </div>
          </>
        )}
      </div>

      {/* Menu Navigation */}
      <div style={{ padding: "20px 0" }}>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          onClick={({ key }) => void navigate(key)}
          items={menuItems}
          className="sidebar-menu"
          style={{
            background: "transparent",
            border: "none",
            fontSize: "14px",
          }}
          theme="light"
          inlineIndent={24}
          inlineCollapsed={collapsed}
        />
      </div>

      {/* Spacer */}
      <div style={{ flex: 1 }} />

      {/* Footer Section */}
      <div style={{
        padding: collapsed ? "12px" : "20px",
        background: "rgba(86, 128, 233, 0.05)",
        borderTop: "1px solid rgba(86, 128, 233, 0.15)",
      }}>
        {!collapsed && (
          <Divider style={{ 
            borderColor: "rgba(86, 128, 233, 0.2)", 
            margin: "12px 0" 
          }} />
        )}
        
        <div
          onClick={handleLogout}
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: collapsed ? "center" : "flex-start",
            padding: "12px 16px",
            borderRadius: "8px",
            cursor: "pointer",
            transition: "all 0.3s ease",
            background: "transparent",
            color: "#5680E9",
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.background = "rgba(86, 128, 233, 0.1)";
            e.currentTarget.style.transform = "translateX(4px)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.background = "transparent";
            e.currentTarget.style.transform = "translateX(0)";
          }}
        >
          <LogoutOutlined style={{ 
            fontSize: "16px",
            marginRight: collapsed ? 0 : "12px"
          }} />
          {!collapsed && (
            <Text style={{ 
              color: "inherit", 
              fontSize: "14px",
              fontWeight: "500"
            }}>
              Đăng Xuất
            </Text>
          )}
        </div>
      </div>
    </Sider>
  );
};

export default SidebarAdmin;