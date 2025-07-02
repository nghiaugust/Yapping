// src/components/admin/report/ReportStatistics.tsx
import { Card, Row, Col, Statistic, Progress, Spin } from 'antd';
import { useState, useEffect } from 'react';
import { 
  FileTextOutlined, 
  ExclamationCircleOutlined, 
  CheckCircleOutlined, 
  CloseCircleOutlined,
  CalendarOutlined,
  BarChartOutlined
} from '@ant-design/icons';
import { getReportStatistics } from '../../../service/admin/reportService';

interface ReportStatsData {
  totalReports: number;
  pendingReports: number;
  reviewedReports: number;
  resolvedReports: number;
  dismissedReports: number;
  reportsThisWeek: number;
  reportsThisMonth: number;
}

interface ReportStatisticsProps {
  onStatisticClick?: () => void;
}

const ReportStatistics: React.FC<ReportStatisticsProps> = ({ onStatisticClick }) => {
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState<ReportStatsData | null>(null);

  useEffect(() => {
    void fetchStatistics();
  }, []);

  const fetchStatistics = async () => {
    setLoading(true);
    try {
      const response = await getReportStatistics();
      if (response.success && response.data) {
        setStats(response.data);
      }
    } catch (error) {
      console.error('Error fetching report statistics:', error);
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

  const processingRate = stats.totalReports > 0 
    ? ((stats.resolvedReports + stats.dismissedReports) / stats.totalReports * 100)
    : 0;

  return (
    <div style={{ marginBottom: '24px' }}>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card
            style={{ 
              background: 'linear-gradient(135deg, #ff7875, #ffa940)',
              border: 'none',
              borderRadius: '12px',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
            hoverable
            onClick={onStatisticClick}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 4px 16px rgba(255, 120, 117, 0.3)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = '';
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Tổng báo cáo</span>}
              value={stats.totalReports}
              prefix={<FileTextOutlined style={{ color: '#fff' }} />}
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
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Chờ xử lý</span>}
              value={stats.pendingReports}
              prefix={<ExclamationCircleOutlined style={{ color: '#fff' }} />}
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
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Đã xử lý</span>}
              value={stats.resolvedReports}
              prefix={<CheckCircleOutlined style={{ color: '#fff' }} />}
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
              title={<span style={{ color: 'rgba(255, 255, 255, 0.9)' }}>Từ chối</span>}
              value={stats.dismissedReports}
              prefix={<CloseCircleOutlined style={{ color: '#fff' }} />}
              valueStyle={{ color: '#fff', fontWeight: 'bold' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        <Col xs={24} md={12}>
          <Card
            title={
              <span style={{ color: '#ff7875', fontWeight: '600' }}>
                <BarChartOutlined style={{ marginRight: '8px' }} />
                Tỷ lệ xử lý
              </span>
            }
            style={{ borderRadius: '12px', border: '1px solid rgba(255, 120, 117, 0.2)' }}
          >
            <Progress
              percent={Math.round(processingRate)}
              strokeColor={{
                '0%': '#ff7875',
                '100%': '#ffa940',
              }}
              trailColor="rgba(255, 120, 117, 0.1)"
              strokeWidth={12}
              format={(percent) => `${percent}%`}
            />
            <div style={{ marginTop: '12px', color: '#666', fontSize: '13px' }}>
              Đã xử lý {stats.resolvedReports + stats.dismissedReports} / {stats.totalReports} báo cáo
            </div>
          </Card>
        </Col>

        <Col xs={24} md={12}>
          <Card
            title={
              <span style={{ color: '#ff7875', fontWeight: '600' }}>
                <CalendarOutlined style={{ marginRight: '8px' }} />
                Thống kê theo thời gian
              </span>
            }
            style={{ borderRadius: '12px', border: '1px solid rgba(255, 120, 117, 0.2)' }}
          >
            <Row gutter={16}>
              <Col span={12}>
                <Statistic
                  title="Tuần này"
                  value={stats.reportsThisWeek}
                  valueStyle={{ color: '#ff7875', fontSize: '20px' }}
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="Tháng này"
                  value={stats.reportsThisMonth}
                  valueStyle={{ color: '#ffa940', fontSize: '20px' }}
                />
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ReportStatistics;
