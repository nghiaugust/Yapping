// src/layouts/LayoutAdmin.tsx
import { Layout } from "antd";
import { Outlet } from "react-router-dom";
import SidebarAdmin from "../components/admin/SidebarAdmin";
import Header from "../components/common/Header";
import { useState } from "react";

const { Content} = Layout;

const LayoutAdmin: React.FC = () => {
  const [refreshKey, setRefreshKey] = useState(0);

  // Callback để làm mới nội dung
  const handleRefreshContent = () => {
    setRefreshKey((prev) => prev + 1); // Thay đổi key để buộc render lại Outlet
  };
  return (
    <Layout style={{ minHeight: "100vh", background: "#ffffff" }}>
      {/* Sử dụng Header mới */}
      <Header
        onRefreshContent={handleRefreshContent}
        username="John Doe" // Thay bằng dữ liệu thực tế từ state hoặc API
        avatarUrl="https://example.com/avatar.jpg" // Thay bằng URL thực tế
      />
      
      {/* Nội dung chính */}
      <Layout style={{ marginTop: "64px" }}>
        <SidebarAdmin />
        <Layout style={{ background: "#ffffff" }}>
          <Content
            style={{
              padding: "24px 16px",
              minHeight: "calc(100vh - 70px)",
              display: "flex",
              justifyContent: "center",
            }}
          >
            <div
              style={{
                background: "#fff",
                borderRadius: "12px", //Bo góc
                maxWidth: "1000px", //Chiều rộng tối đa
                width: "100%",//Đảm bảo container chiếm toàn bộ chiều rộng có sẵn nhưng không vượt quá maxWidth
                padding: "16px",
                boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)", //Tạo hiệu ứng bóng nhẹ
                border: "1px solid #e0e0e0", // Thêm viền xám nhạt
              }}
            >
              <Outlet key={refreshKey} /> {/* Key thay đổi để buộc render lại */}
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default LayoutAdmin;