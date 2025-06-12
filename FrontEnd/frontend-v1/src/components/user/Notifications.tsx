// src/components/user/Notifications.tsx
import { BellOutlined } from "@ant-design/icons";
import { Popover, List, Typography, Badge, Spin, Button, message } from "antd";
import { useState, useEffect } from "react";
import { getNotifications, countUnreadNotifications, markAllNotificationsAsRead } from "../../service/user/notificationService";
import { NotificationDTO } from "../../types/notification";
import dayjs from "dayjs";

const { Text } = Typography;

const Notifications: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [notifications, setNotifications] = useState<NotificationDTO[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [markingAllAsRead, setMarkingAllAsRead] = useState(false);

  // Fetch unread count
  const fetchUnreadCount = async () => {
    try {
      const response = await countUnreadNotifications();
      if (response.success) {
        setUnreadCount(response.data);
      }
    } catch (error) {
      console.error('Error fetching unread count:', error);
    }
  };

  // Fetch notifications
  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const response = await getNotifications(0, 10);
      if (response.success) {
        setNotifications(response.data.content);
      }
    } catch (error) {
      console.error('Error fetching notifications:', error);
      message.error('Không thể tải thông báo');
    } finally {
      setLoading(false);
    }
  };

  // Mark all as read
  const handleMarkAllAsRead = async () => {
    if (unreadCount === 0) return;
    
    setMarkingAllAsRead(true);
    try {
      const response = await markAllNotificationsAsRead();
      if (response.success) {
        message.success(`Đã đánh dấu ${response.data} thông báo đã đọc`);
        setUnreadCount(0);
        // Refresh notifications to update read status
        await fetchNotifications();
      }
    } catch (error) {
      console.error('Error marking all as read:', error);
      message.error('Không thể đánh dấu tất cả đã đọc');
    } finally {
      setMarkingAllAsRead(false);    }
  };

  // Load data when popover opens
  const handleVisibleChange = (newVisible: boolean) => {
    setVisible(newVisible);
    if (newVisible) {
      void fetchNotifications();
    }
  };

  // Load unread count on component mount
  useEffect(() => {
    void fetchUnreadCount();
    // Set up interval to refresh unread count every 30 seconds
    const interval = setInterval(() => void fetchUnreadCount(), 30000);
    return () => clearInterval(interval);
  }, []);

  // Format notification time
  const formatTime = (createdAt: string) => {
    const now = dayjs();
    const notificationTime = dayjs(createdAt);
    const diffInMinutes = now.diff(notificationTime, 'minute');
    const diffInHours = now.diff(notificationTime, 'hour');
    const diffInDays = now.diff(notificationTime, 'day');

    if (diffInMinutes < 1) {
      return 'Vừa xong';
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes} phút trước`;
    } else if (diffInHours < 24) {
      return `${diffInHours} giờ trước`;
    } else if (diffInDays < 7) {
      return `${diffInDays} ngày trước`;
    } else {
      return notificationTime.format('DD/MM/YYYY');
    }
  };

  const content = (
    <div style={{ width: "350px", maxHeight: "400px" }}>
      <div style={{ 
        padding: "8px 16px", 
        borderBottom: "1px solid #f0f0f0", 
        display: "flex", 
        justifyContent: "space-between", 
        alignItems: "center" 
      }}>
        <Text strong>Thông Báo</Text>        {unreadCount > 0 && (
          <Button 
            type="link" 
            size="small" 
            onClick={() => void handleMarkAllAsRead()}
            loading={markingAllAsRead}
          >
            Đánh dấu tất cả đã đọc
          </Button>
        )}
      </div>
      
      <div style={{ maxHeight: "300px", overflowY: "auto" }}>
        {loading ? (
          <div style={{ textAlign: "center", padding: "20px" }}>
            <Spin />
          </div>
        ) : notifications.length === 0 ? (
          <div style={{ textAlign: "center", padding: "20px", color: "#999" }}>
            <Text type="secondary">Không có thông báo nào</Text>
          </div>
        ) : (
          <List
            dataSource={notifications}
            renderItem={(item) => (              <List.Item
                style={{
                  padding: "12px 16px",
                  backgroundColor: item.isRead ? "#fff" : "#f6ffed",
                  borderLeft: item.isRead ? "none" : "3px solid #52c41a",
                  cursor: "pointer"
                }}
                onClick={() => {
                  // Handle navigation based on redirectUrl if needed
                  if (item.redirectUrl) {
                    console.log('Navigate to:', item.redirectUrl);
                    // You can implement navigation logic here
                  }
                }}
              >
                <div style={{ width: "100%" }}>
                  <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "4px" }}>                    {item.actorProfilePicture && (
                      <img 
                        src={item.actorProfilePicture} 
                        alt={item.actorFullName ?? 'User'} 
                        style={{ width: "24px", height: "24px", borderRadius: "50%" }}
                      />
                    )}
                    <Text style={{ fontSize: "13px", lineHeight: "18px" }}>
                      {item.message}
                    </Text>
                  </div>
                  <Text type="secondary" style={{ fontSize: "11px" }}>
                    {formatTime(item.createdAt)}
                  </Text>
                </div>
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );

  return (
    <Popover
      content={content}
      title={null}
      trigger="click"
      open={visible}
      onOpenChange={handleVisibleChange}
      placement="bottomRight"
      overlayStyle={{ zIndex: 1050 }}
    >
      <Badge count={unreadCount} size="small" offset={[-5, 5]}>
        <BellOutlined 
          style={{ 
            fontSize: "24px", 
            cursor: "pointer", 
            color: "#ffffff",
            transition: "color 0.3s"
          }} 
        />
      </Badge>
    </Popover>
  );
};

export default Notifications;