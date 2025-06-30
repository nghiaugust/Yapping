// src/components/admin/account/CreateAccountModal.tsx
import { Modal, Form, Input, Button, Select, message } from "antd";
import { useState } from "react";
import { UserAddOutlined } from "@ant-design/icons";
import { createUser } from "../../../service/admin/userService";

interface CreateAccountModalProps {
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

interface FormValues {
  username: string;
  email: string;
  password: string;
  fullName: string;
  bio?: string;
  roles: string[];
}

const CreateAccountModal: React.FC<CreateAccountModalProps> = ({
  visible,
  onClose,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: FormValues) => {
    setLoading(true);
    try {
      const userData = {
        username: values.username,
        email: values.email,
        password: values.password,
        fullName: values.fullName,
        bio: values.bio ?? "",
        roles: values.roles.map((role) => ({ name: role })),
      };
      
      await createUser(userData);
      message.success("Tạo tài khoản thành công!");
      form.resetFields();
      onSuccess();
      onClose();
    } catch (error: unknown) {
      console.error('Error creating user:', error);
      message.error("Tạo tài khoản thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleFormSubmit = (values: FormValues) => {
    void handleSubmit(values);
  };

  const handleCancel = () => {
    form.resetFields();
    onClose();
  };

  return (
    <Modal
      title={null}
      open={visible}
      onCancel={handleCancel}
      width={800}
      centered
      footer={null}
      closable={false}
      styles={{
        body: { padding: 0 }
      }}
      className="create-account-modal"
    >
      <div style={{ 
        background: "linear-gradient(135deg, #f8faff 0%, #f1f5ff 100%)",
        borderRadius: "8px",
        maxHeight: "85vh",
        overflow: "hidden",
        display: "flex",
        flexDirection: "column"
      }}>
        {/* Header Section */}
        <div style={{
          background: "linear-gradient(135deg, #5680E9, #84CEEB)",
          padding: "24px",
          color: "#ffffff",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexShrink: 0
        }} className="header-content">
          <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
            <div style={{
              width: "48px",
              height: "48px",
              background: "rgba(255, 255, 255, 0.2)",
              borderRadius: "12px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              border: "2px solid rgba(255, 255, 255, 0.3)"
            }} className="header-avatar">
              <UserAddOutlined style={{ fontSize: "24px", color: "#ffffff" }} />
            </div>
            <div>
              <h2 style={{
                margin: 0,
                color: "#ffffff",
                fontSize: "22px",
                fontWeight: "600",
                textShadow: "0 2px 4px rgba(0, 0, 0, 0.1)"
              }}>
                Tạo Tài Khoản Mới
              </h2>
              <p style={{
                margin: "4px 0 0 0",
                color: "rgba(255, 255, 255, 0.9)",
                fontSize: "14px"
              }}>
                Điền thông tin để tạo tài khoản mới
              </p>
            </div>
          </div>
          <Button
            type="text"
            onClick={handleCancel}
            style={{
              color: "rgba(255, 255, 255, 0.8)",
              fontSize: "16px",
              padding: "8px",
              borderRadius: "6px"
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = "rgba(255, 255, 255, 0.1)";
              e.currentTarget.style.color = "#ffffff";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = "transparent";
              e.currentTarget.style.color = "rgba(255, 255, 255, 0.8)";
            }}
          >
            ✕
          </Button>
        </div>

        {/* Content Section */}
        <div style={{ 
          padding: "24px",
          overflow: "auto",
          flex: 1
        }} className="form-section">
          <Form
            form={form}
            layout="vertical"
            onFinish={handleFormSubmit}
            initialValues={{ roles: ["USER"] }}
            style={{ height: "100%" }}
          >
            {/* Form Grid */}
            <div style={{ 
              display: "grid", 
              gridTemplateColumns: "1fr 1fr", 
              gap: "20px",
              marginBottom: "20px"
            }} className="form-grid">
              {/* Left Column */}
              <div style={{
                background: "#ffffff",
                padding: "20px",
                borderRadius: "12px",
                boxShadow: "0 2px 8px rgba(86, 128, 233, 0.06)",
                border: "1px solid rgba(86, 128, 233, 0.1)"
              }}>
                <h3 style={{
                  margin: "0 0 16px 0",
                  color: "#374151",
                  fontSize: "16px",
                  fontWeight: "600"
                }}>
                  Thông tin đăng nhập
                </h3>
                
                <Form.Item
                  name="username"
                  label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Tên đăng nhập</span>}
                  rules={[
                    { required: true, message: "Vui lòng nhập tên đăng nhập!" },
                    { min: 3, message: "Tên đăng nhập phải có ít nhất 3 ký tự!" },
                    { pattern: /^[a-zA-Z0-9_]+$/, message: "Tên đăng nhập chỉ được chứa chữ, số và dấu gạch dưới!" }
                  ]}
                  style={{ marginBottom: "16px" }}
                >
                  <Input 
                    placeholder="Nhập tên đăng nhập"
                    style={{
                      borderRadius: "8px",
                      border: "1px solid #e5e7eb",
                      padding: "8px 12px",
                      fontSize: "14px"
                    }}
                  />
                </Form.Item>

                <Form.Item
                  name="email"
                  label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Email</span>}
                  rules={[
                    { required: true, message: "Vui lòng nhập email!" },
                    { type: "email", message: "Email không đúng định dạng!" },
                  ]}
                  style={{ marginBottom: "16px" }}
                >
                  <Input 
                    placeholder="Nhập địa chỉ email"
                    style={{
                      borderRadius: "8px",
                      border: "1px solid #e5e7eb",
                      padding: "8px 12px",
                      fontSize: "14px"
                    }}
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Mật khẩu</span>}
                  rules={[
                    { required: true, message: "Vui lòng nhập mật khẩu!" },
                    { min: 6, message: "Mật khẩu phải có ít nhất 6 ký tự!" }
                  ]}
                  style={{ marginBottom: "0" }}
                >
                  <Input.Password 
                    placeholder="Nhập mật khẩu"
                    style={{
                      borderRadius: "8px",
                      border: "1px solid #e5e7eb",
                      padding: "8px 12px",
                      fontSize: "14px"
                    }}
                  />
                </Form.Item>
              </div>

              {/* Right Column */}
              <div style={{
                background: "#ffffff",
                padding: "20px",
                borderRadius: "12px",
                boxShadow: "0 2px 8px rgba(86, 128, 233, 0.06)",
                border: "1px solid rgba(86, 128, 233, 0.1)"
              }}>
                <h3 style={{
                  margin: "0 0 16px 0",
                  color: "#374151",
                  fontSize: "16px",
                  fontWeight: "600"
                }}>
                  Thông tin cá nhân
                </h3>

                <Form.Item
                  name="fullName"
                  label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Họ và tên</span>}
                  rules={[
                    { required: true, message: "Vui lòng nhập họ và tên!" },
                    { min: 2, message: "Họ và tên phải có ít nhất 2 ký tự!" }
                  ]}
                  style={{ marginBottom: "16px" }}
                >
                  <Input 
                    placeholder="Nhập họ và tên đầy đủ"
                    style={{
                      borderRadius: "8px",
                      border: "1px solid #e5e7eb",
                      padding: "8px 12px",
                      fontSize: "14px"
                    }}
                  />
                </Form.Item>

