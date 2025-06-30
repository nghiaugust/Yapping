// src/components/admin/AccountDetailModal.tsx
import { Modal, Button, Tag, Avatar } from "antd";
import { User} from "../../../types/user";
import { UserOutlined, CheckCircleOutlined, CloseCircleOutlined } from "@ant-design/icons";
import moment from "moment";

interface AccountDetailModalProps {
  visible: boolean;
  account: User | null;
  onClose: () => void;
}

const AccountDetailModal: React.FC<AccountDetailModalProps> = ({
  visible,
  account,
  onClose,
}) => {
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
      onCancel={onClose}
      width={800}
      centered
      footer={null}
      closable={false}
      styles={{
        body: { padding: 0 }
      }}
      className="account-detail-modal"
    >
      {account ? (
        <div style={{ 
          background: "linear-gradient(135deg, #f8faff 0%, #f1f5ff 100%)",
          borderRadius: "8px"
        }}>
          {/* Header Section */}
          <div style={{
            background: "linear-gradient(135deg, #5680E9, #84CEEB)",
            padding: "24px",
            borderTopLeftRadius: "8px",
            borderTopRightRadius: "8px",
            color: "#ffffff",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between"
          }}>
            <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
              <Avatar 
                size={64} 
                src={account.profilePicture}
                icon={<UserOutlined />}
                style={{
                  border: "3px solid rgba(255, 255, 255, 0.3)",
                  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)"
                }}
              />
              <div>
                <h2 style={{
                  margin: 0,
                  color: "#ffffff",
                  fontSize: "22px",
                  fontWeight: "600",
                  textShadow: "0 2px 4px rgba(0, 0, 0, 0.1)"
                }}>
                  {account.fullName || account.username}
                </h2>
                <p style={{
                  margin: "4px 0 0 0",
                  color: "rgba(255, 255, 255, 0.9)",
                  fontSize: "14px"
                }}>
                  @{account.username}
                </p>
                <div style={{ marginTop: "8px", display: "flex", gap: "8px" }}>
                  <Tag 
                    color={getStatusColor(account.status)}
                    style={{ 
                      background: "rgba(255, 255, 255, 0.2)",
                      color: "#ffffff",
                      border: "1px solid rgba(255, 255, 255, 0.3)",
                      fontWeight: "500"
                    }}
                  >
                    {getStatusLabel(account.status)}
                  </Tag>
                  {account.isVerified && (
                    <Tag 
                      color="green"
                      icon={<CheckCircleOutlined />}
                      style={{ 
                        background: "rgba(255, 255, 255, 0.2)",
                        color: "#ffffff",
                        border: "1px solid rgba(255, 255, 255, 0.3)",
                        fontWeight: "500"
                      }}
                    >
                      Đã xác thực
                    </Tag>
                  )}
                </div>
              </div>
            </div>
            <Button
              type="text"
              onClick={onClose}
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
          <div style={{ padding: "24px" }}>
            <div style={{ 
              display: "grid", 
              gridTemplateColumns: "1fr 1fr", 
              gap: "24px",
              marginBottom: "24px"
            }}>
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
                <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                  <div>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>ID:</span>
                    <p style={{ margin: "2px 0 0 0", color: "#1f2937", fontWeight: "500" }}>{account.id}</p>
                  </div>
                  <div>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Email:</span>
                    <p style={{ margin: "2px 0 0 0", color: "#1f2937", fontWeight: "500" }}>{account.email}</p>
                  </div>
                  <div>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Họ và tên:</span>
                    <p style={{ margin: "2px 0 0 0", color: "#1f2937", fontWeight: "500" }}>{account.fullName || "Chưa cập nhật"}</p>
                  </div>
                  <div>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Xác thực:</span>
                    <p style={{ 
                      margin: "2px 0 0 0", 
                      color: account.isVerified ? "#10b981" : "#ef4444", 
                      fontWeight: "500",
                      display: "flex",
                      alignItems: "center",
                      gap: "4px"
                    }}>
                      {account.isVerified ? <CheckCircleOutlined /> : <CloseCircleOutlined />}
                      {account.isVerified ? "Đã xác thực" : "Chưa xác thực"}
                    </p>
                  </div>
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
                  Vai trò & Thời gian
                </h3>
                <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                  <div>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Vai trò:</span>
                    <div style={{ marginTop: "4px", display: "flex", flexWrap: "wrap", gap: "4px" }}>
                      {account.roles && account.roles.length > 0 ? (
                        account.roles.map((role) => (
                          <Tag 
                            key={role.id} 
                            color={role.name === "ADMIN" ? "#5680E9" : "#10b981"}
                            style={{ 
                              margin: 0,
                              borderRadius: "6px",
                              fontWeight: "500",
                              fontSize: "12px"
                            }}
                          >
                            {role.name}
                          </Tag>
                        ))
                      ) : (
                        <Tag color="gray" style={{ margin: 0, borderRadius: "6px", fontWeight: "500" }}>USER</Tag>
                      )}
                    </div>
                  </div>
                  <div>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Ngày tạo:</span>
                    <p style={{ margin: "2px 0 0 0", color: "#1f2937", fontWeight: "500" }}>
                      {moment(account.createdAt).format("DD/MM/YYYY HH:mm")}
                    </p>
                  </div>
                  <div>
                    <span style={{ color: "#6b7280", fontSize: "13px", fontWeight: "500" }}>Cập nhật cuối:</span>
                    <p style={{ margin: "2px 0 0 0", color: "#1f2937", fontWeight: "500" }}>
                      {moment(account.updatedAt).format("DD/MM/YYYY HH:mm")}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Footer Buttons */}
            <div style={{
              display: "flex",
              justifyContent: "flex-end",
              gap: "12px",
              paddingTop: "16px",
              borderTop: "1px solid rgba(86, 128, 233, 0.1)"
            }}>
              <Button
                type="primary"
                onClick={onClose}
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
                  e.currentTarget.style.transform = "translateY(-1px)";
                  e.currentTarget.style.boxShadow = "0 4px 12px rgba(86, 128, 233, 0.3)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = "translateY(0)";
                  e.currentTarget.style.boxShadow = "0 2px 8px rgba(86, 128, 233, 0.2)";
                }}
              >
                Đóng
              </Button>
            </div>
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
            Không có thông tin tài khoản.
          </p>
        </div>
      )}
    </Modal>
  );
};

export default AccountDetailModal;