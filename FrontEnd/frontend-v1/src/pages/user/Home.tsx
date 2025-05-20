// src/pages/user/Home.tsx
import { Typography, List, Card } from "antd";

const { Title, Text } = Typography;

const posts = [
  { id: 1, author: "User1", content: "Đây là bài đăng đầu tiên!", timestamp: "2025-05-05 10:00" },
  { id: 2, author: "User2", content: "Hôm nay thật tuyệt vời!", timestamp: "2025-05-05 09:30" },
  { id: 3, author: "User3", content: "Đang học React với Vite!", timestamp: "2025-05-05 08:45" },
];

const Home: React.FC = () => {
  return (
    <>
      <Title level={2} style={{ color: "#167d7f" }}>
        Dòng Thời Gian
      </Title>
      <List
        dataSource={posts}
        renderItem={(post) => (
          <List.Item>
            <Card
              style={{
                width: "100%",
                borderRadius: "8px",
                background: "linear-gradient(to bottom, #ffffff, #DDFFE7)", // Gradient nhẹ
                border: "1px solid #98D7C2",
                boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
                transition: "all 0.3s",
              }}
              hoverable
            >
              <Text strong style={{ color: "#167d7f" }}>
                {post.author}
              </Text>
              <Text type="secondary" style={{ marginLeft: "8px" }}>
                {post.timestamp}
              </Text>
              <p style={{ color: "#333" }}>{post.content}</p>
            </Card>
          </List.Item>
        )}
      />
    </>
  );
};

export default Home;