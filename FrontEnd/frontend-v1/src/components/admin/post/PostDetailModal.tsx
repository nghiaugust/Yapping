// src/components/admin/post/PostDetailModal.tsx
import { Modal, Descriptions, Tag, Button, message, Space, Image } from 'antd';
import { useState, useEffect, useCallback } from 'react';
import { 
  FileTextOutlined, 
  UserOutlined, 
  ClockCircleOutlined, 
  EyeOutlined,
  EyeInvisibleOutlined,
  TeamOutlined,
  PlayCircleOutlined,
  PictureOutlined,
  HeartOutlined,
  MessageOutlined,
  RetweetOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons';
import { getPostById, deletePost, updatePostVisibility } from '../../../service/admin/postService';
import { Post } from '../../../types/post';
import { getProfilePictureUrl, getMediaUrl } from '../../../utils/constants';
import moment from 'moment';

interface PostDetailModalProps {
  visible: boolean;
  onCancel: () => void;
  postId: number | null;
  onPostUpdated: () => void;
}

const PostDetailModal: React.FC<PostDetailModalProps> = ({
  visible,
  onCancel,
  postId,
  onPostUpdated,
}) => {
  const [loading, setLoading] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [post, setPost] = useState<Post | null>(null);

  const fetchPostDetails = useCallback(async () => {
    if (!postId) return;
    
    setLoading(true);
    try {
      const response = await getPostById(postId);
      if (response.success && response.data) {
        setPost(response.data);
      } else {
        message.error('Không thể tải thông tin bài đăng');
      }
    } catch (error) {
      console.error('Error fetching post details:', error);
      message.error('Lỗi khi tải thông tin bài đăng');
    } finally {
      setLoading(false);
    }
  }, [postId]);

  useEffect(() => {
    if (visible && postId) {
      void fetchPostDetails();
    }
  }, [visible, postId, fetchPostDetails]);

  const handleUpdateVisibility = async (visibility: "PUBLIC" | "FOLLOWERS_ONLY" | "PRIVATE") => {
    if (!postId) return;

    setUpdating(true);
    try {
      const response = await updatePostVisibility(postId, visibility);
      if (response.success) {
        message.success('Cập nhật trạng thái hiển thị thành công');
        onPostUpdated();
        void fetchPostDetails(); // Refresh data
      } else {
        message.error('Cập nhật trạng thái thất bại');
      }
    } catch (error) {
      console.error('Error updating post visibility:', error);
      message.error('Lỗi khi cập nhật trạng thái');
    } finally {
      setUpdating(false);
    }
  };

  const handleDeletePost = async () => {
    if (!postId) return;

    setUpdating(true);
    try {
      const response = await deletePost(postId);
      if (response.success) {
        message.success('Xóa bài đăng thành công');
        onPostUpdated();
        onCancel();
      } else {
        message.error('Xóa bài đăng thất bại');
      }
    } catch (error) {
      console.error('Error deleting post:', error);
      message.error('Lỗi khi xóa bài đăng');
    } finally {
      setUpdating(false);
    }
  };

  const getVisibilityColor = (visibility: string): string => {
    const colorMap: Record<string, string> = {
      PUBLIC: 'green',
      FOLLOWERS_ONLY: 'orange',
      PRIVATE: 'red'
    };
    return colorMap[visibility] || 'gray';
  };

  const getVisibilityLabel = (visibility: string): string => {
    const labelMap: Record<string, string> = {
      PUBLIC: 'Công khai',
      FOLLOWERS_ONLY: 'Chỉ bạn bè',
      PRIVATE: 'Riêng tư'
    };
    return labelMap[visibility] || visibility;
  };

  const getVisibilityIcon = (visibility: string) => {
    const iconMap: Record<string, React.ReactNode> = {
      PUBLIC: <EyeOutlined />,
      FOLLOWERS_ONLY: <TeamOutlined />,
      PRIVATE: <EyeInvisibleOutlined />
    };
    return iconMap[visibility] ?? <QuestionCircleOutlined />;
  };

  const getPostTypeLabel = (postType: string | null): string => {
    if (!postType) return 'Không xác định';
    const typeMap: Record<string, string> = {
      TEXT: 'Văn bản',
      RESOURCE: 'Tài nguyên'
    };
    return typeMap[postType] || postType;
  };

  const renderMedia = () => {
    if (!post?.media || post.media.length === 0) {
      return (
        <div style={{ 
          textAlign: 'center', 
          color: '#999', 
          padding: '24px',
          fontStyle: 'italic'
        }}>
          <PictureOutlined style={{ fontSize: '24px', marginBottom: '8px', display: 'block' }} />
          Không có media đính kèm
        </div>
      );
    }

    return (
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', 
        gap: '12px',
        marginTop: '12px'
      }}>
        {post.media.map((media, index) => (
          <div key={media.id || index} style={{ 
            border: '1px solid #e8e8e8', 
            borderRadius: '8px', 
            overflow: 'hidden',
            background: '#fafafa'
          }}>
            {media.mediaType === 'IMAGE' ? (
              <Image
                src={getMediaUrl(media.mediaUrl)}
                alt={`Media ${index + 1}`}
                style={{ width: '100%', height: '150px', objectFit: 'cover' }}
                placeholder={
                  <div style={{ 
                    height: '150px', 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    color: '#999'
                  }}>
                    <PictureOutlined style={{ fontSize: '24px' }} />
                  </div>
                }
              />
            ) : (
              <div style={{ 
                height: '150px', 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                flexDirection: 'column',
                color: '#722ed1',
                background: 'rgba(114, 46, 209, 0.1)'
              }}>
                <PlayCircleOutlined style={{ fontSize: '32px', marginBottom: '8px' }} />
                <span style={{ fontSize: '12px' }}>Video</span>
                <a 
                  href={getMediaUrl(media.mediaUrl)} 
                  target="_blank" 
                  rel="noopener noreferrer"
                  style={{ fontSize: '11px', marginTop: '4px' }}
                >
                  Xem video
                </a>
              </div>
            )}
            <div style={{ 
              padding: '8px', 
              fontSize: '11px', 
              color: '#666',
              borderTop: '1px solid #e8e8e8'
            }}>
              <div>Loại: {media.mediaType}</div>
              <div>Thứ tự: {media.sortOrder}</div>
            </div>
          </div>
        ))}
      </div>
    );
  };

  const renderActionButtons = () => {
    if (!post) return null;

    return (
      <Space>
        <Button
          type="primary"
          style={{ background: '#52c41a', borderColor: '#52c41a' }}
          loading={updating}
          onClick={() => void handleUpdateVisibility('PUBLIC')}
          disabled={post.visibility === 'PUBLIC'}
        >
          Công khai
        </Button>
        <Button
          type="primary"
          style={{ background: '#faad14', borderColor: '#faad14' }}
          loading={updating}
          onClick={() => void handleUpdateVisibility('FOLLOWERS_ONLY')}
          disabled={post.visibility === 'FOLLOWERS_ONLY'}
        >
          Chỉ bạn bè
        </Button>
        <Button
          type="primary"
          style={{ background: '#8c8c8c', borderColor: '#8c8c8c' }}
          loading={updating}
          onClick={() => void handleUpdateVisibility('PRIVATE')}
          disabled={post.visibility === 'PRIVATE'}
        >
          Riêng tư
        </Button>
        <Button
          type="primary"
          danger
          loading={updating}
          onClick={() => void handleDeletePost()}
        >
          Xóa bài đăng
        </Button>
        <Button onClick={onCancel}>
          Đóng
        </Button>
      </Space>
    );
  };

  return (
    <Modal
      title={
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <div style={{
            width: '32px',
            height: '32px',
            borderRadius: '8px',
            background: 'linear-gradient(135deg, #722ed1, #9254de)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff'
          }}>
            <FileTextOutlined />
          </div>
          <span style={{ fontSize: '16px', fontWeight: '600' }}>
            Chi tiết bài đăng #{postId}
          </span>
        </div>
      }
      visible={visible}
      onCancel={onCancel}
      footer={renderActionButtons()}
      width={900}
      loading={loading}
      style={{ top: 20 }}
      className="posts-detail-modal"
    >
      {post && (
        <div>
          <Descriptions
            bordered
            column={2}
            size="small"
            style={{ marginBottom: '16px' }}
          >
            <Descriptions.Item 
              label={
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <UserOutlined />
                  Tác giả
                </span>
              }
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                {post.user.profilePicture && (
                  <Image
                    src={getProfilePictureUrl(post.user.profilePicture)}
                    alt="Avatar"
                    width={24}
                    height={24}
                    style={{ borderRadius: '50%' }}
                  />
                )}
                <div>
                  <div style={{ fontWeight: '500' }}>{post.user.fullName}</div>
                  <div style={{ fontSize: '12px', color: '#666' }}>@{post.user.username}</div>
                </div>
              </div>
            </Descriptions.Item>

            <Descriptions.Item 
              label={
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <ClockCircleOutlined />
                  Trạng thái hiển thị
                </span>
              }
            >
              <Tag 
                color={getVisibilityColor(post.visibility)}
                icon={getVisibilityIcon(post.visibility)}
                style={{ fontSize: '12px', fontWeight: '500' }}
              >
                {getVisibilityLabel(post.visibility)}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Loại bài đăng">
              <Tag color="purple">
                {getPostTypeLabel(post.post_type)}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Thời gian tạo">
              {moment(post.createdAt).format("DD/MM/YYYY HH:mm:ss")}
            </Descriptions.Item>

            <Descriptions.Item label="Tương tác" span={2}>
              <Space size="middle">
                <span>
                  <HeartOutlined style={{ color: '#ff4d4f', marginRight: '4px' }} />
                  {post.likeCount ?? 0} lượt thích
                </span>
                <span>
                  <MessageOutlined style={{ color: '#1890ff', marginRight: '4px' }} />
                  {post.commentCount ?? 0} bình luận
                </span>
                <span>
                  <RetweetOutlined style={{ color: '#52c41a', marginRight: '4px' }} />
                  {post.repostCount ?? 0} chia sẻ lại
                </span>
              </Space>
            </Descriptions.Item>

            <Descriptions.Item label="Nội dung bài đăng" span={2}>
              <div style={{ 
                background: '#f5f5f5', 
                padding: '12px', 
                borderRadius: '6px',
                minHeight: '80px',
                whiteSpace: 'pre-wrap',
                lineHeight: '1.5'
              }}>
                {post.content || 'Không có nội dung văn bản'}
              </div>
            </Descriptions.Item>
          </Descriptions>

          {/* Media Section */}
          <div style={{
            background: '#fafafa',
            border: '1px solid #d9d9d9',
            borderRadius: '8px',
            padding: '16px',
            marginTop: '16px'
          }}>
            <h4 style={{
              margin: '0 0 12px 0',
              color: '#722ed1',
              display: 'flex',
              alignItems: 'center',
              gap: '8px'
            }}>
              <PictureOutlined />
              Media đính kèm ({post.media?.length ?? 0})
            </h4>
            {renderMedia()}
          </div>
        </div>
      )}
    </Modal>
  );
};

export default PostDetailModal;
