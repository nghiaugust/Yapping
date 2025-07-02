// src/pages/admin/Reports.tsx
import { Table, Button, Tag, message, Tabs, Popconfirm, Select, Input } from 'antd';
import { useState, useEffect, useCallback } from 'react';
import { EyeOutlined, EditOutlined, DeleteOutlined, FileTextOutlined, MessageOutlined, SearchOutlined, ReloadOutlined, BarChartOutlined, UpOutlined, DownOutlined } from '@ant-design/icons';
import { getAllReports, deleteReport } from '../../service/admin/reportService';
import { Report } from '../../types/report';
import { useNavigate } from 'react-router-dom';
import moment from 'moment';
import ReportDetailModal from '../../components/admin/report/ReportDetailModal';
import ReportStatistics from '../../components/admin/report/ReportStatistics';
import '../../assets/styles/reports.css';

interface ApiErrorResponse {
  type?: string;
  title?: string;
  status: number;
  detail?: string;
  errorCode?: string;
}

const Reports: React.FC = () => {
  const [data, setData] = useState<Report[]>([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [activeTab, setActiveTab] = useState<'ALL' | 'POST' | 'COMMENT'>('ALL');
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedReportId, setSelectedReportId] = useState<number | null>(null);
  const [statusFilter, setStatusFilter] = useState<string | undefined>(undefined);
  const [searchText, setSearchText] = useState<string>('');
  const [loadingDetailId, setLoadingDetailId] = useState<number | null>(null);
  const [showStatistics, setShowStatistics] = useState(true);
  const navigate = useNavigate();

  // Lấy danh sách báo cáo
  const fetchReports = useCallback(async (): Promise<void> => {
    setLoading(true);
    try {
      const targetType = activeTab === 'ALL' ? undefined : activeTab;
      const status = statusFilter ? statusFilter as "PENDING" | "REVIEWING" | "RESOLVED_ACTION_TAKEN" | "RESOLVED_NO_ACTION" : undefined;
      const response = await getAllReports(currentPage - 1, 10, status, targetType);
      
      if (response.success && response.data) {
        let reports = response.data.content || [];
        
        // Lọc theo searchText nếu có
        if (searchText.trim()) {
          reports = reports.filter(report => 
            (report.reporterUsername ?? '').toLowerCase().includes(searchText.toLowerCase()) ||
            (report.description ?? '').toLowerCase().includes(searchText.toLowerCase()) ||
            report.reason.toLowerCase().includes(searchText.toLowerCase())
          );
        }
        
        setData(reports);
        setTotalElements(searchText.trim() ? reports.length : response.data.totalElements || 0);
      } else {
        setData([]);
        setTotalElements(0);
      }
    } catch (error: unknown) {
      console.error('Error fetching reports:', error);
      const errorResponse: ApiErrorResponse = (error as { response?: { data?: ApiErrorResponse } }).response?.data ?? { status: 0 };
      const errorMessage = errorResponse.detail ?? errorResponse.title ?? 'Lỗi không xác định';
      if (errorResponse.status === 401) {
        message.error('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!');
        void navigate('/login');
      } else {
        message.error(`Lấy danh sách báo cáo thất bại: ${errorMessage}`);
      }
      setData([]);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  }, [currentPage, activeTab, statusFilter, searchText, navigate]);

  useEffect(() => {
    void fetchReports();
  }, [fetchReports]);

  const handleTabChange = (key: string) => {
    setActiveTab(key as 'ALL' | 'POST' | 'COMMENT');
    setCurrentPage(1);
  };

  const handleViewDetail = (reportId: number) => {
    setLoadingDetailId(reportId);
    setSelectedReportId(reportId);
    setDetailModalVisible(true);
    // Reset loading sau một khoảng thời gian ngắn
    setTimeout(() => setLoadingDetailId(null), 500);
  };

  const handleCloseDetailModal = () => {
    setDetailModalVisible(false);
    setSelectedReportId(null);
    setLoadingDetailId(null);
  };

  const handleReportUpdated = () => {
    void fetchReports(); // Reload data after update
  };

  const handleDeleteReport = async (reportId: number) => {
    try {
      const response = await deleteReport(reportId);
      if (response.success) {
        message.success('Xóa báo cáo thành công');
        void fetchReports(); // Reload data
      } else {
        message.error('Xóa báo cáo thất bại');
      }
    } catch (error) {
      console.error('Error deleting report:', error);
      message.error('Lỗi khi xóa báo cáo');
    }
  };

  const getReasonLabel = (reason: string): string => {
    const reasonMap: Record<string, string> = {
      SPAM: 'Spam/Quảng cáo',
      HARASSMENT: 'Quấy rối',
      HATE_SPEECH: 'Ngôn từ thù địch',
      INAPPROPRIATE_CONTENT: 'Nội dung không phù hợp',
      INTELLECTUAL_PROPERTY: 'Vi phạm bản quyền',
      MISINFORMATION: 'Thông tin sai lệch',
      OTHER: 'Khác'
    };
    return reasonMap[reason] || reason;
  };

  const getStatusColor = (status: string): string => {
    const statusMap: Record<string, string> = {
      PENDING: 'orange',
      REVIEWING: 'blue',
      RESOLVED_ACTION_TAKEN: 'green',
      RESOLVED_NO_ACTION: 'gray'
    };
    return statusMap[status] || 'gray';
  };

  const getStatusLabel = (status: string): string => {
    const statusMap: Record<string, string> = {
      PENDING: 'Chờ xử lý',
      REVIEWING: 'Đang xem xét', 
      RESOLVED_ACTION_TAKEN: 'Đã xử lý',
      RESOLVED_NO_ACTION: 'Không vi phạm'
    };
    return statusMap[status] || status;
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
      dataIndex: 'targetType', 
      key: 'targetType', 
      width: 100,
      render: (targetType: string) => (
        <Tag 
          color={targetType === 'POST' ? '#ff7875' : '#ffa940'}
          icon={targetType === 'POST' ? <FileTextOutlined /> : <MessageOutlined />}
          style={{ fontWeight: '500' }}
        >
          {targetType === 'POST' ? 'Bài đăng' : 'Bình luận'}
        </Tag>
      )
    },
    { 
      title: 'Người báo cáo', 
      dataIndex: 'reporterUsername', 
      key: 'reporterUsername', 
      width: 140,
      render: (text: string) => (
        <span style={{ fontWeight: '500' }}>
          {String(text ?? 'Ẩn danh')}
        </span>
      )
    },
    { 
      title: 'Lý do', 
      dataIndex: 'reason', 
      key: 'reason', 
      width: 160,
      render: (reason: string) => (
        <span style={{ fontSize: '13px' }}>{getReasonLabel(reason)}</span>
      )
    },
    { 
      title: 'Mô tả', 
      dataIndex: 'description', 
      key: 'description', 
      width: 200,
      render: (text: string) => (
        <div style={{ 
          maxWidth: '180px', 
          overflow: 'hidden', 
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
          fontSize: '13px'
        }}>
          {text || 'Không có mô tả'}
        </div>
      )
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => (
        <Tag 
          color={getStatusColor(status)}
          style={{ fontSize: '11px', fontWeight: '500' }}
        >
          {getStatusLabel(status)}
        </Tag>
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
      render: (_: unknown, record: Report) => (
        <div style={{ display: 'flex', gap: '4px' }}>
          <Button
            type="primary"
            icon={<EyeOutlined />}
            size="small"
            className="reports-action-btn"
            style={{ background: '#ff7875', borderColor: '#ff7875' }}
            onClick={(e) => {
              e.stopPropagation();
              handleViewDetail(record.id);
            }}
          />
          <Button
            type="primary"
            icon={<EditOutlined />}
            size="small"
            className="reports-action-btn"
            style={{ background: '#ffa940', borderColor: '#ffa940' }}
            onClick={(e) => {
              e.stopPropagation();
              handleViewDetail(record.id); // Sử dụng cùng modal để edit
            }}
          />
          <Popconfirm
            title="Xóa báo cáo"
            description="Bạn có chắc chắn muốn xóa báo cáo này?"
            onConfirm={(e) => {
              e?.stopPropagation();
              void handleDeleteReport(record.id);
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
              className="reports-action-btn"
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
      key: 'POST',
      label: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <FileTextOutlined style={{ color: '#ff7875' }} />
          <span style={{ fontSize: '14px', fontWeight: '500' }}>Báo cáo bài đăng</span>
        </div>
      ),
    },
    {
      key: 'COMMENT',
      label: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <MessageOutlined style={{ color: '#ffa940' }} />
          <span style={{ fontSize: '14px', fontWeight: '500' }}>Báo cáo bình luận</span>
        </div>
      ),
    },
  ];

  return (
    <div className="reports-container">
      {/* Header Section */}
      <div className="reports-header">
        <div className="reports-header-content">
          <div>
            <h2>Quản Lý Báo Cáo</h2>
            <p>Xem xét và xử lý các báo cáo từ người dùng</p>
          </div>
          <div className="reports-header-controls">
            <Select
              placeholder="Lọc theo trạng thái"
              allowClear
              value={statusFilter}
              onChange={setStatusFilter}                options={[
                  { value: 'PENDING', label: 'Chờ xử lý' },
                  { value: 'REVIEWING', label: 'Đang xem xét' },
                  { value: 'RESOLVED_ACTION_TAKEN', label: 'Đã xử lý' },
                  { value: 'RESOLVED_NO_ACTION', label: 'Không vi phạm' },
                ]}
            />
            <Input.Search
              placeholder="Tìm kiếm..."
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onSearch={() => void fetchReports()}
              enterButton={<SearchOutlined />}
            />
            <Button
              type="primary"
              icon={<ReloadOutlined />}
              onClick={() => void fetchReports()}
              style={{ background: '#ff7875', borderColor: '#ff7875' }}
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
                borderColor: '#ff7875', 
                color: '#ff7875',
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
          <ReportStatistics onStatisticClick={() => {
            setShowStatistics(false);
            message.info('Đã ẩn phần thống kê để hiển thị bảng báo cáo rõ hơn');
          }} />
        </div>
      )}

      {/* Tabs Section */}
      <div className="reports-tabs">
        <Tabs
          activeKey={activeTab}
          onChange={handleTabChange}
          items={tabItems}
          style={{ padding: "0 24px" }}
          tabBarStyle={{ 
            margin: 0,
            borderBottom: "1px solid rgba(255, 120, 117, 0.1)"
          }}
        />
      </div>

      {/* Table Section */}
      <div className="reports-table">
        <div style={{ 
          marginBottom: '12px', 
          padding: '8px 12px',
          background: 'rgba(255, 120, 117, 0.05)',
          border: '1px solid rgba(255, 120, 117, 0.2)',
          borderRadius: '6px',
          fontSize: '13px',
          color: '#ff4d4f',
          display: 'flex',
          alignItems: 'center',
          gap: '6px'
        }}>
          <EyeOutlined style={{ fontSize: '12px' }} />
          <span>💡 <strong>Mẹo:</strong> Click vào bất kỳ dòng nào trong bảng để xem chi tiết báo cáo, hoặc click vào các thẻ thống kê để ẩn/hiện phần thống kê</span>
        </div>
        <Table
          columns={columns}
          dataSource={data}
          pagination={{ 
            pageSize: 10, 
            showSizeChanger: false,
            showTotal: (total, range) => `${range[0]}-${range[1]} của ${total} báo cáo`,
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
          scroll={{ x: 'max-content', y: showStatistics ? 'calc(100vh - 450px)' : 'calc(100vh - 300px)' }}
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
              row.style.backgroundColor = 'rgba(255, 120, 117, 0.08)';
              row.style.transform = 'translateY(-1px)';
              row.style.boxShadow = '0 2px 8px rgba(255, 120, 117, 0.15)';
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

      {/* Report Detail Modal */}
      <ReportDetailModal
        visible={detailModalVisible}
        onCancel={handleCloseDetailModal}
        reportId={selectedReportId}
        onReportUpdated={handleReportUpdated}
      />
    </div>
  );
};

export default Reports;
