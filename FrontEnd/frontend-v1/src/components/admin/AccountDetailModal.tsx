// src/components/admin/AccountDetailModal.tsx
import { Modal, Descriptions, Button, Tag, Space } from "antd";
import { User} from "../../types/user";
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
  return (
    <Modal
      title="Chi Tiết Tài Khoản"
      open={visible}
      onCancel={onClose}
      footer={[
        <Button key="close" onClick={onClose}>
          Đóng
        </Button>,
      ]}
    >
      {account ? (
        <Descriptions bordered column={1}>
          <Descriptions.Item label="ID">{account.id}</Descriptions.Item>
          <Descriptions.Item label="Tên đăng nhập">{account.username}</Descriptions.Item>
          <Descriptions.Item label="Email">{account.email}</Descriptions.Item>
          <Descriptions.Item label="Họ và tên">{account.fullName}</Descriptions.Item>
          <Descriptions.Item label="Tiểu sử">{account.bio ?? "Chưa có"}</Descriptions.Item>
          <Descriptions.Item label="Ảnh đại diện">
            {account.profilePicture ? (
              <img src={account.profilePicture} alt="Profile" style={{ width: 100 }} />
            ) : (
              "Chưa có"
            )}
          </Descriptions.Item>
          <Descriptions.Item label="Xác thực">
            {account.isVerified ? "Đã xác thực" : "Chưa xác thực"}
          </Descriptions.Item>
          <Descriptions.Item label="Trạng thái">
            <Tag
              color={
                account.status === "ACTIVE"
                  ? "green"
                  : account.status === "SUSPENDED"
                  ? "orange"
                  : account.status === "DELETED"
                  ? "red"
                  : "yellow"
              }
            >
              {account.status}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Vai trò">
            <Space>
              {account.roles.map((role) => (
                <Tag key={role.id} color={role.name === "ADMIN" ? "blue" : "green"}>
                  {role.name}
                </Tag>
              ))}
            </Space>
          </Descriptions.Item>
          <Descriptions.Item label="Ngày tạo">
            {moment(account.createdAt).format("DD/MM/YYYY HH:mm")}
          </Descriptions.Item>
          <Descriptions.Item label="Cập nhật cuối">
            {moment(account.updatedAt).format("DD/MM/YYYY HH:mm")}
          </Descriptions.Item>
        </Descriptions>
      ) : (
        <p>Không có thông tin tài khoản.</p>
      )}
    </Modal>
  );
};

export default AccountDetailModal;