// src/pages/admin/AccountManagement.tsx
import { Table, Typography, Button, Popconfirm, message, Tag } from 'antd';
import { useState, useEffect } from 'react';
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import AccountDetailModal from '../../components/admin/AccountDetailModal';
import CreateAccountModal from '../../components/admin/CreateAccountModal';
import { getUsers, deleteUser } from '../../service/admin/userService';
import { User, Role } from '../../types/user';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';

const { Title } = Typography;

interface ApiErrorResponse {
  type?: string;
  title?: string;
  status: number;
  detail?: string;
  errorCode?: string;
}

const AccountManagement: React.FC = () => {
  const [data, setData] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [isDetailModalVisible, setIsDetailModalVisible] = useState(false);
  const [isCreateModalVisible, setIsCreateModalVisible] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState<User | null>(null);
  const navigate = useNavigate();

  // Lấy danh sách tài khoản
  const fetchUsers = async (): Promise<void> => {
    setLoading(true);
    try {
      const users = await getUsers();
      setData(users);
    } catch (error: unknown) {
      const errorResponse: ApiErrorResponse = (error as { response?: { data?: ApiErrorResponse } }).response?.data ?? { status: 0 };
      const errorMessage = errorResponse.detail ?? errorResponse.title ?? 'Lỗi không xác định';
      if (errorResponse.status === 401) {
        message.error('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!');
        void navigate('/login');
      } else {
        message.error(`Lấy danh sách tài khoản thất bại: ${errorMessage}`);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const loadUsers = async () => {
      await fetchUsers();
    };
    void loadUsers();
  }, []);

  const handleDelete = async (id: number): Promise<void> => {
    try {
      await deleteUser(id);
      setData(data.filter((item) => item.id !== id));
      message.success('Xóa tài khoản thành công!');
    } catch (error: unknown) {
      const errorResponse: ApiErrorResponse = (error as { response?: { data?: ApiErrorResponse } }).response?.data ?? { status: 0 };
      const errorMessage = errorResponse.detail ?? errorResponse.title ?? 'Lỗi không xác định';
      if (errorResponse.status === 401) {
        message.error('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!');
        void navigate('/login');
      } else {
        message.error(`Xóa tài khoản thất bại: ${errorMessage}`);
      }
    }
  };

  const handleRowClick = (record: User): void => {
    setSelectedAccount(record);
    setIsDetailModalVisible(true);
  };

  const handleCloseDetailModal = (): void => {
    setIsDetailModalVisible(false);
    setSelectedAccount(null);
  };

  const handleOpenCreateModal = (): void => {
    setIsCreateModalVisible(true);
  };

  const handleCloseCreateModal = (): void => {
    setIsCreateModalVisible(false);
  };

  const columns = [
    { title: 'Tên đăng nhập', dataIndex: 'username', key: 'username', width: 150 },
    { title: 'Email', dataIndex: 'email', key: 'email', width: 200 },
    { title: 'Họ và tên', dataIndex: 'fullName', key: 'fullName', width: 150 },
    {
      title: 'Vai trò',
      dataIndex: 'roles',
      key: 'roles',
      width: 150,
      render: (roles: Role[]) => {
        const maxDisplay = 2;
        const displayedRoles = roles.slice(0, maxDisplay);
        const remainingRoles = roles.length - maxDisplay;

        return (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
            {displayedRoles.map((role) => (
              <Tag
                key={role.id}
                color={role.name === 'ADMIN' ? 'blue' : 'green'}
                style={{ margin: 0 }}
              >
                {role.name}
              </Tag>
            ))}
            {remainingRoles > 0 && (
              <span style={{ fontSize: '12px', color: '#888' }}>
                và {remainingRoles} vai trò khác
              </span>
            )}
          </div>
        );
      },
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => {
        const statusMap: Record<string, { label: string; color: string }> = {
          ACTIVE: { label: 'Hoạt động', color: 'green' },
          PENDING_VERIFICATION: { label: 'Chờ xác nhận', color: 'yellow' },
          DELETED: { label: 'Đã xóa', color: 'red' },
          SUSPENDED: { label: 'Tạm khóa', color: 'orange' },
        };

        const { label, color } = statusMap[status] || {
          label: status,
          color: 'gray',
        };

        return <Tag color={color}>{label}</Tag>;
      },
    },
    {
      title: 'Định danh',
      dataIndex: 'isVerified',
      key: 'isVerified',
      width: 100,
      render: (isVerified: boolean) => (isVerified ? 'Định danh' : 'Không'),
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 150,
      render: (createdAt: string) =>
        createdAt && dayjs(createdAt).isValid()
          ? dayjs(createdAt).format('DD/MM/YYYY HH:mm')
          : 'Không xác định',
    },
    {
      title: 'Cập nhật',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 150,
      render: (updatedAt: string) =>
        updatedAt && dayjs(updatedAt).isValid()
          ? dayjs(updatedAt).format('DD/MM/YYYY HH:mm')
          : 'Không xác định',
    },
    {
      title: 'Hành động',
      key: 'action',
      width: 100,
      render: (_: unknown, record: User) => (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <Button
            type="primary"
            icon={<EditOutlined />}
            size="small"
            onClick={() => {
              message.info(`Chỉnh sửa tài khoản: ${record.username}`);
            }}
          >
            Sửa
          </Button>
          <Popconfirm
            title="Bạn có chắc chắn muốn xóa tài khoản này?"
            onConfirm={() => {
              void handleDelete(record.id);
            }}
            okText="Có"
            cancelText="Không"
          >
            <Button
              type="primary"
              danger
              icon={<DeleteOutlined />}
              size="small"
            >
              Xóa
            </Button>
          </Popconfirm>
        </div>
      ),
    },
  ];

  return (
    <>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '16px',
        }}
      >
        <Title level={2} style={{ margin: 0 }}>
          Quản Lý Tài Khoản
        </Title>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={handleOpenCreateModal}
        >
          Thêm Tài Khoản
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        pagination={{ pageSize: 5 }}
        loading={loading}
        onRow={(record) => ({
          onClick: () => handleRowClick(record),
          style: { cursor: 'pointer' },
        })}
        rowKey="id"
        scroll={{ x: 'max-content' }}
      />
      <AccountDetailModal
        visible={isDetailModalVisible}
        account={selectedAccount}
        onClose={handleCloseDetailModal}
      />
      <CreateAccountModal
        visible={isCreateModalVisible}
        onClose={handleCloseCreateModal}
        onSuccess={() => {
          void fetchUsers();
        }}
      />
    </>
  );
};

export default AccountManagement;