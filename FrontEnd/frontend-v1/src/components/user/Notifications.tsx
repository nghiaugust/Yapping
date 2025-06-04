// src/components/user/Notifications.tsx
import { BellOutlined } from "@ant-design/icons";
import { Popover, List, Typography } from "antd";
import { useState } from "react";

const { Text } = Typography;

const notifications = [
  { id: 1, message: "User2 đã thích bài đăng của bạn", timestamp: "2025-05-05 10:00" },
  { id: 2, message: "User3 đã theo dõi bạn", timestamp: "2025-05-05 09:30" },
];

const Notifications: React.FC = () => {
  const [visible, setVisible] = useState(false);

  const content = (
    <div style={{ width: "300px", maxHeight: "300px", overflowY: "auto" }}>
      <List
        dataSource={notifications}
        renderItem={(item) => (
          <List.Item>
            <Text>{item.message}</Text>
            <Text type="secondary" style={{ marginLeft: "8px" }}>
              {item.timestamp}
            </Text>
          </List.Item>
        )}
      />
    </div>
  );

  return (
    <Popover
      content={content}
      title="Thông Báo"
      trigger="click"
      open={visible}
      onOpenChange={(newVisible) => setVisible(newVisible)}
      placement="bottomRight"
    >
      <BellOutlined style={{ fontSize: "24px", cursor: "pointer", color: "#ffffff" }} />
    </Popover>
  );
};

export default Notifications;