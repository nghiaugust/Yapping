// src/pages/user/UserProfile.tsx
import { Typography, Card, List } from "antd";

const { Title, Text } = Typography;

const user = {
  username: "User1",
  email: "user1@example.com",
  bio: "Tôi là một lập trình viên đam mê công nghệ!",
  activities: [
    "Đã đăng một bài viết mới - 2025-05-05 10:00",
    "Đã theo dõi User2 - 2025-05-04 15:30",
    "Đã đánh dấu một bài viết - 2025-05-03 09:00",
  ],
};

const UserProfile: React.FC = () => {
  return (
    <>
      <Title level={2}>Hồ Sơ Người Dùng</Title>
      <Card title="Thông Tin Cá Nhân" style={{ borderRadius: "8px" }}>
        <Text strong>Tên người dùng: </Text>
        <Text>{user.username}</Text>
        <br />
        <Text strong>Email: </Text>
        <Text>{user.email}</Text>
        <br />
        <Text strong>Tiểu sử: </Text>
        <Text>{user.bio}</Text>
      </Card>
      <Title level={3} style={{ marginTop: "16px" }}>
        Hoạt Động Gần Đây
      </Title>
      <List
        dataSource={user.activities}
        renderItem={(activity) => <List.Item>{activity}</List.Item>}
      />
    </>
  );
};

export default UserProfile;