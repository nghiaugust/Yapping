// src/components/user/UserProfilePopover.tsx
import React from 'react';
import { Avatar, Typography, Space, Divider, Button, Tooltip } from 'antd';
import { UserOutlined, CheckCircleFilled, LogoutOutlined, SettingOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { UserProfile } from '../../service/user/userService';
import { getProfilePictureUrl } from '../../utils/constants';

const { Text, Paragraph } = Typography;

interface UserProfilePopoverProps {
  user: UserProfile | null;
  loading: boolean;
  onLogout: () => void;
}

const UserProfilePopover: React.FC<UserProfilePopoverProps> = ({ user, loading, onLogout }) => {
  const navigate = useNavigate();

  const content = (
    <div style={{ width: 280 }}>
      {user ? (
        <>
          <div style={{ textAlign: 'center', padding: '8px 0' }}>            <Avatar 
              src={getProfilePictureUrl(user.profilePicture)}
              icon={!user.profilePicture && <UserOutlined />}
              size={64}
              style={{ 
                border: '2px solid #5AB9EA',
                marginBottom: 8,
                backgroundColor: '#C1C8E4',
              }}
            />
            <div>
              <Space align="center">
                <Text strong style={{ fontSize: '16px' }}>{user.fullName}</Text>
                {user.isVerified && (
                  <Tooltip title="Tài khoản đã xác thực">
                    <CheckCircleFilled style={{ color: '#1890ff' }} />
                  </Tooltip>
                )}
              </Space>
              <br />
              <Text type="secondary" style={{ fontSize: '14px' }}>@{user.username}</Text>
            </div>
          </div>

          <Divider style={{ margin: '12px 0' }} />
          
          <div style={{ padding: '0 8px' }}>
            <Text type="secondary" style={{ fontSize: '12px' }}>Email:</Text>
            <Paragraph style={{ marginTop: 4 }}>{user.email}</Paragraph>
            
            {user.bio && (
              <>
                <Text type="secondary" style={{ fontSize: '12px' }}>Tiểu sử:</Text>
                <Paragraph 
                  ellipsis={{ rows: 2, expandable: true, symbol: 'Xem thêm' }}
                  style={{ marginTop: 4 }}
                >
                  {user.bio}
                </Paragraph>
              </>
            )}
            
            <Text type="secondary" style={{ fontSize: '12px' }}>Vai trò:</Text>
            <Paragraph style={{ marginTop: 4 }}>
              {user.roles.map(role => role.name).join(', ')}
            </Paragraph>
          </div>
          
          <Divider style={{ margin: '12px 0' }} />
          
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0 8px' }}>
            <Button 
              icon={<SettingOutlined />} 
              size="small"
              onClick={() => void navigate('/profile')}
            >
              Hồ sơ của tôi
            </Button>
            <Button 
              icon={<LogoutOutlined />} 
              danger 
              size="small"
              onClick={onLogout}
            >
              Đăng xuất
            </Button>
          </div>
        </>
      ) : loading ? (
        <div style={{ textAlign: 'center', padding: '20px 0' }}>
          <Text>Đang tải...</Text>
        </div>
      ) : (
        <div style={{ textAlign: 'center', padding: '20px 0' }}>
          <Text type="danger">Lỗi tải thông tin người dùng</Text>
        </div>
      )}
    </div>
  );

  return content;
};

export default UserProfilePopover;
