// src/pages/user/Bookmarks.tsx
import { Typography, List, Card } from "antd";

const { Title, Text } = Typography;

const bookmarks = [
  { id: 1, author: "User1", content: "Bài đăng đã đánh dấu 1", timestamp: "2025-05-05 10:00" },
  { id: 2, author: "User2", content: "Bài đăng đã đánh dấu 2", timestamp: "2025-05-04 15:30" },
];

const Bookmarks: React.FC = () => {
  return (
    <>
      <Title level={2}>Bài Đăng Đã Đánh Dấu</Title>
      <List
        dataSource={bookmarks}
        renderItem={(bookmark) => (
          <List.Item>
            <Card style={{ width: "100%", borderRadius: "8px" }}>
              <Text strong>{bookmark.author}</Text>
              <Text type="secondary" style={{ marginLeft: "8px" }}>
                {bookmark.timestamp}
              </Text>
              <p>{bookmark.content}</p>
            </Card>
          </List.Item>
        )}
      />
    </>
  );
};

export default Bookmarks;