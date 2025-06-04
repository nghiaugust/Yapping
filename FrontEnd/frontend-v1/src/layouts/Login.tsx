// src/layouts/Login.tsx
import { Button, Form, Input, Card, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import api from '../service/admin/api';
import RoleSelectionModal from '../components/common/RoleSelectionModal';

const { Title } = Typography;

interface LoginResponse {
  access_token: string;
  refresh_token: string;
  user_id: number;
  roles: string[];
}

interface ApiErrorResponse {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  errorCode?: string;
  stackTrace?: string[];
}

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [modalVisible, setModalVisible] = useState(false);
  const [loginData, setLoginData] = useState<LoginResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const onFinish = async (values: { username: string; password: string }) => {
    setIsLoading(true);
    try {
      const response = await api.post<LoginResponse>('/auth/login', {
        username: values.username,
        password: values.password,
      });

      const { access_token, refresh_token, user_id, roles } = response.data;

      // Lưu tokens và thông tin user
      localStorage.setItem('access_token', access_token);
      localStorage.setItem('refresh_token', refresh_token);
      localStorage.setItem('user_id', user_id.toString());

      
      if (roles.includes('ADMIN') && roles.includes('USER')) {
        // Hiển thị modal chọn vai trò
        setLoginData({ access_token, refresh_token, user_id, roles });
        setModalVisible(true);
      } else if (roles.includes('ADMIN')) {
        // Tự động lưu role ADMIN vào localStorage
        localStorage.setItem('user_role', 'ADMIN');
        void navigate('/admin/accounts');
      } else if (roles.includes('USER')) {
        // Tự động lưu role USER vào localStorage
        localStorage.setItem('user_role', 'USER');
        void navigate('/');
      }
    } catch (error: unknown) {
      let errorMessage = 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!';

      // Xử lý lỗi từ API
      if (error instanceof Error && 'response' in error && error.response) {
        const errorResponse: ApiErrorResponse = error.response.data;
        
        if (errorResponse.status === 401 && errorResponse.detail === 'Invalid credentials') {
          errorMessage = 'Tên đăng nhập hoặc mật khẩu không đúng!';
        } else if (errorResponse.status === 500) {
          errorMessage = 'Lỗi máy chủ. Vui lòng thử lại sau!';
        } else if (errorResponse.title) {
          errorMessage = errorResponse.title;
        }
      } else if (error.request|| error.code === 'ERR_NETWORK') {
        // Không nhận được phản hồi từ server
        errorMessage = 'Server không phản hồi. Vui lòng kiểm tra kết nối mạng và thử lại!';
      } else {
        // Lỗi khác (ví dụ: cấu hình Axios sai)
        errorMessage = 'Đã xảy ra lỗi. Vui lòng thử lại!';
      }

      console.log('Showing message:', errorMessage); // Log trước khi hiển thị
      message.error(errorMessage, 5);
      console.error('Login error:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleModalClose = () => {
    setModalVisible(false);
    setLoginData(null);
  };

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        background: '#ffffff',
      }}
    >
      <Card
        style={{
          width: 400,
          borderRadius: '12px',
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
        }}
      >
        <Title level={2} style={{ textAlign: 'center' }}>
          Đăng Nhập
        </Title>
        <Form name="login" layout="vertical" onFinish={onFinish} autoComplete="off">
          <Form.Item
            label="Tên đăng nhập"
            name="username"
            rules={[{ required: true, message: 'Vui lòng nhập tên đăng nhập!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Mật khẩu"
            name="password"
            rules={[{ required: true, message: 'Vui lòng nhập mật khẩu!' }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={isLoading}>
              Đăng Nhập
            </Button>
          </Form.Item>
        </Form>
      </Card>
      {loginData && (
        <RoleSelectionModal
          visible={modalVisible}
          onClose={handleModalClose}
          roles={loginData.roles}
          userId={loginData.user_id}
        />
      )}
    </div>
  );
};

export default Login;