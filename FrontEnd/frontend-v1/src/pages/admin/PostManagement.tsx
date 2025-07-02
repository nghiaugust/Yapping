// src/pages/admin/PostManagement.tsx
import { Table, Button, Tag, message, Tabs, Popconfirm, Select, Input } from 'antd';
import { useState, useEffect, useCallback } from 'react';
import { 
  EyeOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  FileTextOutlined, 
  LinkOutlined, 
  SearchOutlined, 
  ReloadOutlined, 
  BarChartOutlined, 
  UpOutlined, 
  DownOutlined,
  EyeInvisibleOutlined,
  TeamOutlined,
  HeartOutlined,
  MessageOutlined,
  RetweetOutlined,
  PictureOutlined
} from '@ant-design/icons';
import { getAllPosts, deletePost } from '../../service/admin/postService';
import { Post, PostUser } from '../../types/post';
import { useNavigate } from 'react-router-dom';
import moment from 'moment';
import PostDetailModal from '../../components/admin/post/PostDetailModal';
import PostStatistics from '../../components/admin/post/PostStatistics';
import { getProfilePictureUrl } from '../../utils/constants';
import '../../assets/styles/posts.css';

interface ApiErrorResponse {
  type?: string;
  title?: string;
  status: number;
  detail?: string;
  errorCode?: string;
}

