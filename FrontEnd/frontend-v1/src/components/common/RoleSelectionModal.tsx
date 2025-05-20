// src/components/common/RoleSelectionModal.tsx
import { Modal, Button, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

interface RoleSelectionModalProps {
  visible: boolean;
  onClose: () => void;
  roles: string[];
  userId: number;
}

const RoleSelectionModal: React.FC<RoleSelectionModalProps> = ({
  visible,
  onClose,
  roles,
  //userId,
}) => {
  const navigate = useNavigate();

  const handleRoleSelection = (role: string) => {
    localStorage.setItem('user_role', role);
    if (role === 'USER') {
      void navigate('/');
    } else if (role === 'ADMIN') {
        // Chuyển hướng đến trang quản trị tài khoản
      void navigate('/admin/accounts');
    }
    onClose();
  };

  return (
    <Modal
      open={visible}
      onCancel={onClose}
      footer={null}
      centered
      width={400}
    >
      <div style={{ textAlign: 'center', padding: '20px' }}>
        <Title level={4}>Chọn vai trò</Title>
        <Text>Bạn muốn đăng nhập với vai trò nào?</Text>
        <div style={{ marginTop: '20px' }}>
          {roles.includes('USER') && (
            <Button
              type="primary"
              block
              style={{ marginBottom: '10px' }}
              onClick={() => handleRoleSelection('USER')}
            >
              Trang Người Dùng
            </Button>
          )}
          {roles.includes('ADMIN') && (
            <Button
              type="default"
              block
              onClick={() => handleRoleSelection('ADMIN')}
            >
              Trang Quản Trị
            </Button>
          )}
        </div>
      </div>
    </Modal>
  );
};

export default RoleSelectionModal;