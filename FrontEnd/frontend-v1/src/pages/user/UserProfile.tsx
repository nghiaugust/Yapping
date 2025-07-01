import React, { useState, useEffect } from 'react';
import { Typography, Card, Avatar, Space, Button, Empty, Spin } from 'antd';
import { LikeOutlined, MessageOutlined, RetweetOutlined, EditOutlined } from '@ant-design/icons';
import { getCurrentUser, UserProfile as UserProfileType } from '../../service/user/userService';
import { getUserPosts } from '../../service/user/postService';
import { getUserReposts } from '../../service/user/repostService';
import { getNotifications } from '../../service/user/notificationService';
import { Post } from '../../types/post';
import { Repost } from '../../service/user/repostService';
import { Notification } from '../../service/user/notificationService';
import { formatTimeAgo } from '../../utils/formatDate';

const { Title, Text, Paragraph } = Typography;

const UserProfile = () => {
  const [user, setUser] = useState<UserProfileType | null>(null);
  const [posts, setPosts] = useState<Post[]>([]);
  const [reposts, setReposts] = useState<Repost[]>([]);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [activeTab, setActiveTab] = useState<'posts' | 'reposts' | 'resources' | 'activity'>('posts');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [postsLoading, setPostsLoading] = useState(false);
  const [repostsLoading, setRepostsLoading] = useState(false);
  const [notificationsLoading, setNotificationsLoading] = useState(false);

  // Fetch user data on component mount
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        setLoading(true);
        const response = await getCurrentUser();
        if (response.success && response.data) {
          setUser(response.data);
        } else {
          setError(response.message || 'Không thể tải thông tin người dùng');
        }
      } catch (error) {
        console.error('Error fetching user data:', error);
        setError('Đã xảy ra lỗi khi tải thông tin người dùng');
      } finally {
        setLoading(false);
      }
    };

    void fetchUserData();
  }, []);

  // Fetch content based on active tab
  useEffect(() => {
    if (!user) return;

    const fetchTabContent = async () => {
      try {
        switch (activeTab) {
          case 'posts': {
            setPostsLoading(true);
            const postsResponse = await getUserPosts(user.id);
            if (postsResponse.success && postsResponse.data) {
              setPosts(postsResponse.data.content);
            }
            setPostsLoading(false);
            break;
          }          case 'reposts': {
            setRepostsLoading(true);
            try {
              const repostsResponse = await getUserReposts(user.id);
              if (repostsResponse.success && repostsResponse.data) {
                setReposts(repostsResponse.data.content);
              }
            } catch (error) {
              console.error('Error fetching reposts:', error);
            }
            setRepostsLoading(false);
            break;
          }

          case 'activity': {
            setNotificationsLoading(true);
            const notificationsResponse = await getNotifications();
            if (notificationsResponse.success && notificationsResponse.data) {
              setNotifications(notificationsResponse.data.content);
            }
            setNotificationsLoading(false);
            break;
          }

          default:
            break;
        }
      } catch (error) {
        console.error('Error fetching tab content:', error);
      }
    };

    void fetchTabContent();
  }, [activeTab, user]);
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };
  const renderTabContent = () => {
    switch (activeTab) {
      case 'posts':
        if (postsLoading) {
          return (
            <div style={{ textAlign: "center", padding: "40px 0" }}>
              <Spin size="large" tip="Đang tải bài viết..." />
            </div>
          );
        }
        return (
          <div style={{ padding: "0 20px" }}>
            {posts.length === 0 ? (
              <Empty description="Chưa có bài viết nào" />
            ) : (
              posts.map((post) => (
                <Card
                  key={post.id}
                  style={{
                    width: "100%",
                    borderRadius: "0px",
                    background: "#ffffff",
                    borderBottom: "1px solid #e8e8e8",
                    marginBottom: "0",
                    transition: "all 0.3s",
                    boxShadow: "none"
                  }}
                  hoverable
                >
                  <div style={{ display: "flex", alignItems: "flex-start" }}>
                    <Avatar
                      src={user?.profilePicture ? `http://localhost:8080/yapping${user.profilePicture}` : null}
                      size={40}
                      style={{ marginRight: 12 }}
                    >
                      {!user?.profilePicture && user?.fullName.charAt(0).toUpperCase()}
                    </Avatar>

                    <div style={{ flex: 1, overflow: "visible", minWidth: 0 }}>
                      <div style={{ display: "flex", justifyContent: "space-between" }}>
                        <div>
                          <Text strong style={{ color: "#8860D0" }}>
                            {user?.fullName}
                          </Text>
                          <Text type="secondary" style={{ marginLeft: "8px" }}>
                            @{user?.username}
                          </Text>
                        </div>
                        <Text type="secondary">{formatTimeAgo(post.createdAt)}</Text>
                      </div>

                      <Paragraph
                        style={{
                          color: "#333",
                          margin: "12px 0",
                          whiteSpace: "pre-wrap"
                        }}
                      >
                        {post.content}
                      </Paragraph>

                      {post.media && post.media.length > 0 && (
                        <div style={{
                          overflowX: 'auto',
                          overflowY: 'hidden',
                          margin: '10px 0',
                          paddingBottom: '8px'
                        }}>
                          <div style={{
                            display: 'flex',
                            gap: '8px',
                            width: 'max-content'
                          }}>
                            {post.media.map((media) => (
                              <div
                                key={media.id}
                                style={{
                                  width: '120px',
                                  height: '150px',
                                  borderRadius: '8px',
                                  overflow: 'hidden',
                                  position: 'relative',
                                  flexShrink: 0,
                                  cursor: 'pointer'
                                }}
                              >
                                {media.mediaType === 'IMAGE' ? (
                                  <img
                                    src={`http://localhost:8080/yapping${media.mediaUrl}`}
                                    alt="Post media"
                                    style={{
                                      width: '100%',
                                      height: '100%',
                                      objectFit: 'cover'
                                    }}
                                  />
                                ) : (
                                  <video
                                    src={`http://localhost:8080/yapping${media.mediaUrl}`}
                                    style={{
                                      width: '100%',
                                      height: '100%',
                                      objectFit: 'cover'
                                    }}
                                  />
                                )}
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      <Space size="large">
                        <Button type="text" icon={<LikeOutlined />}>
                          {post.likeCount ?? 0}
                        </Button>
                        <Button type="text" icon={<MessageOutlined />}>
                          {post.commentCount ?? 0}
                        </Button>
                        <Button type="text" icon={<RetweetOutlined />}>
                          {post.repostCount ?? 0}
                        </Button>
                      </Space>
                    </div>
                  </div>
                </Card>
              ))
            )}
          </div>
        );

      case 'reposts':
        if (repostsLoading) {
          return (
            <div style={{ textAlign: "center", padding: "40px 0" }}>
              <Spin size="large" tip="Đang tải bài chia sẻ..." />
            </div>
          );
        }
        return (
          <div style={{ padding: "0 20px" }}>
            {reposts.length === 0 ? (
              <Empty description="Chưa có bài chia sẻ nào" />
            ) : (
              reposts.map((repost) => (
                <Card
                  key={repost.id}
                  style={{
                    width: "100%",
                    borderRadius: "0px",
                    background: "#ffffff",
                    borderBottom: "1px solid #e8e8e8",
                    marginBottom: "0",
                    transition: "all 0.3s",
                    boxShadow: "none"
                  }}
                  hoverable
                >
                  <div style={{ marginBottom: "8px" }}>
                    <Text type="secondary" style={{ fontSize: "12px" }}>
                      <RetweetOutlined style={{ marginRight: "4px" }} />
                      Đã chia sẻ lúc {formatTimeAgo(repost.createdAt)}
                    </Text>
                  </div>

                  <div style={{ 
                    borderLeft: "3px solid #8860D0", 
                    paddingLeft: "12px",
                    marginLeft: "8px"
                  }}>
                    <div style={{ display: "flex", alignItems: "flex-start" }}>
                      <Avatar
                        src={repost.post.user?.profilePicture ? `http://localhost:8080/yapping${repost.post.user.profilePicture}` : null}
                        size={32}
                        style={{ marginRight: 8 }}
                      >
                        {!repost.post.user?.profilePicture && repost.post.user?.fullName?.charAt(0).toUpperCase()}
                      </Avatar>

                      <div style={{ flex: 1, overflow: "visible", minWidth: 0 }}>
                        <div>
                          <Text strong style={{ color: "#8860D0", fontSize: "14px" }}>
                            {repost.post.user?.fullName}
                          </Text>
                          <Text type="secondary" style={{ marginLeft: "8px", fontSize: "12px" }}>
                            @{repost.post.user?.username}
                          </Text>
                          <Text type="secondary" style={{ marginLeft: "8px", fontSize: "12px" }}>
                            {formatTimeAgo(repost.post.createdAt)}
                          </Text>
                        </div>

                        <Paragraph
                          style={{
                            color: "#333",
                            margin: "8px 0",
                            whiteSpace: "pre-wrap",
                            fontSize: "14px"
                          }}
                        >
                          {repost.post.content}
                        </Paragraph>

                        {repost.post.media && repost.post.media.length > 0 && (
                          <div style={{
                            overflowX: 'auto',
                            overflowY: 'hidden',
                            margin: '8px 0',
                            paddingBottom: '8px'
                          }}>
                            <div style={{
                              display: 'flex',
                              gap: '6px',
                              width: 'max-content'
                            }}>
                              {repost.post.media.map((media) => (
                                <div
                                  key={media.id}
                                  style={{
                                    width: '100px',
                                    height: '120px',
                                    borderRadius: '6px',
                                    overflow: 'hidden',
                                    position: 'relative',
                                    flexShrink: 0
                                  }}
                                >
                                  {media.mediaType === 'IMAGE' ? (
                                    <img
                                      src={`http://localhost:8080/yapping${media.mediaUrl}`}
                                      alt="Post media"
                                      style={{
                                        width: '100%',
                                        height: '100%',
                                        objectFit: 'cover'
                                      }}
                                    />
                                  ) : (
                                    <video
                                      src={`http://localhost:8080/yapping${media.mediaUrl}`}
                                      style={{
                                        width: '100%',
                                        height: '100%',
                                        objectFit: 'cover'
                                      }}
                                    />
                                  )}
                                </div>
                              ))}
                            </div>
                          </div>
                        )}

                        <Space size="large">
                          <Button type="text" icon={<LikeOutlined />} size="small">
                            {repost.post.likeCount ?? 0}
                          </Button>
                          <Button type="text" icon={<MessageOutlined />} size="small">
                            {repost.post.commentCount ?? 0}
                          </Button>
                        </Space>
                      </div>
                    </div>
                  </div>
                </Card>
              ))
            )}
          </div>
        );

      case 'resources':
        return (
          <div style={{ textAlign: "center", padding: "40px 0" }}>
            <Empty description="Tính năng tài nguyên đang được phát triển" />
          </div>
        );

      case 'activity':
        if (notificationsLoading) {
          return (
            <div style={{ textAlign: "center", padding: "40px 0" }}>
              <Spin size="large" tip="Đang tải lịch sử hoạt động..." />
            </div>
          );
        }
        return (
          <div style={{ padding: "0 20px" }}>
            {notifications.length === 0 ? (
              <Empty description="Chưa có hoạt động nào" />
            ) : (
              notifications.map((notification) => (
                <Card
                  key={notification.id}
                  style={{
                    width: "100%",
                    borderRadius: "0px",
                    background: "#ffffff",
                    borderBottom: "1px solid #e8e8e8",
                    marginBottom: "0",
                    transition: "all 0.3s",
                    boxShadow: "none"
                  }}
                  hoverable
                >
                  <div style={{ display: "flex", alignItems: "flex-start" }}>
                    <div style={{
                      width: "8px",
                      height: "8px",
                      borderRadius: "50%",
                      background: "#8860D0",
                      marginTop: "8px",
                      marginRight: "12px",
                      flexShrink: 0
                    }} />
                    <div style={{ flex: 1 }}>
                      <Paragraph style={{ color: "#333", margin: "0 0 8px 0" }}>
                        {notification.message}
                      </Paragraph>
                      <Text type="secondary" style={{ fontSize: "12px" }}>
                        {formatTimeAgo(notification.createdAt)}
                      </Text>
                    </div>
                  </div>
                </Card>
              ))
            )}
          </div>
        );

      default:
        return null;
    }
  };
  if (loading) {
    return (
      <div style={{ textAlign: "center", padding: "40px 0" }}>
        <Spin size="large" tip="Đang tải thông tin..." />
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ textAlign: "center", padding: "40px 0" }}>
        <Text type="danger" style={{ fontSize: "16px", marginBottom: "16px", display: "block" }}>
          {error}
        </Text>
        <Button 
          type="primary"
          onClick={() => window.location.reload()}
        >
          Thử lại
        </Button>
      </div>
    );
  }

  if (!user) {
    return (
      <div style={{ textAlign: "center", padding: "40px 0" }}>
        <Text type="secondary">Không tìm thấy thông tin người dùng</Text>
      </div>
    );
  }

  return (
    <>
      {/* Header */}
      <div style={{
        position: "sticky",
        top: 0,
        zIndex: 10,
        background: "white",
        padding: "10px",
        borderBottom: "1px solid #8860D0",
        boxShadow: "0 2px 5px rgba(0, 0, 0, 0.1)",
      }}>
        <Title level={2} style={{ color: "#5680E9", marginBottom: "0" }}>
          Hồ Sơ Người Dùng
        </Title>
      </div>

      {/* Content */}
      <div style={{
        overflowY: "auto",
        maxHeight: "calc(100vh - 130px)",
        paddingBottom: "10px",
        touchAction: "manipulation",
        WebkitOverflowScrolling: "touch"
      }}>
        {/* Profile Header */}
        <Card style={{
          borderRadius: "0px",
          background: "#ffffff",
          borderBottom: "1px solid #e8e8e8",
          marginBottom: "0",
          boxShadow: "none"
        }}>
          <div style={{ display: "flex", alignItems: "center", marginBottom: "24px" }}>
            <Avatar
              src={user.profilePicture ? `http://localhost:8080/yapping${user.profilePicture}` : null}
              size={80}
              style={{ marginRight: "20px", border: "4px solid #8860D0" }}
            >
              {!user.profilePicture && user.fullName.charAt(0).toUpperCase()}
            </Avatar>
            <div style={{ flex: 1 }}>
              <Title level={3} style={{ color: "#333", marginBottom: "8px" }}>
                {user.fullName}
              </Title>
              <Text type="secondary" style={{ fontSize: "16px", display: "block", marginBottom: "8px" }}>
                @{user.username}
              </Text>
              {user.bio && (
                <Paragraph style={{ color: "#666", marginBottom: "12px" }}>
                  {user.bio}
                </Paragraph>
              )}
              <Button 
                type="primary"
                icon={<EditOutlined />}
                style={{ background: "#8860D0", borderColor: "#8860D0" }}
              >
                Chỉnh sửa hồ sơ
              </Button>
            </div>
          </div>

          {/* Stats */}
          <div style={{ 
            display: "flex", 
            justifyContent: "space-around", 
            background: "#f5f5f5", 
            padding: "16px", 
            borderRadius: "8px",
            marginBottom: "24px"
          }}>
            <div style={{ textAlign: "center" }}>
              <Title level={4} style={{ color: "#333", marginBottom: "4px" }}>
                {user.postsCount ?? 0}
              </Title>
              <Text type="secondary">Bài viết</Text>
            </div>
            <div style={{ textAlign: "center" }}>
              <Title level={4} style={{ color: "#333", marginBottom: "4px" }}>
                {user.followersCount ?? 0}
              </Title>
              <Text type="secondary">Người theo dõi</Text>
            </div>
            <div style={{ textAlign: "center" }}>
              <Title level={4} style={{ color: "#333", marginBottom: "4px" }}>
                {user.followingCount ?? 0}
              </Title>
              <Text type="secondary">Đang theo dõi</Text>
            </div>
          </div>

          {/* Personal Info Section */}
          <div style={{ 
            background: "#f5f5f5", 
            padding: "16px", 
            borderRadius: "8px" 
          }}>
            <Title level={5} style={{ color: "#333", marginBottom: "16px" }}>
              Thông tin cá nhân
            </Title>
            <div style={{ 
              display: "grid", 
              gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
              gap: "16px"
            }}>
              <div>
                <Text strong style={{ color: "#666", fontSize: "14px", display: "block" }}>
                  Email
                </Text>
                <Text style={{ color: "#333", fontSize: "16px" }}>
                  {user.email}
                </Text>
              </div>
              <div>
                <Text strong style={{ color: "#666", fontSize: "14px", display: "block" }}>
                  Ngày tham gia
                </Text>
                <Text style={{ color: "#333", fontSize: "16px" }}>
                  {user.createdAt ? formatDate(user.createdAt) : 'N/A'}
                </Text>
              </div>
            </div>
          </div>
        </Card>

        {/* Tabs */}
        <div style={{
          position: "sticky",
          top: "56px",
          zIndex: 9,
          background: "white",
          borderBottom: "1px solid #e8e8e8",
          padding: "0 20px"
        }}>
          <div style={{ display: "flex", gap: "32px" }}>
            <Button
              type="text"
              onClick={() => setActiveTab('posts')}
              style={{
                padding: "16px 8px",
                borderBottom: activeTab === 'posts' ? "2px solid #8860D0" : "2px solid transparent",
                color: activeTab === 'posts' ? "#8860D0" : "#666",
                fontWeight: activeTab === 'posts' ? "600" : "400",
                borderRadius: "0",
                transition: "all 0.3s"
              }}
            >
              <Space>
                <MessageOutlined />
                <span>Bài viết</span>
              </Space>
            </Button>
            <Button
              type="text"
              onClick={() => setActiveTab('reposts')}
              style={{
                padding: "16px 8px",
                borderBottom: activeTab === 'reposts' ? "2px solid #8860D0" : "2px solid transparent",
                color: activeTab === 'reposts' ? "#8860D0" : "#666",
                fontWeight: activeTab === 'reposts' ? "600" : "400",
                borderRadius: "0",
                transition: "all 0.3s"
              }}
            >
              <Space>
                <RetweetOutlined />
                <span>Chia sẻ</span>
              </Space>
            </Button>
            <Button
              type="text"
              onClick={() => setActiveTab('resources')}
              style={{
                padding: "16px 8px",
                borderBottom: activeTab === 'resources' ? "2px solid #8860D0" : "2px solid transparent",
                color: activeTab === 'resources' ? "#8860D0" : "#666",
                fontWeight: activeTab === 'resources' ? "600" : "400",
                borderRadius: "0",
                transition: "all 0.3s"
              }}
            >
              <Space>
                <EditOutlined />
                <span>Tài nguyên</span>
              </Space>
            </Button>
            <Button
              type="text"
              onClick={() => setActiveTab('activity')}
              style={{
                padding: "16px 8px",
                borderBottom: activeTab === 'activity' ? "2px solid #8860D0" : "2px solid transparent",
                color: activeTab === 'activity' ? "#8860D0" : "#666",
                fontWeight: activeTab === 'activity' ? "600" : "400",
                borderRadius: "0",
                transition: "all 0.3s"
              }}
            >
              <Space>
                <LikeOutlined />
                <span>Lịch sử hoạt động</span>
              </Space>
            </Button>
          </div>
        </div>        {/* Tab Content */}
        {renderTabContent()}
      </div>
    </>
  );
};

export default UserProfile;