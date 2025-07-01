// src/components/user/Followers.tsx
import { TeamOutlined } from "@ant-design/icons";
import { Popover, List, Typography, Spin, Avatar } from "antd";
import { useState, useEffect } from "react";
import { getFollowing } from "../../service/user/followService";
import { getUserById } from "../../service/user/userService";
import { Follow, FollowUser } from "../../types/follow";

const { Text } = Typography;

interface FollowersProps {
  userId: number;
}

const Followers: React.FC<FollowersProps> = ({ userId }) => {
  const [visible, setVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [followers, setFollowers] = useState<FollowUser[]>([]);

  // Test function to debug getUserById
  const testGetUserById = async (testUserId: number) => {
    try {
      console.log(`Testing getUserById for user ${testUserId}...`);
      const response = await getUserById(testUserId);
      console.log(`Test getUserById response:`, response);
      return response;
    } catch (error) {
      console.error(`Test getUserById error:`, error);
      throw error;
    }
  };
  // Fetch following users when popover opens
  useEffect(() => {
    const fetchFollowing = async () => {
      if (!visible) return;
      
      setLoading(true);
      try {
        console.log("=== STARTING FETCH FOLLOWING ===");
        console.log("Fetching following for userId:", userId);
        
        // First test getUserById directly
        await testGetUserById(9); // Test with user ID 9 since that's what we expect
        
        const response = await getFollowing(userId);
        console.log("Following response:", response);
        console.log("Following response structure:", JSON.stringify(response, null, 2));
        
        if (response.success && response.data) {
          console.log("Following data:", response.data);
          console.log("Following data length:", response.data.length);
          
          // Get detailed user info for each followed user
          const followedUsers = await Promise.all(
            response.data.map(async (follow: Follow) => {
              try {
                console.log("Processing follow:", follow);
                console.log("Fetching user details for followedId:", follow.followedId);
                const userResponse = await getUserById(follow.followedId);
                console.log("User response for", follow.followedId, ":", userResponse);
                console.log("User response structure:", JSON.stringify(userResponse, null, 2));
                
                if (userResponse.success && userResponse.data) {
                  const userData = {
                    id: userResponse.data.id,
                    username: userResponse.data.username,
                    fullName: userResponse.data.fullName,
                    profilePicture: userResponse.data.profilePicture,
                    timestamp: userResponse.data.createdAt
                  };
                  console.log("Processed user data:", userData);
                  return userData;
                }
                console.log("User response failed for", follow.followedId);
                return null;
              } catch (error) {
                console.error(`Error fetching user ${follow.followedId}:`, error);
                return null;
              }
            })
          );
          
          console.log("Raw followedUsers array:", followedUsers);
          const filteredUsers = followedUsers.filter(Boolean) as FollowUser[];
          console.log("Final filtered users:", filteredUsers);
          console.log("Setting followers state with:", filteredUsers);
          setFollowers(filteredUsers);
        } else {
          console.log("Following response failed:", response);
        }
      } catch (error) {
        console.error("Error fetching following:", error);
      } finally {
        setLoading(false);
      }
    };

    void fetchFollowing();
  }, [visible, userId]);

  const content = loading ? (
    <div style={{ width: "300px", padding: "20px", textAlign: "center" }}>
      <Spin size="large" tip="Đang tải danh sách..." />
    </div>
  ) : (
    <div style={{ width: "300px", maxHeight: "300px", overflowY: "auto" }}>
      {followers.length === 0 ? (
        <div style={{ padding: "20px", textAlign: "center", color: "#999" }}>
          Chưa theo dõi ai
        </div>
      ) : (
        <List
          dataSource={followers}
          renderItem={(item) => (
            <List.Item>
              <div style={{ display: "flex", alignItems: "center", gap: "8px", width: "100%" }}>
                <Avatar
                  src={item.profilePicture ? `http://localhost:8080/yapping${item.profilePicture}` : undefined}
                  size={32}
                >
                  {!item.profilePicture && item.fullName.charAt(0)}
                </Avatar>
                <div style={{ flex: 1 }}>
                  <div>
                    <Text strong>{item.fullName}</Text>
                  </div>
                  <div>
                    <Text type="secondary" style={{ fontSize: "12px" }}>
                      @{item.username}
                    </Text>
                  </div>
                </div>
              </div>
            </List.Item>
          )}
        />
      )}
    </div>
  );

  return (
    <Popover
      content={content}
      title="Danh Sách Đang Theo Dõi"
      trigger="click"
      open={visible}
      onOpenChange={(newVisible) => setVisible(newVisible)}
      placement="bottomRight" 
    >
      <TeamOutlined style={{ fontSize: "24px", cursor: "pointer", color: "#ffffff" }} />
    </Popover>
  );
};

export default Followers;