                <Form.Item
                  name="roles"
                  label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Vai trò</span>}
                  rules={[{ required: true, message: "Vui lòng chọn ít nhất một vai trò!" }]}
                  style={{ marginBottom: "16px" }}
                >
                  <Select 
                    mode="multiple" 
                    placeholder="Chọn vai trò cho tài khoản"
                    style={{
                      borderRadius: "8px"
                    }}
                  >
                    <Select.Option value="ADMIN">Admin</Select.Option>
                    <Select.Option value="USER">User</Select.Option>
                  </Select>
                </Form.Item>

                <Form.Item 
                  name="bio" 
                  label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Tiểu sử (không bắt buộc)</span>}
                  style={{ marginBottom: "0" }}
                >
                  <Input.TextArea 
                    rows={3}
                    placeholder="Mô tả ngắn về người dùng..."
                    style={{
                      borderRadius: "8px",
                      border: "1px solid #e5e7eb",
                      padding: "8px 12px",
                      fontSize: "14px",
                      resize: "none"
                    }}
                  />
                </Form.Item>
              </div>
            </div>

            {/* Footer Buttons */}
            <div style={{
              display: "flex",
              justifyContent: "flex-end",
              gap: "12px",
              paddingTop: "16px",
              borderTop: "1px solid rgba(86, 128, 233, 0.1)"
            }} className="footer-buttons">
              <Button
                onClick={handleCancel}
                style={{
                  borderRadius: "8px",
                  padding: "8px 24px",
                  height: "40px",
                  fontWeight: "500",
                  border: "1px solid #d1d5db",
                  color: "#6b7280"
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.borderColor = "#9ca3af";
                  e.currentTarget.style.color = "#374151";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.borderColor = "#d1d5db";
                  e.currentTarget.style.color = "#6b7280";
                }}
              >
                Hủy
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                style={{
                  background: "linear-gradient(135deg, #5680E9, #84CEEB)",
                  border: "none",
                  borderRadius: "8px",
                  padding: "8px 24px",
                  height: "40px",
                  fontWeight: "500",
                  boxShadow: "0 2px 8px rgba(86, 128, 233, 0.2)"
                }}
                onMouseEnter={(e) => {
                  if (!loading) {
                    e.currentTarget.style.transform = "translateY(-1px)";
                    e.currentTarget.style.boxShadow = "0 4px 12px rgba(86, 128, 233, 0.3)";
                  }
                }}
                onMouseLeave={(e) => {
                  if (!loading) {
                    e.currentTarget.style.transform = "translateY(0)";
                    e.currentTarget.style.boxShadow = "0 2px 8px rgba(86, 128, 233, 0.2)";
                  }
                }}
              >
                {loading ? "Đang tạo..." : "Tạo Tài Khoản"}
              </Button>
            </div>
          </Form>
        </div>
      </div>
    </Modal>
  );
};

export default CreateAccountModal;