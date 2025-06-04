// src/pages/user/Home.tsx
import React, { useEffect, useState, useCallback, useRef } from "react";
import { Typography, List, Card, Avatar, Space, Spin, Button, Empty, Pagination, Modal } from "antd";
import { LikeOutlined, MessageOutlined, RetweetOutlined, EditOutlined, CloseOutlined, LeftOutlined, RightOutlined } from "@ant-design/icons";
import { getPublicPosts, likePost } from "../../service/user/postService";
import { Post } from "../../types/post";
import { formatTimeAgo } from "../../utils/formatDate";
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
  // Thêm state cho modal xem media toàn màn hình và điều hướng
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [selectedMedia, setSelectedMedia] = useState<{ url: string; type: string } | null>(null);
  const [currentPostMedia, setCurrentPostMedia] = useState<{id: number; mediaUrl: string; mediaType: string}[]>([]);
  const [currentMediaIndex, setCurrentMediaIndex] = useState<number>(0);
  
  // Định nghĩa các hàm xử lý media với useCallback để tránh vòng lặp phụ thuộc
  const handleCloseModal = useCallback(() => {
    setSelectedMedia(null);
    setModalVisible(false);
    setCurrentMediaIndex(0);
  }, []);

  // Hàm chuyển đến media kế tiếp
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

  // Hàm chuyển đến media trước đó
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

  // Sử dụng useCallback để tránh tạo lại hàm fetchPosts khi component render
  const fetchPosts = useCallback(async (page = 0) => {
    try {
      setLoading(true);
      const response = await getPublicPosts(page, pagination.pageSize);
      if (response.success) {
        console.log("Dữ liệu bài đăng nhận được:", response.data.content);
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
    // Thêm effect để debug các bài đăng có media
  useEffect(() => {
    // Log các bài đăng có media để kiểm tra
    const postsWithMedia = posts.filter(post => post.media && post.media.length > 0);
    if (postsWithMedia.length > 0) {
      console.log("Các bài đăng có media:", postsWithMedia);
    } else {
      console.log("Không có bài đăng nào có media");
    }
  }, [posts]);
  // Xử lý phím tắt trong modal
  useEffect(() => {
    // Chỉ xử lý phím tắt khi modal hiển thị
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
    
    // Cleanup listener khi component unmount hoặc modal đóng
    return () => {
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [modalVisible, handleCloseModal, handleNextMedia, handlePrevMedia]);  // Cleanup tất cả event listeners khi component unmount
  useEffect(() => {
    const currentCleanupRefs = cleanupRefs.current; // Copy reference trong effect
    return () => {
      currentCleanupRefs.forEach((cleanup) => cleanup());
      currentCleanupRefs.clear();
    };
  }, []);

  const handleLikePost = async (postId: number) => {
    try {
      const response = await likePost(postId);
      if (response.success) {
        // Cập nhật lại danh sách bài đăng sau khi like
        void fetchPosts(pagination.current);
      }
    } catch (err) {
      console.error("Lỗi khi thích bài đăng:", err);
    }
  };
  const handlePageChange = (page: number) => {
    void fetchPosts(page - 1); // API sử dụng page bắt đầu từ 0
  };  // Hàm mở modal xem media
  const handleOpenMediaModal = useCallback((media: { url: string; type: string }, postMedia: {id: number; mediaUrl: string; mediaType: string}[], index: number) => {
    setSelectedMedia(media);
    setCurrentPostMedia(postMedia);
    setCurrentMediaIndex(index);
    setModalVisible(true);
  }, []);

  // Hàm xử lý click media với kiểm tra drag
  const handleMediaClick = useCallback((
    e: React.MouseEvent,
    media: { url: string; type: string }, 
    postMedia: {id: number; mediaUrl: string; mediaType: string}[], 
    index: number
  ) => {
    // Kiểm tra xem có đang trong quá trình drag không
    const target = e.currentTarget as HTMLElement;
    const scroller = target.closest('.media-scroller') as HTMLElement;
    
    if (scroller && scroller.style.cursor === 'grabbing') {
      e.preventDefault();
      e.stopPropagation();
      return;
    }
    
    handleOpenMediaModal(media, postMedia, index);
  }, [handleOpenMediaModal]);// useRef để lưu cleanup function
  const cleanupRefs = useRef<Map<HTMLDivElement, () => void>>(new Map());  // Hàm tối ưu để xử lý horizontal scrolling như Threads
  const setupMediaScroller = useCallback((element: HTMLDivElement) => {
    if (!element) return;
    if (element.scrollWidth <= element.clientWidth) {
      element.style.overflowX = 'hidden';
      return;
    }

    // Cấu hình CSS
    element.style.scrollBehavior = 'auto';
    element.style.touchAction = 'pan-x';
    element.style.overflowX = 'auto';
    element.style.overflowY = 'hidden';
    element.style.cursor = 'grab';
    element.style.userSelect = 'none';
    element.style.WebkitUserDrag = 'none';
    element.style.WebkitOverflowScrolling = 'touch';
    element.style.scrollSnapType = 'x proximity';
    element.style.scrollPadding = '8px';
    element.classList.add('media-scroller', 'hide-scrollbar');
    element.tabIndex = 0;

    let isDragging = false;
    let startX = 0;
    let scrollLeft = 0;
    let hasMoved = false;
    const MOVEMENT_THRESHOLD = 5; // Ngưỡng 5px

    const handleMouseDown = (e: MouseEvent) => {
      if (e.button !== 0) return;
      isDragging = true;
      hasMoved = false;
      startX = e.pageX - element.offsetLeft;
      scrollLeft = element.scrollLeft;
      element.style.cursor = 'grabbing';
      document.body.style.cursor = 'grabbing';
      element.classList.add('dragging');
      element.style.scrollBehavior = 'auto';
      element.style.scrollSnapType = 'none';
      e.preventDefault();
    };

    const handleMouseMove = (e: MouseEvent) => {
      if (!isDragging) return;
      e.preventDefault();
      const x = e.pageX - element.offsetLeft;
      const walkX = (x - startX) * 1.5;
      if (Math.abs(x - startX) > MOVEMENT_THRESHOLD) {
        hasMoved = true;
      }
      const newScrollLeft = scrollLeft - walkX;
      const maxScrollLeft = element.scrollWidth - element.clientWidth;
      element.scrollLeft = Math.max(0, Math.min(newScrollLeft, maxScrollLeft));
    };

    const handleMouseUp = () => {
      if (!isDragging) return;
      isDragging = false;
      element.style.cursor = 'grab';
      document.body.style.cursor = '';
      element.classList.remove('dragging');
      element.style.scrollBehavior = 'smooth';
      element.style.scrollSnapType = 'x proximity';
      hasMoved = false;
    };

    const handleTouchStart = (e: TouchEvent) => {
      isDragging = true;
      hasMoved = false;
      startX = e.touches[0].pageX - element.offsetLeft;
      scrollLeft = element.scrollLeft;
      element.style.scrollBehavior = 'auto';
      element.style.scrollSnapType = 'none';
      element.classList.add('dragging');
      if (e.cancelable) {
        e.preventDefault();
      }
    };

    const handleTouchMove = (e: TouchEvent) => {
      if (!isDragging) return;
      const x = e.touches[0].pageX - element.offsetLeft;
      const walkX = (x - startX) * 1.2;
      if (Math.abs(x - startX) > MOVEMENT_THRESHOLD) {
        hasMoved = true;
      }
      const newScrollLeft = scrollLeft - walkX;
      const maxScrollLeft = element.scrollWidth - element.clientWidth;
      element.scrollLeft = Math.max(0, Math.min(newScrollLeft, maxScrollLeft));
      if (e.cancelable) {
        e.preventDefault();
      }
    };

    const handleTouchEnd = () => {
      if (!isDragging) return;
      isDragging = false;
      element.classList.remove('dragging');
      element.style.scrollBehavior = 'smooth';
      element.style.scrollSnapType = 'x proximity';
      hasMoved = false;
    };

    const handleClick = (e: Event) => {
      if (hasMoved) {
        e.preventDefault();
        e.stopPropagation();
        e.stopImmediatePropagation();
        hasMoved = false;
        return false;
      }
    };

    // Gắn sự kiện vào document
    document.addEventListener('mousemove', handleMouseMove, { passive: false });
    document.addEventListener('mouseup', handleMouseUp);
    document.addEventListener('touchstart', handleTouchStart, { passive: false });
    document.addEventListener('touchmove', handleTouchMove, { passive: false });
    document.addEventListener('touchend', handleTouchEnd);
    element.addEventListener('click', handleClick, { capture: true });

    // Ngăn drag mặc định của hình ảnh
    const images = element.querySelectorAll('img');
    images.forEach(img => {
      img.addEventListener('dragstart', (e) => e.preventDefault());
    });

    // Xử lý phím bấm
    const handleKeyDown = (e: KeyboardEvent) => {
      if (document.activeElement === element || element.contains(document.activeElement)) {
        const itemWidth = element.children[1]?.getBoundingClientRect().width || 200;
        if (e.key === 'ArrowLeft') {
          element.scrollBy({ left: -itemWidth, behavior: 'smooth' });
          e.preventDefault();
        } else if (e.key === 'ArrowRight') {
          element.scrollBy({ left: itemWidth, behavior: 'smooth' });
          e.preventDefault();
        }
      }
    };

    element.addEventListener('keydown', handleKeyDown);
    element.addEventListener('focus', () => {
      element.style.outline = 'none';
      element.style.boxShadow = '0 0 0 2px rgba(24, 144, 255, 0.3)';
    });
    element.addEventListener('blur', () => {
      element.style.boxShadow = 'none';
    });

    // Cleanup
    const cleanup = () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
      document.removeEventListener('touchstart', handleTouchStart);
      document.removeEventListener('touchmove', handleTouchMove);
      document.removeEventListener('touchend', handleTouchEnd);
      element.removeEventListener('click', handleClick);
      element.removeEventListener('keydown', handleKeyDown);
      element.removeEventListener('focus', () => {});
      element.removeEventListener('blur', () => {});
      const imgs = element.querySelectorAll('img');
      imgs.forEach(img => {
        img.removeEventListener('dragstart', (e) => e.preventDefault());
      });
      cleanupRefs.current.delete(element);
    };

    cleanupRefs.current.set(element, cleanup);
    return cleanup;
  }, []);

  return (    
    <>
      <div style={{ 
        position: "sticky", 
        top: 0, 
        zIndex: 10,
        background: "white",
        padding: "10px",
        borderBottom: "1px solid #8860D0",
        boxShadow: "0 2px 5px rgba(0, 0, 0, 0.1)",// đổ bóng
      }}>
        <Title level={2} style={{ color: "#5680E9", marginBottom: "0" }}>
          Dòng Thời Gian
        </Title>
      </div>        <div style={{ 
        overflowY: "auto", 
        maxHeight: "calc(100vh - 130px)",
        paddingBottom: "10px",
        touchAction: "manipulation", // Cho phép trình duyệt tối ưu tương tác
        WebkitOverflowScrolling: "touch" // Cuộn mượt trên iOS
      }}>
        {loading ? (
          <div style={{ textAlign: "center", padding: "40px 0" }}>
            <Spin size="large" tip="Đang tải bài đăng..." />
          </div>      ) : error ? (
          <div style={{ textAlign: "center", padding: "20px 0" }}>
            <Text type="danger">{error}</Text>
            <Button onClick={() => { void fetchPosts(); }} style={{ marginTop: 10 }}>
              Thử lại
            </Button>
          </div>
        ) : posts.length === 0 ? (
          <Empty description="Không có bài đăng nào" />
        ) : (
          <>          <List
            dataSource={posts}
            renderItem={(post) => (
              <List.Item style={{ padding: 0, marginBottom: "0" }}>                <Card                  style={{
                    width: "100%",
                    borderRadius: "0px",
                    background: "#ffffff", 
                    borderBottom: "1px #808080",
                    transition: "all 0.3s",
                    boxShadow: "none"
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
                    <div style={{ flex: 1 }}>
                      <div style={{ display: "flex", justifyContent: "space-between" }}>
                        <div>                          <Text strong style={{ color: "#8860D0" }}>
                            {post.user.fullName}
                          </Text><Text type="secondary" style={{ marginLeft: "8px" }}>
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
                        {post.content}                      </Paragraph>                        {post.media && post.media.length > 0 && (                        <div 
                          className="media-scroller hide-scrollbar"
                          ref={setupMediaScroller}
                          style={{
                            display: 'flex',
                            gap: '8px',
                            margin: '10px 0',
                            maxWidth: '100%',
                            borderRadius: '8px',
                            overflow: 'auto',
                            touchAction: 'pan-x', // Chỉ cho phép cuộn ngang
                            WebkitOverflowScrolling: 'touch', // Cuộn mượt trên iOS
                            scrollSnapType: 'x mandatory', // Thêm snap mạnh hơn để người dùng cảm thấy ảnh "đóng khung"
                            position: 'relative',
                            cursor: 'grab',
                            padding: '4px', // Thêm padding để dễ thấy là có thể cuộn
                            backgroundColor: 'rgba(0,0,0,0.02)', // Thêm màu nền nhẹ để phân biệt khu vực cuộn
                            borderRadius: '12px'
                          }}
                        >                          {post.media.map((media, index) => (
                            <div 
                              key={media.id}
                              onClick={(e) => handleMediaClick(
                                e,
                                {url: media.mediaUrl, type: media.mediaType.toLowerCase()},
                                post.media ?? [],
                                index
                              )}
                              style={{
                                minWidth: '200px',
                                height: '250px',
                                borderRadius: '8px',
                                overflow: 'hidden',
                                flexShrink: 0
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
                                />
                              ) : media.mediaType.toLowerCase() === 'video' ? (
                                <video 
                                  src={`http://localhost:8080/yapping${media.mediaUrl}`}
                                  preload="metadata"
                                  style={{
                                    width: '100%',
                                    height: '100%',
                                    objectFit: 'cover'
                                  }}
                                />
                              ) : null}
                            </div>
                          ))}
                        </div>
                      )}
                      
                      <Space size="large">                        <Button 
                          type="text" 
                          icon={<LikeOutlined />} 
                          onClick={() => { void handleLikePost(post.id); }}
                        >
                          {post.likeCount ?? 0}
                        </Button>
                        <Button type="text" icon={<MessageOutlined />}>
                          {post.commentCount ?? 0}
                        </Button>                        <Button type="text" icon={<RetweetOutlined />}>
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
              current={pagination.current + 1} // +1 vì API bắt đầu từ 0
              pageSize={pagination.pageSize}
              total={pagination.total}
              onChange={handlePageChange}
              showSizeChanger={false}
            />
          </div>
        </>
      )}
      </div>      {/* Modal xem media toàn màn hình */}      <Modal
        open={modalVisible}
        footer={null}
        onCancel={handleCloseModal}
        closeIcon={<CloseOutlined />}
        centered={true}
        width="100%"        bodyStyle={{ 
          padding: 0,
          height: "100%", // Thay đổi từ 100vh thành 100% để phù hợp với container
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}style={{ 
          padding: 0,
          margin: 0,
          maxWidth: "95vw", // Giảm chiều rộng xuống 95% để có khoảng trống hai bên
          maxHeight: "90vh", // Giảm chiều cao xuống 90% để không phải lăn chuột
        }}
        wrapClassName="threads-modal"        
        mask={true}      >
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
            {/* Navigation arrows */}
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
            
            {/* Media content */}
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
            
            {/* Page indicator */}
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
        )}
      </Modal>
    </>
  );
};

export default Home;