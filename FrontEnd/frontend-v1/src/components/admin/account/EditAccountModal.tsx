// src/components/admin/account/EditAccountModal.tsx
import { Modal, Form, Input, Button, Select, message, Tag, Avatar } from "antd";
import { useState, useEffect } from "react";
import { EditOutlined, UserOutlined, LockOutlined } from "@ant-design/icons";
import { updateUser } from "../../../service/admin/userService";
import { User } from "../../../types/user";
import { getProfilePictureUrl } from "../../../utils/constants";
import ChangePasswordModal from "./ChangePasswordModal";

interface EditAccountModalProps {
  visible: boolean;
  account: User | null;
  onClose: () => void;
  onSuccess: () => void;
}

interface FormValues {
  fullName: string;
  roles: string[];
  isVerified: boolean;
  status: "ACTIVE" | "SUSPENDED" | "DELETED" | "PENDING_VERIFICATION";
}

const EditAccountModal: React.FC<EditAccountModalProps> = ({
  visible,
  account,
  onClose,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [isChangePasswordModalVisible, setIsChangePasswordModalVisible] = useState(false);

  // Reset form khi account thay đổi
  useEffect(() => {
    if (account && visible) {
      form.setFieldsValue({
        fullName: account.fullName || "",
        roles: account.roles?.map(role => role.name) || ["USER"],
        isVerified: account.isVerified || false,
        status: account.status || "ACTIVE"
      });
    }
  }, [account, visible, form]);

  const handleSubmit = async (values: FormValues) => {
    if (!account) return;
    
    setLoading(true);
    try {
      const updateData = {
        fullName: values.fullName,
        roles: values.roles.map((role) => ({ name: role })),
        isVerified: values.isVerified,
        status: values.status,
      };
      
      await updateUser(account.id, updateData);
      message.success("Cập nhật tài khoản thành công!");
      form.resetFields();
      onSuccess();
      onClose();
    } catch (error: unknown) {
      console.error('Error updating user:', error);
      message.error("Cập nhật tài khoản thất bại!");
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

  const handleOpenChangePasswordModal = () => {
    setIsChangePasswordModalVisible(true);
  };

  const handleCloseChangePasswordModal = () => {
    setIsChangePasswordModalVisible(false);
  };

  const getStatusColor = (status: string) => {
    const statusMap: Record<string, string> = {
      ACTIVE: 'green',
      PENDING_VERIFICATION: 'yellow',
      DELETED: 'red',
      SUSPENDED: 'orange',
    };
    return statusMap[status] || 'gray';
  };

  const getStatusLabel = (status: string) => {
    const statusMap: Record<string, string> = {
      ACTIVE: 'Hoạt động',
      PENDING_VERIFICATION: 'Chờ xác nhận',
      DELETED: 'Đã xóa',
      SUSPENDED: 'Tạm khóa',
    };
    return statusMap[status] || status;
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
      className="edit-account-modal"
    >
      {account ? (
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
                position: "relative",
                display: "flex",
                alignItems: "center",
                justifyContent: "center"
              }}>
                <Avatar 
                  size={56} 
                  src={getProfilePictureUrl(account.profilePicture)}
                  icon={<UserOutlined />}
                  style={{
                    border: "3px solid rgba(255, 255, 255, 0.3)",
                    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)"
                  }}
                />
                <div style={{
                  position: "absolute",
                  bottom: "-2px",
                  right: "-2px",
                  width: "20px",
                  height: "20px",
                  background: "rgba(255, 255, 255, 0.2)",
                  borderRadius: "50%",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  border: "2px solid rgba(255, 255, 255, 0.3)"
                }}>
                  <EditOutlined style={{ fontSize: "10px", color: "#ffffff" }} />
                </div>
              </div>
              <div>
                <h2 style={{
                  margin: 0,
                  color: "#ffffff",
                  fontSize: "22px",
                  fontWeight: "600",
                  textShadow: "0 2px 4px rgba(0, 0, 0, 0.1)"
                }}>
                  Chỉnh Sửa Tài Khoản
                </h2>
                <p style={{
                  margin: "4px 0 0 0",
                  color: "rgba(255, 255, 255, 0.9)",
                  fontSize: "14px"
                }}>
                  @{account.username} (ID: {account.id})
                </p>
                <div style={{ marginTop: "8px", display: "flex", gap: "8px" }}>
                  <Tag 
                    color={getStatusColor(account.status)}
                    style={{ 
                      background: "rgba(255, 255, 255, 0.2)",
                      color: "#ffffff",
                      border: "1px solid rgba(255, 255, 255, 0.3)",
                      fontWeight: "500",
                      fontSize: "11px"
                    }}
                  >
                    {getStatusLabel(account.status)}
                  </Tag>
                </div>
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
                    Thông tin cơ bản
                  </h3>

                  <div style={{ marginBottom: "16px" }}>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Tên đăng nhập:</span>
                    <p style={{ 
                      margin: "2px 0 0 0", 
                      color: "#1f2937", 
                      fontWeight: "500",
                      padding: "8px 12px",
                      background: "#f9fafb",
                      borderRadius: "6px",
                      border: "1px solid #e5e7eb"
                    }}>
                      {account.username}
                    </p>
                  </div>

                  <div style={{ marginBottom: "16px" }}>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Email:</span>
                    <p style={{ 
                      margin: "2px 0 0 0", 
                      color: "#1f2937", 
                      fontWeight: "500",
                      padding: "8px 12px",
                      background: "#f9fafb",
                      borderRadius: "6px",
                      border: "1px solid #e5e7eb"
                    }}>
                      {account.email}
                    </p>
                  </div>
                  
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

                  <div style={{ marginBottom: "0" }}>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Mật khẩu:</span>
                    <Button
                      onClick={handleOpenChangePasswordModal}
                      icon={<LockOutlined />}
                      style={{
                        marginTop: "4px",
                        width: "100%",
                        borderRadius: "8px",
                        border: "1px solid #e5e7eb",
                        padding: "8px 12px",
                        height: "40px",
                        fontSize: "14px",
                        fontWeight: "500",
                        color: "#374151",
                        background: "#ffffff",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        gap: "8px"
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.borderColor = "#ef4444";
                        e.currentTarget.style.color = "#ef4444";
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.borderColor = "#e5e7eb";
                        e.currentTarget.style.color = "#374151";
                      }}
                    >
                      Đổi Mật Khẩu
                    </Button>
                  </div>
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
                    Phân quyền & Trạng thái
                  </h3>

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
                    name="status"
                    label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Trạng thái</span>}
                    rules={[{ required: true, message: "Vui lòng chọn trạng thái!" }]}
                    style={{ marginBottom: "16px" }}
                  >
                    <Select 
                      placeholder="Chọn trạng thái tài khoản"
                      style={{
                        borderRadius: "8px"
                      }}
                    >
                      <Select.Option value="ACTIVE">Hoạt động</Select.Option>
                      <Select.Option value="PENDING_VERIFICATION">Chờ xác nhận</Select.Option>
                      <Select.Option value="SUSPENDED">Tạm khóa</Select.Option>
                      <Select.Option value="DELETED">Đã xóa</Select.Option>
                    </Select>
                  </Form.Item>

                  <Form.Item
                    name="isVerified"
                    label={<span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Trạng thái xác thực</span>}
                    style={{ marginBottom: "0" }}
                  >
                    <Select 
                      placeholder="Chọn trạng thái xác thực"
                      style={{
                        borderRadius: "8px"
                      }}
                    >
                      <Select.Option value={true}>Đã xác thực</Select.Option>
                      <Select.Option value={false}>Chưa xác thực</Select.Option>
                    </Select>
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
                  {loading ? "Đang cập nhật..." : "Cập Nhật"}
                </Button>
              </div>
            </Form>
          </div>
        </div>
      ) : (
        <div style={{ 
          padding: "40px", 
          textAlign: "center",
          background: "#ffffff"
        }}>
          <p style={{ 
            margin: 0, 
            color: "#6b7280", 
            fontSize: "16px" 
          }}>
            Không có thông tin tài khoản để chỉnh sửa.
          </p>
        </div>
      )}

      {/* Change Password Modal */}
      <ChangePasswordModal
        visible={isChangePasswordModalVisible}
        username={account?.username ?? ""}
        onClose={handleCloseChangePasswordModal}
      />
    </Modal>
  );
};

export default EditAccountModal;
