// src/components/admin/post/PostStatistics.tsx
import { Card, Row, Col, Statistic, Progress, Spin } from 'antd';
import { useState, useEffect } from 'react';
import { 
  FileTextOutlined, 
  EyeOutlined, 
  EyeInvisibleOutlined,
  TeamOutlined,
  CalendarOutlined,
  BarChartOutlined,
  LinkOutlined
} from '@ant-design/icons';
import { getPostStatistics } from '../../../service/admin/postService';

interface PostStatsData {
  totalPosts: number;
  publicPosts: number;
  privatePosts: number;
  followersOnlyPosts: number;
  textPosts: number;
  resourcePosts: number;
  postsThisWeek: number;
  postsThisMonth: number;
}

interface PostStatisticsProps {
  onStatisticClick?: () => void;
}

const PostStatistics: React.FC<PostStatisticsProps> = ({ onStatisticClick }) => {
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState<PostStatsData | null>(null);

  useEffect(() => {
    void fetchStatistics();
  }, []);

  const fetchStatistics = async () => {
    setLoading(true);
    try {
      const response = await getPostStatistics();
      if (response.success && response.data) {
        setStats(response.data);
      }
    } catch (error) {
      console.error('Error fetching post statistics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '40px' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!stats) {
    return null;
  }

  const publicRate = stats.totalPosts > 0 
    ? (stats.publicPosts / stats.totalPosts * 100)
    : 0;

  return (
    <div style={{ marginBottom: '24px' }}>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card
            style={{ 
              background: 'linear-gradient(135deg, #722ed1, #9254de)',
              border: 'none',
              borderRadius: '12px',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
            hoverable
            onClick={onStatisticClick}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 4px 16px rgba(114, 46, 209, 0.3)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = '';
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Tổng bài đăng</span>}
              value={stats.totalPosts}
              prefix={<FileTextOutlined style={{ color: '#fff' }} />}
              valueStyle={{ color: '#fff', fontWeight: 'bold' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card
            style={{ 
              background: 'linear-gradient(135deg, #52c41a, #73d13d)',
              border: 'none',
              borderRadius: '12px',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
            hoverable
            onClick={onStatisticClick}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 4px 16px rgba(82, 196, 26, 0.3)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = '';
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Công khai</span>}
              value={stats.publicPosts}
              prefix={<EyeOutlined style={{ color: '#fff' }} />}
              valueStyle={{ color: '#fff', fontWeight: 'bold' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card
            style={{ 
              background: 'linear-gradient(135deg, #faad14, #ffd666)',
              border: 'none',
              borderRadius: '12px',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
            hoverable
            onClick={onStatisticClick}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 4px 16px rgba(250, 173, 20, 0.3)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = '';
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Bạn bè</span>}
              value={stats.followersOnlyPosts}
              prefix={<TeamOutlined style={{ color: '#fff' }} />}
              valueStyle={{ color: '#fff', fontWeight: 'bold' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card
            style={{ 
              background: 'linear-gradient(135deg, #8c8c8c, #bfbfbf)',
              border: 'none',
              borderRadius: '12px',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
            hoverable
            onClick={onStatisticClick}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 4px 16px rgba(140, 140, 140, 0.3)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = '';
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Riêng tư</span>}
              value={stats.privatePosts}
              prefix={<EyeInvisibleOutlined style={{ color: '#fff' }} />}
              valueStyle={{ color: '#fff', fontWeight: 'bold' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        <Col xs={24} md={8}>
          <Card
            title={
              <span style={{ color: '#722ed1', fontWeight: '600' }}>
                <BarChartOutlined style={{ marginRight: '8px' }} />
                Tỷ lệ công khai
              </span>
            }
            style={{ borderRadius: '12px', border: '1px solid rgba(114, 46, 209, 0.2)' }}
          >
            <Progress
              percent={Math.round(publicRate)}
              strokeColor={{
                '0%': '#722ed1',
                '100%': '#9254de',
              }}
              trailColor="rgba(114, 46, 209, 0.1)"
              strokeWidth={12}
              format={(percent) => `${percent}%`}
            />
            <div style={{ marginTop: '12px', color: '#666', fontSize: '13px' }}>
              {stats.publicPosts} / {stats.totalPosts} bài đăng công khai
            </div>
          </Card>
        </Col>

        <Col xs={24} md={8}>
          <Card
            title={
              <span style={{ color: '#722ed1', fontWeight: '600' }}>
                <CalendarOutlined style={{ marginRight: '8px' }} />
                Thống kê theo thời gian
              </span>
            }
            style={{ borderRadius: '12px', border: '1px solid rgba(114, 46, 209, 0.2)' }}
          >
            <Row gutter={16}>
              <Col span={12}>
                <Statistic
                  title="Tuần này"
                  value={stats.postsThisWeek}
                  valueStyle={{ color: '#722ed1', fontSize: '20px' }}
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="Tháng này"
                  value={stats.postsThisMonth}
                  valueStyle={{ color: '#9254de', fontSize: '20px' }}
                />
              </Col>
            </Row>
          </Card>
        </Col>

        <Col xs={24} md={8}>
          <Card
            title={
              <span style={{ color: '#722ed1', fontWeight: '600' }}>
                <LinkOutlined style={{ marginRight: '8px' }} />
                Loại bài đăng
              </span>
            }
            style={{ borderRadius: '12px', border: '1px solid rgba(114, 46, 209, 0.2)' }}
          >
            <Row gutter={16}>
              <Col span={12}>
                <Statistic
                  title="Văn bản"
                  value={stats.textPosts}
                  valueStyle={{ color: '#722ed1', fontSize: '20px' }}
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="Tài nguyên"
                  value={stats.resourcePosts}
                  valueStyle={{ color: '#9254de', fontSize: '20px' }}
                />
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default PostStatistics;
