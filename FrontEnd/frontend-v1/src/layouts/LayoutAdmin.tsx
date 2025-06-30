// src/layouts/LayoutAdmin.tsx
import { Layout } from "antd";
import { Outlet } from "react-router-dom";
import SidebarAdmin from "../components/admin/SidebarAdmin";
import { useState, useEffect } from "react";

const { Content } = Layout;

const LayoutAdmin: React.FC = () => {
  const [refreshKey] = useState(0);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  // Listen for sidebar collapse state changes
  useEffect(() => {
    const handleResize = () => {
      // Auto collapse on mobile
      if (window.innerWidth <= 768) {
        setSidebarCollapsed(true);
      }
    };

    window.addEventListener('resize', handleResize);
    handleResize(); // Check initial size

    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <Layout style={{ minHeight: "100vh", background: "#f8fafc" }}>
      <SidebarAdmin onCollapseChange={setSidebarCollapsed} />
      
      {/* Main Content Area */}
      <Layout 
        style={{ 
          marginLeft: sidebarCollapsed ? "80px" : "280px", // Dynamic margin based on sidebar state
          background: "#f8fafc",
          transition: "margin-left 0.3s ease"
        }}
        className="admin-main-layout"
      >
        <Content
          style={{
            minHeight: "100vh",
            background: "#f8fafc",
          }}
        >
          {/* Full content without container wrapper */}
          <Outlet key={refreshKey} />
        </Content>
      </Layout>
    </Layout>
  );
};

export default LayoutAdmin;