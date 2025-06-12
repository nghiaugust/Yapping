import React, { useEffect, useState, useCallback } from "react";
import { Typography, List, Card, Avatar, Space, Spin, Button, Empty, Pagination, Modal } from "antd";
import { LikeOutlined, LikeFilled, MessageOutlined, RetweetOutlined, EditOutlined, CloseOutlined, LeftOutlined, RightOutlined } from "@ant-design/icons";
import { getPublicPosts, likePost } from "../../service/user/postService";
import { getUserLikes, deleteLike } from "../../service/user/likeService";
import { getCurrentUser } from "../../service/user/userService";
import { Post } from "../../types/post";
import { Like } from "../../types/like";
import { formatTimeAgo } from "../../utils/formatDate";
import PostCommentsModal from "../../components/user/PostCommentsModal";
import "../../assets/styles/modal.css";

const { Title, Text, Paragraph } = Typography;

const Home: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [posts, setPosts] = useState<Post[]>([]);
  const [pagination, setPagination] = useState({
    current: 0,
    pageSize: 10,
    total: 0,
  });
  const [error, setError] = useState<string | null>(null);
  const [userLikes, setUserLikes] = useState<Like[]>([]);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  // Modal states
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [selectedMedia, setSelectedMedia] = useState<{ url: string; type: string } | null>(null);
  const [currentPostMedia, setCurrentPostMedia] = useState<{ id: number; mediaUrl: string; mediaType: string }[]>([]);
  const [currentMediaIndex, setCurrentMediaIndex] = useState<number>(0);

  // Comments modal states
  const [commentsModalVisible, setCommentsModalVisible] = useState<boolean>(false);
  const [selectedPost, setSelectedPost] = useState<Post | null>(null);

  // Modal handlers
  const handleCloseModal = useCallback(() => {
    setSelectedMedia(null);
    setModalVisible(false);
    setCurrentMediaIndex(0);
  }, []);

  const handleNextMedia = useCallback(() => {
    if (currentMediaIndex < currentPostMedia.length - 1) {
      const nextIndex = currentMediaIndex + 1;
      const nextMedia = currentPostMedia[nextIndex];
      setCurrentMediaIndex(nextIndex);
      setSelectedMedia({
        url: nextMedia.mediaUrl,
        type: nextMedia.mediaType.toLowerCase()
      });
    }
  }, [currentMediaIndex, currentPostMedia]);

  const handlePrevMedia = useCallback(() => {
    if (currentMediaIndex > 0) {
      const prevIndex = currentMediaIndex - 1;
      const prevMedia = currentPostMedia[prevIndex];
      setCurrentMediaIndex(prevIndex);
      setSelectedMedia({
        url: prevMedia.mediaUrl,
        type: prevMedia.mediaType.toLowerCase()
      });
    }
  }, [currentMediaIndex, currentPostMedia]);

  // Fetch posts  // Fetch user likes when component mounts
  const fetchUserLikes = useCallback(async (userId: number) => {
    try {
      const likesResponse = await getUserLikes(userId);
      if (likesResponse.success) {
        setUserLikes(likesResponse.data.content);
      }
    } catch (error) {
      console.error("Lỗi khi lấy danh sách lượt thích:", error);
    }
  }, []);

  // Fetch current user info on mount
  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const userResponse = await getCurrentUser();
        if (userResponse.success) {
          setCurrentUserId(userResponse.data.id);
          await fetchUserLikes(userResponse.data.id);
        }
      } catch (error) {
        console.error("Lỗi khi lấy thông tin người dùng:", error);
      }
    };

    void fetchCurrentUser();
  }, [fetchUserLikes]);

  // Update posts with like status based on userLikes
  useEffect(() => {
    if (userLikes.length > 0 && posts.length > 0) {
      const likedPostIds = new Set(
        userLikes
          .filter(like => like.targetType === "POST")
          .map(like => like.targetId)
      );

      setPosts(prevPosts => 
        prevPosts.map(post => ({
          ...post,
          isLiked: likedPostIds.has(post.id)
        }))
      );
    }
  }, [userLikes, posts.length]);

  const fetchPosts = useCallback(async (page = 0) => {
    try {
      setLoading(true);
      const response = await getPublicPosts(page, pagination.pageSize);
      if (response.success) {
        console.log("Dữ liệu bài đăng nhận được:", response.data.content);
        
        // Set posts first, the useEffect above will update with like status
        setPosts(response.data.content);
        setPagination({
          current: response.data.pageable.pageNumber,
          pageSize: response.data.pageable.pageSize,
          total: response.data.totalElements,
        });
      } else {
        setError("Không thể tải danh sách bài đăng");
      }
    } catch (err) {
      console.error("Lỗi khi lấy danh sách bài đăng:", err);
      setError("Đã xảy ra lỗi khi tải danh sách bài đăng");
    } finally {
      setLoading(false);
    }
  }, [pagination.pageSize]);

  useEffect(() => {
    void fetchPosts();
  }, [fetchPosts]);

  // Debug posts with media
  useEffect(() => {
    const postsWithMedia = posts.filter(post => post.media && post.media.length > 0);
    if (postsWithMedia.length > 0) {
      console.log("Các bài đăng có media:", postsWithMedia);
    } else {
      console.log("Không có bài đăng nào có media");
    }
  }, [posts]);

  // Keyboard navigation in modal
  useEffect(() => {
    if (!modalVisible) return;

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        handleCloseModal();
      } else if (e.key === "ArrowRight") {
        handleNextMedia();
      } else if (e.key === "ArrowLeft") {
        handlePrevMedia();
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [modalVisible, handleCloseModal, handleNextMedia, handlePrevMedia]);  // Handle like post
  const handleLikePost = async (postId: number) => {
    if (!currentUserId) {
      console.error("User ID không tồn tại");
      return;
    }

    try {
      // Check current like status
      const isCurrentlyLiked = userLikes.some(like => 
        like.targetType === "POST" && like.targetId === postId
      );

      // Optimistic update - cập nhật UI ngay lập tức
      setPosts(prevPosts => 
        prevPosts.map(post => 
          post.id === postId 
            ? {
                ...post,
                isLiked: !post.isLiked,
                likeCount: post.isLiked 
                  ? (post.likeCount ?? 0) - 1 
                  : (post.likeCount ?? 0) + 1
              }
            : post
        )
      );

      // Cập nhật userLikes state
      setUserLikes(prevLikes => {
        if (isCurrentlyLiked) {
          // Remove like
          return prevLikes.filter(like => 
            !(like.targetType === "POST" && like.targetId === postId)
          );
        } else {
          // Add like (tạo object like giả để cập nhật state)
          const newLike: Like = {
            id: Date.now(), // temporary ID
            userId: currentUserId,
            username: "", // sẽ được cập nhật từ API nếu cần
            userFullName: "",
            userProfilePicture: null,
            targetType: "POST",
            targetId: postId,
            createdAt: new Date().toISOString()
          };
          return [newLike, ...prevLikes];
        }
      });

      let response;
      if (isCurrentlyLiked) {
        // Find the like ID to delete
        const likeToDelete = userLikes.find(like => 
          like.targetType === "POST" && like.targetId === postId
        );
        if (likeToDelete) {
          response = await deleteLike(likeToDelete.id);
        } else {
          throw new Error("Không tìm thấy like để xóa");
        }
      } else {
        // Create new like
        response = await likePost(postId);
      }

      if (!response.success) {
        // Nếu API call thất bại, revert lại thay đổi
        setPosts(prevPosts => 
          prevPosts.map(post => 
            post.id === postId 
              ? {
                  ...post,
                  isLiked: !post.isLiked,
                  likeCount: post.isLiked 
                    ? (post.likeCount ?? 0) + 1 
                    : (post.likeCount ?? 0) - 1
                }
              : post
          )
        );

        // Revert userLikes state
        setUserLikes(prevLikes => {
          const wasLiked = prevLikes.some(like => 
            like.targetType === "POST" && like.targetId === postId
          );

          if (wasLiked) {
            return prevLikes.filter(like => 
              !(like.targetType === "POST" && like.targetId === postId)
            );
          } else {
            const newLike: Like = {
              id: Date.now(),
              userId: currentUserId,
              username: "",
              userFullName: "",
              userProfilePicture: null,
              targetType: "POST",
              targetId: postId,
              createdAt: new Date().toISOString()
            };
            return [newLike, ...prevLikes];
          }
        });

        console.error("Lỗi khi thích bài đăng");
      }
    } catch (err) {
      console.error("Lỗi khi thích bài đăng:", err);
      // Revert lại thay đổi khi có lỗi
      setPosts(prevPosts => 
        prevPosts.map(post => 
          post.id === postId 
            ? {
                ...post,
                isLiked: !post.isLiked,
                likeCount: post.isLiked 
                  ? (post.likeCount ?? 0) + 1 
                  : (post.likeCount ?? 0) - 1
              }
            : post
        )
      );

      // Revert userLikes state
      setUserLikes(prevLikes => {
        const wasLiked = prevLikes.some(like => 
          like.targetType === "POST" && like.targetId === postId
        );

        if (wasLiked) {
          return prevLikes.filter(like => 
            !(like.targetType === "POST" && like.targetId === postId)
          );
        } else {
          const newLike: Like = {
            id: Date.now(),
            userId: currentUserId,
            username: "",
            userFullName: "",
            userProfilePicture: null,
            targetType: "POST",
            targetId: postId,
            createdAt: new Date().toISOString()
          };
          return [newLike, ...prevLikes];
        }
      });
    }
  };

  // Handle comments modal
  const handleOpenCommentsModal = (post: Post) => {
    setSelectedPost(post);
    setCommentsModalVisible(true);
  };

  const handleCloseCommentsModal = () => {
    setCommentsModalVisible(false);
    setSelectedPost(null);
  };

  const handlePostUpdate = (updatedPost: Post) => {
    setPosts(prevPosts =>
      prevPosts.map(post =>
        post.id === updatedPost.id ? updatedPost : post
      )
    );
  };

  // Handle pagination
  const handlePageChange = (page: number) => {
    void fetchPosts(page - 1);
  };

  // Open media modal
  const handleOpenMediaModal = useCallback((media: { url: string; type: string }, postMedia: { id: number; mediaUrl: string; mediaType: string }[], index: number) => {
    setSelectedMedia(media);
    setCurrentPostMedia(postMedia);
    setCurrentMediaIndex(index);
    setModalVisible(true);
  }, []);

  // Simple media click without drag interference
  const handleMediaClick = useCallback((
    media: { url: string; type: string },
    postMedia: { id: number; mediaUrl: string; mediaType: string }[],
    index: number
  ) => {
    handleOpenMediaModal(media, postMedia, index);
  }, [handleOpenMediaModal]);

  // Add this useEffect to inject CSS once
  useEffect(() => {
    const styleId = 'media-scroll-styles';
    if (!document.getElementById(styleId)) {
      const style = document.createElement('style');
      style.id = styleId;
      style.textContent = `
        .simple-scroll::-webkit-scrollbar {
          height: 10px;
        }
        .simple-scroll::-webkit-scrollbar-track {
          background: #f1f1f1;
        }
        .simple-scroll::-webkit-scrollbar-thumb {
          background: #ccc;
          border-radius: 5px;
        }
        .simple-scroll::-webkit-scrollbar-thumb:hover {
          background: #999;
        }
      `;
      document.head.appendChild(style);
    }
  }, []);

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
          Dòng Thời Gian
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
        {loading ? (
          <div style={{ textAlign: "center", padding: "40px 0" }}>
            <Spin size="large" tip="Đang tải bài đăng..." />
          </div>
        ) : error ? (
          <div style={{ textAlign: "center", padding: "20px 0" }}>
            <Text type="danger">{error}</Text>
            <Button onClick={() => { void fetchPosts(); }} style={{ marginTop: 10 }}>
              Thử lại
            </Button>
          </div>
        ) : posts.length === 0 ? (
          <Empty description="Không có bài đăng nào" />
        ) : (
          <>
            <List
              dataSource={posts}
              renderItem={(post) => (
                <List.Item style={{ padding: 0, marginBottom: "0" }}>
                  <Card
                    style={{
                      width: "100%",
                      borderRadius: "0px",
                      background: "#ffffff",
                      borderBottom: "1px #808080",
                      transition: "all 0.3s",
                      boxShadow: "none",
                      overflow: "visible"
                    }}
                    hoverable
                  >
                    <div style={{ display: "flex", alignItems: "flex-start" }}>
                      <Avatar
                        src={post.user.profilePicture ? `http://localhost:8080/yapping${post.user.profilePicture}` : null}
                        size={40}
                        style={{ marginRight: 12 }}
                      >
                        {!post.user.profilePicture && post.user.fullName.charAt(0).toUpperCase()}
                      </Avatar>

                      <div style={{
                        flex: 1,
                        overflow: "visible",
                        minWidth: 0
                      }}>
                        <div style={{ display: "flex", justifyContent: "space-between" }}>
                          <div>
                            <Text strong style={{ color: "#8860D0" }}>
                              {post.user.fullName}
                            </Text>
                            <Text type="secondary" style={{ marginLeft: "8px" }}>
                              @{post.user.username}
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
                              {post.media.map((media, index) => (
                                <div
                                  key={media.id}
                                  onClick={() => handleMediaClick(
                                    { url: media.mediaUrl, type: media.mediaType.toLowerCase() },
                                    post.media ?? [],
                                    index
                                  )}
                                  style={{
                                    width: '120px',
                                    height: '150px',
                                    borderRadius: '8px',
                                    overflow: 'hidden',
                                    cursor: 'pointer',
                                    border: '1px solid #ddd',
                                    flexShrink: 0,
                                    position: 'relative'
                                  }}
                                >
                                  {media.mediaType.toLowerCase() === 'image' ? (
                                    <img
                                      src={`http://localhost:8080/yapping${media.mediaUrl}`}
                                      alt="Media content"
                                      style={{
                                        width: '100%',
                                        height: '100%',
                                        objectFit: 'cover'
                                      }}
                                      draggable={false}
                                    />
                                  ) : media.mediaType.toLowerCase() === 'video' ? (
                                    <>
                                      <video
                                        src={`http://localhost:8080/yapping${media.mediaUrl}`}
                                        style={{
                                          width: '100%',
                                          height: '100%',
                                          objectFit: 'cover'
                                        }}
                                        preload="metadata"
                                      />
                                      <div style={{
                                        position: 'absolute',
                                        top: '50%',
                                        left: '50%',
                                        transform: 'translate(-50%, -50%)',
                                        backgroundColor: 'rgba(0,0,0,0.7)',
                                        borderRadius: '50%',
                                        width: '30px',
                                        height: '30px',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        color: 'white',
                                        fontSize: '12px',
                                        pointerEvents: 'none'
                                      }}>
                                        ▶
                                      </div>
                                    </>
                                  ) : null}
                                </div>
                              ))}
                            </div>
                          </div>
                        )}                        <Space size="large">
                          <Button
                            type="text"
                            icon={post.isLiked ? <LikeFilled style={{ color: '#ff4d4f' }} /> : <LikeOutlined />}
                            onClick={() => { void handleLikePost(post.id); }}
                            style={post.isLiked ? { color: '#ff4d4f' } : {}}
                          >
                            {post.likeCount ?? 0}
                          </Button><Button type="text" icon={<MessageOutlined />}
                            onClick={() => handleOpenCommentsModal(post)}
                          >
                            {post.commentCount ?? 0}
                          </Button>
                          <Button type="text" icon={<RetweetOutlined />}>
                            {post.repostCount ?? 0}
                          </Button>
                          <Button type="text" icon={<EditOutlined />}>
                            {post.quoteCount ?? 0}
                          </Button>
                        </Space>
                      </div>
                    </div>
                  </Card>
                </List.Item>
              )}
            />

            <div style={{ textAlign: "center", margin: "10px 0" }}>
              <Pagination
                current={pagination.current + 1}
                pageSize={pagination.pageSize}
                total={pagination.total}
                onChange={handlePageChange}
                showSizeChanger={false}
              />
            </div>
          </>
        )}
      </div>

      <Modal
        open={modalVisible}
        footer={null}
        onCancel={handleCloseModal}
        closeIcon={<CloseOutlined />}
        centered={true}
        width="100%"
        bodyStyle={{
          padding: 0,
          height: "100%",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
        style={{
          padding: 0,
          margin: 0,
          maxWidth: "95vw",
          maxHeight: "90vh",
        }}
        wrapClassName="threads-modal"
        mask={true}
      >
        {selectedMedia && (
          <div style={{
            width: "100%",
            height: "100%",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            position: "relative"
          }}>
            {currentMediaIndex > 0 && (
              <Button
                type="text"
                icon={<LeftOutlined />}
                onClick={handlePrevMedia}
                style={{
                  position: "absolute",
                  left: "20px",
                  top: "50%",
                  transform: "translateY(-50%)",
                  fontSize: "24px",
                  color: "#fff",
                  background: "rgba(0, 0, 0, 0.3)",
                  borderRadius: "50%",
                  width: "50px",
                  height: "50px",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  zIndex: 2
                }}
              />
            )}

            {currentMediaIndex < currentPostMedia.length - 1 && (
              <Button
                type="text"
                icon={<RightOutlined />}
                onClick={handleNextMedia}
                style={{
                  position: "absolute",
                  right: "20px",
                  top: "50%",
                  transform: "translateY(-50%)",
                  fontSize: "24px",
                  color: "#fff",
                  background: "rgba(0, 0, 0, 0.3)",
                  borderRadius: "50%",
                  width: "50px",
                  height: "50px",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  zIndex: 2
                }}
              />
            )}

            <div style={{
              maxWidth: "90%",
              maxHeight: "90%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center"
            }}>
              {selectedMedia.type === "image" ? (
                <img
                  src={`http://localhost:8080/yapping${selectedMedia.url}`}
                  alt="Full media"
                  style={{ maxWidth: "100%", maxHeight: "80vh", objectFit: "contain" }}
                />
              ) : selectedMedia.type === "video" ? (
                <video
                  src={`http://localhost:8080/yapping${selectedMedia.url}`}
                  controls
                  autoPlay
                  style={{ maxWidth: "100%", maxHeight: "80vh" }}
                />
              ) : null}
            </div>

            <div style={{
              position: "absolute",
              bottom: "20px",
              display: "flex",
              gap: "8px"
            }}>
              {currentPostMedia.map((_, idx) => (
                <div
                  key={idx}
                  style={{
                    width: "8px",
                    height: "8px",
                    borderRadius: "50%",
                    background: idx === currentMediaIndex ? "#ffffff" : "rgba(255, 255, 255, 0.5)",
                  }}
                />
              ))}
            </div>
          </div>
        )}      </Modal>

      {/* Comments Modal */}
      <PostCommentsModal
        visible={commentsModalVisible}
        post={selectedPost}
        onClose={handleCloseCommentsModal}
        onPostUpdate={handlePostUpdate}
      />
    </>
  );
};

export default Home;