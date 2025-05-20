// src/components/user/Followers.tsx
import { TeamOutlined } from "@ant-design/icons";
import { Popover, List, Typography } from "antd";
import { useState } from "react";

const { Text } = Typography;

const followers = [
  { id: 1, username: "User2", timestamp: "2025-05-05 09:30" },
  { id: 2, username: "User3", timestamp: "2025-05-04 15:00" },
];

const Followers: React.FC = () => {
  const [visible, setVisible] = useState(false);

  const content = (
    <div style={{ width: "300px", maxHeight: "300px", overflowY: "auto" }}>
      <List
        dataSource={followers}
        renderItem={(item) => (
          <List.Item>
            <Text>{item.username}</Text>
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
      title="Danh Sách Người Theo Dõi"
      trigger="click"
      open={visible}
      onOpenChange={(newVisible) => setVisible(newVisible)}
      placement="bottomRight" 
    >
      <TeamOutlined style={{ fontSize: "30px", cursor: "pointer", color: "#000000" }} />
    </Popover>
  );
};

export default Followers;