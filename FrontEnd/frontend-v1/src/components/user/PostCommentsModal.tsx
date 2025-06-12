// src/components/user/PostCommentsModal.tsx
import React, { useState, useEffect, useCallback } from 'react';
import { 
  Modal, 
  Card, 
  Avatar, 
  Typography, 
  Space, 
  Button, 
  Input, 
  List, 
  Spin, 
  Empty, 
  message,
  Dropdown
} from 'antd';
import { 
  LikeOutlined, 
  SendOutlined,
  MoreOutlined,
  EditOutlined,
  DeleteOutlined,
  LikeFilled
} from '@ant-design/icons';
import { Post } from '../../types/post';
import { Comment } from '../../types/comment';
import { 
  getPostComments, 
  createComment, 
  likeComment, 
  deleteComment, 
  updateComment 
} from '../../service/user/commentService';
import { getUserLikes, deleteLike } from '../../service/user/likeService';
import { formatTimeAgo } from '../../utils/formatDate';
import { getCurrentUser, UserProfile } from '../../service/user/userService';
import { Like } from '../../types/like';

const { Title, Text, Paragraph } = Typography;
const { TextArea } = Input;

interface PostCommentsModalProps {
  visible: boolean;
  post: Post | null;
  onClose: () => void;
  onPostUpdate?: (updatedPost: Post) => void;
}

