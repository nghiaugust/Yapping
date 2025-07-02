// src/components/admin/account/ChangePasswordModal.tsx
import { Modal, Form, Input, Button, message } from "antd";
import { useState } from "react";
import { LockOutlined } from "@ant-design/icons";
import { changePassword } from "../../../service/admin/userService";

interface ChangePasswordModalProps {
  visible: boolean;
  username: string;
  onClose: () => void;
}

interface FormValues {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

const ChangePasswordModal: React.FC<ChangePasswordModalProps> = ({
  visible,
  username,
  onClose,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: FormValues) => {
    setLoading(true);
    try {
      await changePassword({ 
        currentPassword: values.currentPassword,
        newPassword: values.newPassword 
      });
      message.success("Đổi mật khẩu thành công!");
      form.resetFields();
      onClose();
    } catch (error: unknown) {
      console.error('Error changing password:', error);
      // Hiển thị error message chi tiết từ backend
      if (error && typeof error === 'object' && 'response' in error) {
        const apiError = error as { response: { data: { message: string } } };
        message.error(apiError.response?.data?.message || "Đổi mật khẩu thất bại!");
      } else {
        message.error("Đổi mật khẩu thất bại!");
      }
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
      width={500}
      centered
      footer={null}
      closable={false}
      styles={{
        body: { padding: 0 }
      }}
      className="change-password-modal"
    >
      <div style={{ 
        background: "linear-gradient(135deg, #f8faff 0%, #f1f5ff 100%)",
        borderRadius: "8px",
        overflow: "hidden"
      }}>
        {/* Header Section */}
        <div style={{
          background: "linear-gradient(135deg, #EF4444, #F87171)",
          padding: "24px",
          color: "#ffffff",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between"
        }}>
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
            }}>
              <LockOutlined style={{ fontSize: "24px", color: "#ffffff" }} />
            </div>
            <div>
              <h2 style={{
                margin: 0,
                color: "#ffffff",
                fontSize: "20px",
                fontWeight: "600",
                textShadow: "0 2px 4px rgba(0, 0, 0, 0.1)"
              }}>
                Đổi Mật Khẩu
              </h2>
              <p style={{
                margin: "4px 0 0 0",
                color: "rgba(255, 255, 255, 0.9)",
                fontSize: "14px"
              }}>
                Thay đổi mật khẩu cho: @{username}
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
          padding: "24px"
        }}>
          <Form
            form={form}
            layout="vertical"
            onFinish={handleFormSubmit}
          >
            <div style={{
              background: "#ffffff",
              padding: "24px",
              borderRadius: "12px",
              boxShadow: "0 2px 8px rgba(239, 68, 68, 0.06)",
              border: "1px solid rgba(239, 68, 68, 0.1)"
            }}>
              <div style={{
                marginBottom: "20px",
                padding: "16px",
                background: "#fef2f2",
                borderRadius: "8px",
                border: "1px solid #fecaca"
              }}>
                <p style={{
                  margin: 0,
                  color: "#dc2626",
                  fontSize: "14px",
                  fontWeight: "500"
                }}>
                  ⚠️ Lưu ý: Bạn cần nhập mật khẩu hiện tại để xác thực trước khi đổi mật khẩu mới.
                </p>
              </div>

              <Form.Item
                name="currentPassword"
                label={<span style={{ color: "#374151", fontSize: "14px", fontWeight: "600" }}>Mật khẩu hiện tại</span>}
                rules={[
                  { required: true, message: "Vui lòng nhập mật khẩu hiện tại!" }
                ]}
                style={{ marginBottom: "16px" }}
              >
                <Input.Password 
                  placeholder="Nhập mật khẩu hiện tại"
                  prefix={<LockOutlined style={{ color: "#9ca3af" }} />}
                  style={{
                    borderRadius: "8px",
                    border: "1px solid #d1d5db",
                    padding: "10px 12px",
                    fontSize: "14px"
                  }}
                />
              </Form.Item>

              <Form.Item
                name="newPassword"
                label={<span style={{ color: "#374151", fontSize: "14px", fontWeight: "600" }}>Mật khẩu mới</span>}
                rules={[
                  { required: true, message: "Vui lòng nhập mật khẩu mới!" },
                  { min: 6, message: "Mật khẩu phải có ít nhất 6 ký tự!" }
                ]}
                style={{ marginBottom: "16px" }}
              >
                <Input.Password 
                  placeholder="Nhập mật khẩu mới"
                  prefix={<LockOutlined style={{ color: "#9ca3af" }} />}
                  style={{
                    borderRadius: "8px",
                    border: "1px solid #d1d5db",
                    padding: "10px 12px",
                    fontSize: "14px"
                  }}
                />
              </Form.Item>

              <Form.Item
                name="confirmPassword"
                label={<span style={{ color: "#374151", fontSize: "14px", fontWeight: "600" }}>Xác nhận mật khẩu</span>}
                dependencies={['newPassword']}
                rules={[
                  { required: true, message: "Vui lòng xác nhận mật khẩu!" },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('newPassword') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('Mật khẩu xác nhận không khớp!'));
                    },
                  }),
                ]}
                style={{ marginBottom: "0" }}
              >
                <Input.Password 
                  placeholder="Nhập lại mật khẩu mới"
                  prefix={<LockOutlined style={{ color: "#9ca3af" }} />}
                  style={{
                    borderRadius: "8px",
                    border: "1px solid #d1d5db",
                    padding: "10px 12px",
                    fontSize: "14px"
                  }}
                />
              </Form.Item>
            </div>

            {/* Footer Buttons */}
            <div style={{
              display: "flex",
              justifyContent: "flex-end",
              gap: "12px",
              marginTop: "20px"
            }}>
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
                danger
                style={{
                  borderRadius: "8px",
                  padding: "8px 24px",
                  height: "40px",
                  fontWeight: "500",
                  boxShadow: "0 2px 8px rgba(239, 68, 68, 0.2)"
                }}
                onMouseEnter={(e) => {
                  if (!loading) {
                    e.currentTarget.style.transform = "translateY(-1px)";
                    e.currentTarget.style.boxShadow = "0 4px 12px rgba(239, 68, 68, 0.3)";
                  }
                }}
                onMouseLeave={(e) => {
                  if (!loading) {
                    e.currentTarget.style.transform = "translateY(0)";
                    e.currentTarget.style.boxShadow = "0 2px 8px rgba(239, 68, 68, 0.2)";
                  }
                }}
              >
                {loading ? "Đang đổi..." : "Đổi Mật Khẩu"}
              </Button>
            </div>
          </Form>
        </div>
      </div>
    </Modal>
  );
};

export default ChangePasswordModal;
