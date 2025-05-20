// src/layouts/LayoutUser.tsx
import { Layout } from "antd";
import { Outlet } from "react-router-dom";
import SidebarUser from "../components/user/SidebarUser";
import Header from "../components/common/Header";
import { useState } from "react";

const {  Content } = Layout;

const LayoutUser: React.FC = () => {
  const [refreshKey, setRefreshKey] = useState(0);
  
    // Callback để làm mới nội dung
    const handleRefreshContent = () => {
      setRefreshKey((prev) => prev + 1); // Thay đổi key để buộc render lại Outlet
    };

  return (
    <Layout style={{  
      minHeight: "100vh", 
      background: "#ffffff", // Đảm bảo nền trắng
      }}>
      {/* Sử dụng Header mới */}
      <Header
        onRefreshContent={handleRefreshContent}
        username="John Doe" // Thay bằng dữ liệu thực tế từ state hoặc API
        avatarUrl="https://example.com/avatar.jpg" // Thay bằng URL thực tế
      />
      
      {/* Nội dung chính */}
      <Layout style={{ marginTop: "64px" }}>
        <SidebarUser />
        <Layout style={{ background: "transparent" ,
          marginLeft: "80px",
        }}>
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
                borderRadius: "12px",
                maxWidth: "600px",
                width: "100%",
                padding: "16px",
                boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)",
                border: "1px solid #98D7C2", // Thêm viền xanh nhạt
                //transition: "all 0.3s", // Hiệu ứng chuyển tiếp
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

export default LayoutUser;