const PostCommentsModal: React.FC<PostCommentsModalProps> = ({
  visible,
  post,
  onClose,
  onPostUpdate
}) => {
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(false);
  const [commentLoading, setCommentLoading] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [currentUser, setCurrentUser] = useState<UserProfile | null>(null);
  const [userLikes, setUserLikes] = useState<Like[]>([]);
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState('');
  const [pagination, setPagination] = useState({
    current: 0,
    total: 0,
    hasMore: true
  });

  // Fetch user likes when component mounts
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
  // Lấy thông tin user hiện tại
  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const response = await getCurrentUser();
        if (response.success && response.data) {
          setCurrentUser(response.data);
          await fetchUserLikes(response.data.id);
        }
      } catch (error) {
        console.error('Lỗi khi lấy thông tin user:', error);
      }
    };

    if (visible) {
      void fetchCurrentUser();
    }
  }, [visible, fetchUserLikes]);
  // Lấy danh sách bình luận  // Update comments with like status when userLikes changes
  useEffect(() => {
    if (userLikes.length > 0 && comments.length > 0) {
      const likedCommentIds = new Set(
        userLikes
          .filter(like => like.targetType === "COMMENT")
          .map(like => like.targetId)
      );

      setComments(prevComments => 
        prevComments.map(comment => ({
          ...comment,
          isLiked: likedCommentIds.has(comment.id)
        }))
      );
    }
  }, [userLikes, comments.length]);

  const fetchComments = useCallback(async (page = 0, reset = true) => {
    if (!post) return;

    try {
      console.log('Fetching comments for post:', post.id);
      setLoading(page === 0);
      const response = await getPostComments(post.id, page, 10);
      
      console.log('Comments response:', response);
      
      if (response.success) {
        console.log('Comments data:', response.data);
        const commentsWithLikeStatus = response.data.content;
        
        if (reset) {
          setComments(commentsWithLikeStatus);
        } else {
          setComments(prev => [...prev, ...commentsWithLikeStatus]);
        }
        
        setPagination({
          current: response.data.number,
          total: response.data.totalElements,
          hasMore: !response.data.last
        });
      } else {
        console.error('Failed to fetch comments:', response.message);
      }
    } catch (error) {
      console.error('Lỗi khi lấy bình luận:', error);
      message.error('Không thể tải bình luận');
    } finally {
      setLoading(false);
    }
  }, [post]);

  // Load comments when modal opens
  useEffect(() => {
    if (visible && post) {
      void fetchComments();
    }
  }, [visible, post, fetchComments]);

  // Tạo bình luận mới
  const handleCreateComment = async () => {
    if (!newComment.trim() || !post || !currentUser) return;

    try {
      setCommentLoading(true);
      const response = await createComment({
        content: newComment.trim(),
        postId: post.id
      });

      if (response.success) {
        setNewComment('');
        void fetchComments(); // Reload comments
        message.success('Bình luận đã được thêm');
          // Cập nhật số lượng comment của post
        if (onPostUpdate) {
          onPostUpdate({
            ...post,
            commentCount: (post.commentCount ?? 0) + 1
          });
        }
      } else {
        message.error(response.message);
      }
    } catch (error) {
      console.error('Lỗi khi tạo bình luận:', error);
      message.error('Không thể thêm bình luận');
    } finally {
      setCommentLoading(false);
    }
  };  // Thích bình luận
  const handleLikeComment = async (commentId: number) => {
    if (!currentUser) {
      message.error('Vui lòng đăng nhập để thích bình luận');
      return;
    }

    // Find the comment to get current like status
    const comment = comments.find(c => c.id === commentId);
    if (!comment) return;

    const wasLiked = comment.isLiked;

    // Optimistic update for comments
    setComments(prev => prev.map(c => 
      c.id === commentId 
        ? {
            ...c,
            isLiked: !wasLiked,
            likeCount: wasLiked ? c.likeCount - 1 : c.likeCount + 1
          }
        : c
    ));

    // Optimistic update for userLikes
    if (wasLiked) {
      // Remove like from userLikes
      setUserLikes(prev => prev.filter(like => 
        !(like.targetType === "COMMENT" && like.targetId === commentId)
      ));
    } else {
      // Add like to userLikes
      const newLike: Like = {
        id: Date.now(), // Temporary ID
        targetType: "COMMENT",
        targetId: commentId,
        userId: currentUser.id,
        username: currentUser.username,
        userFullName: currentUser.fullName,
        userProfilePicture: currentUser.profilePicture,
        createdAt: new Date().toISOString()
      };
      setUserLikes(prev => [...prev, newLike]);
    }

    try {
      let response;
      if (wasLiked) {
        // Find the like ID to delete
        const likeToDelete = userLikes.find(like => 
          like.targetType === "COMMENT" && like.targetId === commentId
        );
        if (likeToDelete) {
          response = await deleteLike(likeToDelete.id);
        } else {
          throw new Error("Không tìm thấy like để xóa");
        }
      } else {
        // Create new like
        response = await likeComment(commentId);
      }

      if (!response.success) {
        // Rollback on error
        setComments(prev => prev.map(c => 
          c.id === commentId 
            ? {
                ...c,
                isLiked: wasLiked,
                likeCount: wasLiked ? c.likeCount + 1 : c.likeCount - 1
              }
            : c
        ));

        // Rollback userLikes
        if (wasLiked) {
          const rollbackLike: Like = {
            id: Date.now(),
            targetType: "COMMENT",
            targetId: commentId,
            userId: currentUser.id,
            username: currentUser.username,
            userFullName: currentUser.fullName,
            userProfilePicture: currentUser.profilePicture,
            createdAt: new Date().toISOString()
          };
          setUserLikes(prev => [...prev, rollbackLike]);
        } else {
          setUserLikes(prev => prev.filter(like => 
            !(like.targetType === "COMMENT" && like.targetId === commentId)
          ));
        }

        message.error(response.message || 'Không thể thích bình luận');
      }
    } catch (error) {
      // Rollback on error
      setComments(prev => prev.map(c => 
        c.id === commentId 
          ? {
              ...c,
              isLiked: wasLiked,
              likeCount: wasLiked ? c.likeCount + 1 : c.likeCount - 1
            }
          : c
      ));

      // Rollback userLikes
      if (wasLiked) {
        const rollbackLike: Like = {
          id: Date.now(),
          targetType: "COMMENT",
          targetId: commentId,
          userId: currentUser.id,
          username: currentUser.username,
          userFullName: currentUser.fullName,
          userProfilePicture: currentUser.profilePicture,
          createdAt: new Date().toISOString()
        };
        setUserLikes(prev => [...prev, rollbackLike]);
      } else {
        setUserLikes(prev => prev.filter(like => 
          !(like.targetType === "COMMENT" && like.targetId === commentId)
        ));
      }

      console.error('Lỗi khi thích bình luận:', error);
      message.error('Không thể thích bình luận');
    }
  };

  // Xóa bình luận
  const handleDeleteComment = async (commentId: number) => {
    try {
      const response = await deleteComment(commentId);
      if (response.success) {
        setComments(prev => prev.filter(comment => comment.id !== commentId));
        message.success('Đã xóa bình luận');
          // Cập nhật số lượng comment của post
        if (onPostUpdate && post) {
          onPostUpdate({
            ...post,
            commentCount: Math.max((post.commentCount ?? 0) - 1, 0)
          });
        }
      } else {
        message.error(response.message);
      }
    } catch (error) {
      console.error('Lỗi khi xóa bình luận:', error);
      message.error('Không thể xóa bình luận');
    }
  };

  // Bắt đầu chỉnh sửa
  const startEdit = (comment: Comment) => {
    setEditingCommentId(comment.id);
    setEditContent(comment.content);
  };

  // Hủy chỉnh sửa
  const cancelEdit = () => {
    setEditingCommentId(null);
    setEditContent('');
  };

  // Lưu chỉnh sửa
  const saveEdit = async (commentId: number) => {
    if (!editContent.trim()) return;

    try {
      const response = await updateComment(commentId, editContent.trim());
      if (response.success) {
        setComments(prev => prev.map(comment => 
          comment.id === commentId ? response.data : comment
        ));
        setEditingCommentId(null);
        setEditContent('');
        message.success('Đã cập nhật bình luận');
      } else {
        message.error(response.message);
      }
    } catch (error) {
      console.error('Lỗi khi cập nhật bình luận:', error);
      message.error('Không thể cập nhật bình luận');
    }
  };

  // Load more comments
  const loadMoreComments = () => {
    if (pagination.hasMore && !loading) {
      void fetchComments(pagination.current + 1, false);
    }
  };

  // Reset state when modal closes
  const handleModalClose = () => {
    setComments([]);
    setNewComment('');
    setEditingCommentId(null);
    setEditContent('');
    setPagination({ current: 0, total: 0, hasMore: true });
    onClose();
  };

  if (!post) return null;
  return (
    <Modal
      open={visible}
      onCancel={handleModalClose}
      footer={null}
      width={700}
      style={{ top: 10 }}
      bodyStyle={{ 
        padding: 0, 
        maxHeight: '90vh', 
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column'
      }}
      title={null}
      closeIcon={null}
    >
      <div style={{ 
        display: 'flex', 
        flexDirection: 'column',
        height: '90vh',
        maxHeight: '90vh'
      }}>
        {/* Header with close button */}
        <div style={{
          padding: '16px',
          borderBottom: '1px solid #f0f0f0',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <Title level={4} style={{ margin: 0 }}>
            Bình luận
          </Title>
          <Button type="text" onClick={handleModalClose}>
            ✕
          </Button>
        </div>        {/* Original Post */}
        <div style={{ 
          padding: '16px', 
          borderBottom: '1px solid #f0f0f0',
          maxHeight: '40vh',
          overflowY: 'auto',
          flexShrink: 0
        }}>
          <Card
            style={{
              borderRadius: '8px',
              border: '1px solid #f0f0f0'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'flex-start' }}>
              <Avatar
                src={post.user.profilePicture ? `http://localhost:8080/yapping${post.user.profilePicture}` : null}
                size={40}
                style={{ marginRight: 12 }}
              >
                {!post.user.profilePicture && post.user.fullName.charAt(0).toUpperCase()}
              </Avatar>

              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <div>
                    <Text strong style={{ color: '#8860D0' }}>
                      {post.user.fullName}
                    </Text>
                    <Text type="secondary" style={{ marginLeft: 8 }}>
                      @{post.user.username}
                    </Text>
                  </div>
                  <Text type="secondary">
                    {formatTimeAgo(post.createdAt)}
                  </Text>
                </div>

                <Paragraph style={{ margin: '8px 0', whiteSpace: 'pre-wrap' }}>
                  {post.content}
                </Paragraph>                {/* Media display */}
                {post.media && post.media.length > 0 && (
                  <div style={{
                    overflowX: 'auto',
                    margin: '8px 0',
                    paddingBottom: '8px',
                    maxHeight: '200px'
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
                            width: '100px',
                            height: '120px',
                            borderRadius: '8px',
                            overflow: 'hidden',
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
                  </div>                )}
              </div>
            </div>
          </Card>
        </div>        {/* Comments List */}
        <div style={{ 
          flex: 1, 
          minHeight: 0,
          overflowY: 'auto',
          padding: '0 16px'
        }}>
          {loading && (!comments || comments.length === 0) ? (
            <div style={{ textAlign: 'center', padding: '40px 0' }}>
              <Spin size="large" tip="Đang tải bình luận..." />
            </div>
          ) : (!comments || comments.length === 0) ? (
            <Empty 
              description="Chưa có bình luận nào"
              style={{ padding: '40px 0' }}
            />
          ) : (            <List
              dataSource={comments || []}
              renderItem={(comment) => (
                <List.Item style={{ padding: '12px 0', border: 'none' }}>
                  <div style={{ width: '100%' }}>
                    <div style={{ display: 'flex', alignItems: 'flex-start' }}>                      <Avatar
                        src={comment.userProfilePicture ? `http://localhost:8080/yapping${comment.userProfilePicture}` : null}
                        size={32}
                        style={{ marginRight: 12 }}
                      >
                        {!comment.userProfilePicture && comment.userFullName?.charAt(0).toUpperCase()}
                      </Avatar>

                      <div style={{ flex: 1 }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                          <div>                            <Text strong style={{ color: '#8860D0', fontSize: 14 }}>
                              {comment.userFullName || 'Unknown User'}
                            </Text>
                            <Text type="secondary" style={{ marginLeft: 8, fontSize: 12 }}>
                              @{comment.username || 'unknown'} • {formatTimeAgo(comment.createdAt)}
                            </Text>
                          </div>
                          
                          {currentUser && currentUser.id === comment.userId && (
                            <Dropdown
                              menu={{
                                items: [
                                  {
                                    key: 'edit',
                                    icon: <EditOutlined />,
                                    label: 'Chỉnh sửa',
                                    onClick: () => startEdit(comment)
                                  },                                  {
                                    key: 'delete',
                                    icon: <DeleteOutlined />,
                                    label: 'Xóa',
                                    danger: true,
                                    onClick: () => void handleDeleteComment(comment.id)
                                  }
                                ]
                              }}
                              trigger={['click']}
                            >
                              <Button type="text" icon={<MoreOutlined />} size="small" />
                            </Dropdown>
                          )}
                        </div>

                        {editingCommentId === comment.id ? (
                          <div style={{ margin: '8px 0' }}>
                            <TextArea
                              value={editContent}
                              onChange={(e) => setEditContent(e.target.value)}
                              rows={2}
                              style={{ marginBottom: 8 }}
                            />
                            <Space>                              <Button 
                                size="small" 
                                type="primary" 
                                onClick={() => void saveEdit(comment.id)}
                              >
                                Lưu
                              </Button>
                              <Button size="small" onClick={cancelEdit}>
                                Hủy
                              </Button>
                            </Space>
                          </div>
                        ) : (
                          <Paragraph style={{ 
                            margin: '8px 0',
                            fontSize: 14,
                            whiteSpace: 'pre-wrap'
                          }}>
                            {comment.content}
                          </Paragraph>
                        )}

                        <Space>                          <Button
                            type="text"
                            size="small"
                            icon={comment.isLiked ? <LikeFilled style={{ color: '#ff4d4f' }} /> : <LikeOutlined />}
                            onClick={() => void handleLikeComment(comment.id)}
                          >
                            {comment.likeCount || 0}
                          </Button>
                        </Space>
                      </div>
                    </div>
                  </div>
                </List.Item>
              )}
            />
          )}

          {pagination.hasMore && comments && comments.length > 0 && (
            <div style={{ textAlign: 'center', padding: '16px 0' }}>
              <Button 
                onClick={loadMoreComments} 
                loading={loading}
                type="link"
              >
                Xem thêm bình luận
              </Button>
            </div>
          )}
        </div>        {/* Comment Input */}
        <div style={{ 
          padding: '16px',
          borderTop: '1px solid #f0f0f0',
          background: '#fafafa',
          flexShrink: 0
        }}>
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
            {currentUser && (
              <Avatar
                src={currentUser.profilePicture ? `http://localhost:8080/yapping${currentUser.profilePicture}` : null}
                size={32}
              >
                {!currentUser.profilePicture && currentUser.fullName.charAt(0).toUpperCase()}
              </Avatar>
            )}
            
            <div style={{ flex: 1 }}>
              <TextArea
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Viết bình luận..."
                rows={2}
                style={{ marginBottom: 8 }}
                onPressEnter={(e) => {
                  if (e.ctrlKey || e.metaKey) {
                    void handleCreateComment();
                  }
                }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  Nhấn Ctrl + Enter để gửi
                </Text>                <Button
                  type="primary"
                  icon={<SendOutlined />}
                  onClick={() => void handleCreateComment()}
                  loading={commentLoading}
                  disabled={!newComment.trim()}
                  style={{ background: '#8860D0', borderColor: '#8860D0' }}
                >
                  Gửi
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default PostCommentsModal;
