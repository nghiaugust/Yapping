// src/layouts/PageNotFound.tsx
import { Button, Result } from "antd";
import { useNavigate } from "react-router-dom";

const PageNotFound: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
        background: "#181818",
      }}
    >
      <Result
        status="404"
        title="404"
        subTitle="Xin lỗi, trang bạn truy cập không tồn tại."
        extra={
          <Button type="primary" onClick={() => void navigate("/")}>
            Về Trang Chủ
          </Button>
        }
        style={{ background: "#fff", borderRadius: "12px", padding: "24px" }}
      />
    </div>
  );
};

export default PageNotFound;