// src/pages/admin/AccountManagement.tsx
import { Table, Button, Popconfirm, message, Tag } from 'antd';
import { useState, useEffect, useCallback } from 'react';
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import AccountDetailModal from '../../components/admin/account/AccountDetailModal';
import CreateAccountModal from '../../components/admin/account/CreateAccountModal';
import EditAccountModal from '../../components/admin/account/EditAccountModal';
import * as adminUserService from '../../service/admin/userService';
import { User, Role } from '../../types/user';
import { useNavigate } from 'react-router-dom';

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
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState<User | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const navigate = useNavigate();

  // Lấy danh sách tài khoản
  const fetchUsers = useCallback(async (): Promise<void> => {
    setLoading(true);
    try {
      const users = await adminUserService.getUsers();
      console.log('Fetched users:', users);
      
      // Đảm bảo users là một array và có dữ liệu hợp lệ
      if (Array.isArray(users)) {
        const validUsers = users.filter(user => 
          user && 
          typeof user === 'object' && 
          typeof user.id === 'number' && 
          typeof user.username === 'string' && 
          typeof user.email === 'string'
        );
        console.log('Valid users:', validUsers);
        setData(validUsers);
      } else {
        console.error('Users is not an array:', users);
        setData([]);
      }
    } catch (error: unknown) {
      console.error('Error fetching users:', error);
      const errorResponse: ApiErrorResponse = (error as { response?: { data?: ApiErrorResponse } }).response?.data ?? { status: 0 };
      const errorMessage = errorResponse.detail ?? errorResponse.title ?? 'Lỗi không xác định';
      if (errorResponse.status === 401) {
        message.error('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!');
        void navigate('/login');
      } else {
        message.error(`Lấy danh sách tài khoản thất bại: ${errorMessage}`);
      }
      setData([]);
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    void fetchUsers();
  }, [fetchUsers]);

  const handleDelete = async (id: number): Promise<void> => {
    if (!id || typeof id !== 'number') {
      message.error('ID tài khoản không hợp lệ');
      return;
    }
    
    try {
      await adminUserService.deleteUser(id);
      setData(prevData => prevData.filter((item) => item.id !== id));
      message.success('Xóa tài khoản thành công!');
    } catch (error: unknown) {
      console.error('Error deleting user:', error);
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

  const handleOpenEditModal = (account: User): void => {
    setSelectedAccount(account);
    setIsEditModalVisible(true);
  };

  const handleCloseEditModal = (): void => {
    setIsEditModalVisible(false);
    setSelectedAccount(null);
  };

  const columns = [
    { 
      title: 'STT', 
      key: 'stt',
      width: 60,
      align: 'center' as const,
      render: (_: unknown, __: unknown, index: number) => {
        // Tính toán STT dựa trên trang hiện tại
        return (currentPage - 1) * 5 + index + 1;
      }
    },
    { 
      title: 'Tên đăng nhập', 
      dataIndex: 'username', 
      key: 'username', 
      width: 140,
      render: (text: string) => String(text || '-')
    },
    { 
      title: 'Email', 
      dataIndex: 'email', 
      key: 'email', 
      width: 180,
      render: (text: string) => String(text || '-')
    },
    { 
      title: 'Họ và tên', 
      dataIndex: 'fullName', 
      key: 'fullName', 
      width: 140,
      render: (text: string) => String(text || '-')
    },
    {
      title: 'Vai trò',
      dataIndex: 'roles',
      key: 'roles',
      width: 140,
      render: (roles: Role[]) => {
        try {
          if (!roles || !Array.isArray(roles) || roles.length === 0) {
            return <Tag color="gray" style={{ margin: '2px', fontSize: '11px' }}>USER</Tag>;
          }
          
          // Hiển thị tất cả vai trò trên cùng một hàng
          return (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '2px' }}>
              {roles.map((role, index) => {
                const roleName = String(role?.name || 'USER');
                return (
                  <Tag
                    key={index}
                    color={roleName === 'ADMIN' ? 'blue' : 'green'}
                    style={{ margin: '1px', fontSize: '11px' }}
                  >
                    {roleName}
                  </Tag>
                );
              })}
            </div>
          );
        } catch (error) {
          console.error('Error rendering role:', error);
          return <Tag color="gray" style={{ margin: '2px', fontSize: '11px' }}>USER</Tag>;
        }
      },
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        try {
          const statusMap: Record<string, { label: string; color: string }> = {
            ACTIVE: { label: 'Hoạt động', color: 'green' },
            PENDING_VERIFICATION: { label: 'Chờ xác nhận', color: 'yellow' },
            DELETED: { label: 'Đã xóa', color: 'red' },
            SUSPENDED: { label: 'Tạm khóa', color: 'orange' },
          };

          const statusStr = String(status || '');
          const { label, color } = statusMap[statusStr] || {
            label: statusStr || 'Không xác định',
            color: 'gray',
          };

          return <Tag color={color} style={{ fontSize: '11px' }}>{label}</Tag>;
        } catch (error) {
          console.error('Error rendering status:', error);
          return <Tag color="gray" style={{ fontSize: '11px' }}>Không xác định</Tag>;
        }
      },
    },
    {
      title: 'Định danh',
      dataIndex: 'isVerified',
      key: 'isVerified',
      width: 80,
      render: (isVerified: boolean) => (isVerified ? 'Có' : 'Không'),
    },
    {
      title: 'Hành động',
      key: 'action',
      width: 120,
      render: (_: unknown, record: User) => (
        <div style={{ display: 'flex', gap: '4px' }}>
          <Button
            type="primary"
            icon={<EditOutlined />}
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleOpenEditModal(record);
            }}
          />
          <Popconfirm
            title="Bạn có chắc chắn muốn xóa tài khoản này?"
            onConfirm={(e) => {
              e?.stopPropagation();
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
              onClick={(e) => e.stopPropagation()}
            />
          </Popconfirm>
        </div>
      ),
    },
  ];

  return (
    <div style={{ 
      padding: "24px",
      height: "100vh",
      background: "#f8fafc",
      display: "flex",
      flexDirection: "column"
    }}>
      {/* Header Section */}
      <div style={{
        background: "linear-gradient(135deg, #5680E9, #84CEEB)",
        borderRadius: "12px",
        padding: "24px",
        marginBottom: "24px",
        color: "#ffffff",
        boxShadow: "0 4px 20px rgba(86, 128, 233, 0.2)",
      }}>
        <div style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}>
          <div>
            <h2 style={{
              margin: 0,
              color: "#ffffff",
              fontSize: "24px",
              fontWeight: "600",
              textShadow: "0 2px 4px rgba(0, 0, 0, 0.1)"
            }}>
              Quản Lý Tài Khoản
            </h2>
            <p style={{
              margin: "8px 0 0 0",
              color: "rgba(255, 255, 255, 0.9)",
              fontSize: "14px"
            }}>
              Quản lý thông tin người dùng và phân quyền hệ thống
            </p>
          </div>
          <Button
            type="primary"
            size="large"
            icon={<PlusOutlined />}
            onClick={handleOpenCreateModal}
            style={{
              background: "rgba(255, 255, 255, 0.2)",
              borderColor: "rgba(255, 255, 255, 0.3)",
              color: "#ffffff",
              fontWeight: "500",
              height: "48px",
              padding: "0 24px",
              borderRadius: "8px",
              boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
              backdropFilter: "blur(10px)",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = "rgba(255, 255, 255, 0.3)";
              e.currentTarget.style.transform = "translateY(-1px)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = "rgba(255, 255, 255, 0.2)";
              e.currentTarget.style.transform = "translateY(0)";
            }}
          >
            Thêm Tài Khoản
          </Button>
        </div>
      </div>

      {/* Table Section */}
      <div style={{
        background: "#ffffff",
        borderRadius: "12px",
        padding: "24px",
        boxShadow: "0 2px 12px rgba(86, 128, 233, 0.08)",
        border: "1px solid rgba(86, 128, 233, 0.1)",
        flex: 1,
        display: "flex",
        flexDirection: "column",
        overflow: "hidden"
      }}>
        <Table
          columns={columns}
          dataSource={data}
          pagination={{ 
            pageSize: 5, 
            showSizeChanger: false,
            showTotal: (total, range) => `${range[0]}-${range[1]} của ${total} tài khoản`,
            style: { marginTop: "16px" },
            current: currentPage,
            onChange: (page) => setCurrentPage(page)
          }}
          loading={loading}
          onRow={(record) => ({
            onClick: () => {
              if (record?.id) {
                handleRowClick(record);
              }
            },
            style: { 
              cursor: 'pointer',
              transition: "all 0.2s ease"
            },
          })}
          rowKey={(record) => {
            try {
              return record?.id ? String(record.id) : Math.random().toString();
            } catch (error) {
              console.error('Error generating rowKey:', error);
              return Math.random().toString();
            }
          }}
          scroll={{ x: 'max-content', y: 'calc(100vh - 320px)' }}
          size="large"
          locale={{
            emptyText: 'Không có dữ liệu',
            filterTitle: 'Bộ lọc',
            filterConfirm: 'OK',
            filterReset: 'Đặt lại',
            selectAll: 'Chọn tất cả',
            selectInvert: 'Chọn ngược lại',
          }}
          className="admin-table"
        />
      </div>

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
      <EditAccountModal
        visible={isEditModalVisible}
        account={selectedAccount}
        onClose={handleCloseEditModal}
        onSuccess={() => {
          void fetchUsers();
        }}
      />
    </div>
  );
};

export default AccountManagement;