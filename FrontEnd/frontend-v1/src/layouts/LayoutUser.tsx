// src/layouts/LayoutUser.tsx
import { Layout } from "antd";
import { Outlet } from "react-router-dom";
import Header from "../components/common/Header";
import { useState } from "react";

const { Content } = Layout;

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
    {/* Sử dụng Header  */}
      <Header
        onRefreshContent={handleRefreshContent}
      />
        {/* Nội dung chính */}
      <Layout style={{ marginTop: "74px" }}>        
        <Content
          style={{
            padding: "0px",
            minHeight: "calc(100vh - 80px)",
            display: "flex",
            justifyContent: "center",
          }}
        >          <div
            style={{
              background: "#fff",
              borderRadius: "12px",
              maxWidth: "600px",
              width: "100%",
              padding: "0px",
              boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)",
              border: "1px solid #8860D0", // Viền xanh bao quanh
              overflow: "hidden" // Đảm bảo nội dung không vượt ra ngoài bo viền
            }}
          >
            <Outlet key={refreshKey} /> {/* Key thay đổi để buộc render lại */}
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default LayoutUser;