const PostManagement: React.FC = () => {
  const [data, setData] = useState<Post[]>([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [activeTab, setActiveTab] = useState<'ALL' | 'TEXT' | 'RESOURCE'>('ALL');
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedPostId, setSelectedPostId] = useState<number | null>(null);
  const [visibilityFilter, setVisibilityFilter] = useState<string | undefined>(undefined);
  const [searchText, setSearchText] = useState<string>('');
  const [showStatistics, setShowStatistics] = useState(true);
  const navigate = useNavigate();

  // Lấy danh sách bài đăng
  const fetchPosts = useCallback(async (): Promise<void> => {
    setLoading(true);
    try {
      const postType = activeTab === 'ALL' ? undefined : activeTab;
      const visibility = visibilityFilter ? visibilityFilter as "PUBLIC" | "FOLLOWERS_ONLY" | "PRIVATE" : undefined;
      const response = await getAllPosts(currentPage - 1, 10, visibility, postType);
      
      if (response.success && response.data) {
        let posts = response.data.content || [];
        
        // Lọc theo searchText nếu có
        if (searchText.trim()) {
          posts = posts.filter(post => 
            (post.user.username ?? '').toLowerCase().includes(searchText.toLowerCase()) ||
            (post.user.fullName ?? '').toLowerCase().includes(searchText.toLowerCase()) ||
            (post.content ?? '').toLowerCase().includes(searchText.toLowerCase())
          );
        }
        
        setData(posts);
        setTotalElements(searchText.trim() ? posts.length : response.data.totalElements || 0);
      } else {
        setData([]);
        setTotalElements(0);
      }
    } catch (error: unknown) {
      console.error('Error fetching posts:', error);
      const errorResponse: ApiErrorResponse = (error as { response?: { data?: ApiErrorResponse } }).response?.data ?? { status: 0 };
      const errorMessage = errorResponse.detail ?? errorResponse.title ?? 'Lỗi không xác định';
      if (errorResponse.status === 401) {
        message.error('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!');
        void navigate('/login');
      } else {
        message.error(`Lấy danh sách bài đăng thất bại: ${errorMessage}`);
      }
      setData([]);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  }, [currentPage, activeTab, visibilityFilter, searchText, navigate]);

  useEffect(() => {
    void fetchPosts();
  }, [fetchPosts]);

  const handleTabChange = (key: string) => {
    setActiveTab(key as 'ALL' | 'TEXT' | 'RESOURCE');
    setCurrentPage(1);
  };

  const handleViewDetail = (postId: number) => {
    setSelectedPostId(postId);
    setDetailModalVisible(true);
  };

  const handleCloseDetailModal = () => {
    setDetailModalVisible(false);
    setSelectedPostId(null);
  };

  const handlePostUpdated = () => {
    void fetchPosts(); // Reload data after update
  };

  const handleDeletePost = async (postId: number) => {
    try {
      const response = await deletePost(postId);
      if (response.success) {
        message.success('Xóa bài đăng thành công');
        void fetchPosts(); // Reload data
      } else {
        message.error('Xóa bài đăng thất bại');
      }
    } catch (error) {
      console.error('Error deleting post:', error);
      message.error('Lỗi khi xóa bài đăng');
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
    return iconMap[visibility];
  };

  const getPostTypeLabel = (postType: string | null): string => {
    if (!postType) return 'Không xác định';
    const typeMap: Record<string, string> = {
      TEXT: 'Văn bản',
      RESOURCE: 'Tài nguyên'
    };
    return typeMap[postType] || postType;
  };

  const columns = [
    { 
      title: 'STT', 
      key: 'stt',
      width: 60,
      align: 'center' as const,
      render: (_: unknown, __: unknown, index: number) => {
        return (currentPage - 1) * 10 + index + 1;
      }
    },
    { 
      title: 'Loại', 
      dataIndex: 'post_type', 
      key: 'post_type', 
      width: 100,
      render: (postType: string | null) => (
        <Tag 
          color={postType === 'TEXT' ? '#722ed1' : '#9254de'}
          icon={postType === 'TEXT' ? <FileTextOutlined /> : <LinkOutlined />}
          style={{ fontWeight: '500' }}
        >
          {getPostTypeLabel(postType)}
        </Tag>
      )
    },
    { 
      title: 'Tác giả', 
      dataIndex: 'user', 
      key: 'user', 
      width: 150,
      render: (user: PostUser) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          {user.profilePicture && (
            <img 
              src={getProfilePictureUrl(user.profilePicture)} 
              alt="Avatar" 
              style={{ width: '24px', height: '24px', borderRadius: '50%' }}
            />
          )}
          <div>
            <div style={{ fontWeight: '500', fontSize: '13px' }}>{user.fullName}</div>
            <div style={{ fontSize: '11px', color: '#666' }}>@{user.username}</div>
          </div>
        </div>
      )
    },
    { 
      title: 'Nội dung', 
      dataIndex: 'content', 
      key: 'content', 
      width: 250,
      render: (text: string) => (
        <div style={{ 
          maxWidth: '230px', 
          overflow: 'hidden', 
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
          fontSize: '13px',
          lineHeight: '1.4'
        }}>
          {text || 'Không có nội dung văn bản'}
        </div>
      )
    },
    {
      title: 'Hiển thị',
      dataIndex: 'visibility',
      key: 'visibility',
      width: 110,
      render: (visibility: string) => (
        <Tag 
          color={getVisibilityColor(visibility)}
          icon={getVisibilityIcon(visibility)}
          style={{ fontSize: '11px', fontWeight: '500' }}
        >
          {getVisibilityLabel(visibility)}
        </Tag>
      ),
    },
    {
      title: 'Tương tác',
      key: 'interaction',
      width: 140,
      render: (_: unknown, record: Post) => (
        <div style={{ fontSize: '11px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '2px' }}>
            <span style={{ color: '#ff4d4f' }}>
              <HeartOutlined /> {record.likeCount ?? 0}
            </span>
            <span style={{ color: '#1890ff' }}>
              <MessageOutlined /> {record.commentCount ?? 0}
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ color: '#52c41a' }}>
              <RetweetOutlined /> {record.repostCount ?? 0}
            </span>
            {record.media && record.media.length > 0 && (
              <span style={{ color: '#722ed1' }}>
                <PictureOutlined /> {record.media.length}
              </span>
            )}
          </div>
        </div>
      ),
    },
    {
      title: 'Thời gian',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 140,
      render: (createdAt: string) => (
        <span style={{ fontSize: '13px' }}>
          {moment(createdAt).format("DD/MM/YYYY HH:mm")}
        </span>
      ),
    },
    {
      title: 'Hành động',
      key: 'action',
      width: 120,
      render: (_: unknown, record: Post) => (
        <div style={{ display: 'flex', gap: '4px' }}>
          <Button
            type="primary"
            icon={<EyeOutlined />}
            size="small"
            className="posts-action-btn"
            style={{ background: '#722ed1', borderColor: '#722ed1' }}
            onClick={(e) => {
              e.stopPropagation();
              handleViewDetail(record.id);
            }}
          />
          <Button
            type="primary"
            icon={<EditOutlined />}
            size="small"
            className="posts-action-btn"
            style={{ background: '#9254de', borderColor: '#9254de' }}
            onClick={(e) => {
              e.stopPropagation();
              handleViewDetail(record.id); // Sử dụng cùng modal để edit
            }}
          />
          <Popconfirm
            title="Xóa bài đăng"
            description="Bạn có chắc chắn muốn xóa bài đăng này?"
            onConfirm={(e) => {
              e?.stopPropagation();
              void handleDeletePost(record.id);
            }}
            okText="Có"
            cancelText="Không"
            okType="danger"
          >
            <Button
              type="primary"
              danger
              icon={<DeleteOutlined />}
              size="small"
              className="posts-action-btn"
              onClick={(e) => e.stopPropagation()}
            />
          </Popconfirm>
        </div>
      ),
    },
  ];

  const tabItems = [
    {
      key: 'ALL',
      label: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '14px', fontWeight: '500' }}>Tất cả</span>
        </div>
      ),
    },
    {
      key: 'TEXT',
      label: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <FileTextOutlined style={{ color: '#722ed1' }} />
          <span style={{ fontSize: '14px', fontWeight: '500' }}>Văn bản</span>
        </div>
      ),
    },
    {
      key: 'RESOURCE',
      label: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <LinkOutlined style={{ color: '#9254de' }} />
          <span style={{ fontSize: '14px', fontWeight: '500' }}>Tài nguyên</span>
        </div>
      ),
    },
  ];

  return (
    <div className="posts-container">
      {/* Header Section */}
      <div className="posts-header">
        <div className="posts-header-content">
          <div>
            <h2>Quản Lý Bài Đăng</h2>
            <p>Quản lý và kiểm soát nội dung bài đăng</p>
          </div>
          <div className="posts-header-controls">
            <Select
              placeholder="Lọc theo hiển thị"
              allowClear
              value={visibilityFilter}
              onChange={setVisibilityFilter}
              options={[
                { value: 'PUBLIC', label: 'Công khai' },
                { value: 'FOLLOWERS_ONLY', label: 'Chỉ bạn bè' },
                { value: 'PRIVATE', label: 'Riêng tư' },
              ]}
            />
            <Input.Search
              placeholder="Tìm kiếm..."
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onSearch={() => void fetchPosts()}
              enterButton={<SearchOutlined />}
            />
            <Button
              type="primary"
              icon={<ReloadOutlined />}
              onClick={() => void fetchPosts()}
              style={{ background: '#722ed1', borderColor: '#722ed1' }}
            >
              Làm mới
            </Button>
            <Button
              type="default"
              icon={showStatistics ? <UpOutlined /> : <DownOutlined />}
              onClick={() => {
                setShowStatistics(!showStatistics);
                message.info(showStatistics ? 'Đã ẩn phần thống kê' : 'Đã hiển thị phần thống kê');
              }}
              style={{ 
                borderColor: '#722ed1', 
                color: '#722ed1',
                display: 'flex',
                alignItems: 'center',
                gap: '4px'
              }}
            >
              <BarChartOutlined />
              {showStatistics ? 'Ẩn thống kê' : 'Hiện thống kê'}
            </Button>
          </div>
        </div>
      </div>

      {/* Statistics Section */}
      {showStatistics && (
        <div style={{ 
          animation: showStatistics ? 'slideDown 0.3s ease-out' : 'slideUp 0.3s ease-out',
          marginBottom: '24px'
        }}>
          <PostStatistics onStatisticClick={() => {
            setShowStatistics(false);
            message.info('Đã ẩn phần thống kê để hiển thị bảng bài đăng rõ hơn');
          }} />
        </div>
      )}

      {/* Tabs Section */}
      <div className="posts-tabs">
        <Tabs
          activeKey={activeTab}
          onChange={handleTabChange}
          items={tabItems}
          style={{ padding: "0 24px" }}
          tabBarStyle={{ 
            margin: 0,
            borderBottom: "1px solid rgba(114, 46, 209, 0.1)"
          }}
        />
      </div>

      {/* Table Section */}
      <div className="posts-table">
        <div style={{ 
          marginBottom: '12px', 
          padding: '8px 12px',
          background: 'rgba(114, 46, 209, 0.05)',
          border: '1px solid rgba(114, 46, 209, 0.2)',
          borderRadius: '6px',
          fontSize: '13px',
          color: '#722ed1',
          display: 'flex',
          alignItems: 'center',
          gap: '6px'
        }}>
          <EyeOutlined style={{ fontSize: '12px' }} />
          <span>💡 <strong>Mẹo:</strong> Click vào bất kỳ dòng nào trong bảng để xem chi tiết bài đăng, hoặc click vào các thẻ thống kê để ẩn/hiện phần thống kê</span>
        </div>
        <Table
          columns={columns}
          dataSource={data}
          pagination={{ 
            pageSize: 10, 
            showSizeChanger: false,
            showTotal: (total, range) => `${range[0]}-${range[1]} của ${total} bài đăng`,
            style: { marginTop: "16px" },
            current: currentPage,
            total: totalElements,
            onChange: (page) => setCurrentPage(page)
          }}
          loading={loading}
          rowKey={(record) => {
            try {
              return record?.id ? String(record.id) : Math.random().toString();
            } catch (error) {
              console.error('Error generating rowKey:', error);
              return Math.random().toString();
            }
          }}
          scroll={{ x: 'max-content', y: showStatistics ? 'calc(100vh - 400px)' : 'calc(100vh - 250px)' }}
          size="large"
          locale={{
            emptyText: 'Không có dữ liệu',
            filterTitle: 'Bộ lọc',
            filterConfirm: 'OK',
            filterReset: 'Đặt lại',
            selectAll: 'Chọn tất cả',
            selectInvert: 'Chọn ngược lại',
          }}
          className="admin-table"
          onRow={(record) => ({
            onClick: () => handleViewDetail(record.id),
            style: { 
              cursor: 'pointer',
              position: 'relative'
            },
            onMouseEnter: (e) => {
              const row = e.currentTarget as HTMLTableRowElement;
              row.style.backgroundColor = 'rgba(114, 46, 209, 0.08)';
              row.style.transform = 'translateY(-1px)';
              row.style.boxShadow = '0 2px 8px rgba(114, 46, 209, 0.15)';
              row.style.transition = 'all 0.3s ease';
            },
            onMouseLeave: (e) => {
              const row = e.currentTarget as HTMLTableRowElement;
              row.style.backgroundColor = '';
              row.style.transform = '';
              row.style.boxShadow = '';
            },
          })}
        />
      </div>

      {/* Post Detail Modal */}
      <PostDetailModal
        visible={detailModalVisible}
        onCancel={handleCloseDetailModal}
        postId={selectedPostId}
        onPostUpdated={handlePostUpdated}
      />
    </div>
  );
};

export default PostManagement;