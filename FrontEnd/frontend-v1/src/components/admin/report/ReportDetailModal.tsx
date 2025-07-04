// src/components/admin/report/ReportDetailModal.tsx
import { Modal, Descriptions, Tag, Button, message, Space } from 'antd';
import { useState, useEffect, useCallback } from 'react';
import { FileTextOutlined, MessageOutlined, UserOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { getReportById, updateReportStatus } from '../../../service/admin/reportService';
import { ReportWithDetails, UpdateReportRequest } from '../../../types/report';
import moment from 'moment';
import ReportActionModal from './ReportActionModal';
import { Fragment } from 'react';

interface ReportDetailModalProps {
  visible: boolean;
  onCancel: () => void;
  reportId: number | null;
  onReportUpdated: () => void;
}

const ReportDetailModal: React.FC<ReportDetailModalProps> = ({
  visible,
  onCancel,
  reportId,
  onReportUpdated,
}) => {
  const [loading, setLoading] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [report, setReport] = useState<ReportWithDetails | null>(null);
  const [actionModalVisible, setActionModalVisible] = useState(false);

  const fetchReportDetails = useCallback(async () => {
    if (!reportId) return;
    
    setLoading(true);
    try {
      const response = await getReportById(reportId);
      if (response.success && response.data) {
        setReport(response.data);
      } else {
        message.error('Không thể tải thông tin báo cáo');
      }
    } catch (error) {
      console.error('Error fetching report details:', error);
      message.error('Lỗi khi tải thông tin báo cáo');
    } finally {
      setLoading(false);
    }
  }, [reportId]);

  useEffect(() => {
    if (visible && reportId) {
      void fetchReportDetails();
    }
  }, [visible, reportId, fetchReportDetails]);

  const handleUpdateStatus = async (status: string, adminNotes?: string) => {
    if (!reportId) return;

    setUpdating(true);
    try {
      const updateData: UpdateReportRequest = {
        status: status as "PENDING" | "REVIEWING" | "RESOLVED_ACTION_TAKEN" | "RESOLVED_NO_ACTION",
        adminNotes
      };

      const response = await updateReportStatus(reportId, updateData);
      if (response.success) {
        message.success('Cập nhật trạng thái báo cáo thành công');
        onReportUpdated();
        onCancel();
      } else {
        message.error('Cập nhật trạng thái thất bại');
      }
    } catch (error) {
      console.error('Error updating report status:', error);
      message.error('Lỗi khi cập nhật trạng thái');
    } finally {
      setUpdating(false);
    }
  };

  const handleOpenActionModal = () => {
    setActionModalVisible(true);
  };

  const handleCloseActionModal = () => {
    setActionModalVisible(false);
  };

  const handleActionCompleted = () => {
    setActionModalVisible(false); // Đóng action modal
    onReportUpdated();
    onCancel();
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

  const renderActionButtons = () => {
    if (!report) return null;

    const currentStatus = report.status;
    const isResolved = currentStatus === 'RESOLVED_ACTION_TAKEN' || currentStatus === 'RESOLVED_NO_ACTION';

    return (
      <Space>
        {!isResolved && (
          <>
            <Button
              type="primary"
              style={{ background: '#52c41a', borderColor: '#52c41a' }}
              loading={updating}
              onClick={handleOpenActionModal}
            >
              Chấp nhận & Xử lý
            </Button>
            <Button
              type="default"
              loading={updating}
              onClick={() => void handleUpdateStatus('RESOLVED_NO_ACTION', 'Báo cáo không vi phạm quy định')}
            >
              Từ chối
            </Button>
            {currentStatus === 'PENDING' && (
              <Button
                type="primary"
                style={{ background: '#1890ff', borderColor: '#1890ff' }}
                loading={updating}
                onClick={() => void handleUpdateStatus('REVIEWING', 'Đang xem xét báo cáo')}
              >
                Đang xem xét
              </Button>
            )}
          </>
        )}
        <Button onClick={onCancel}>
          Đóng
        </Button>
      </Space>
    );
  };

  return (
    <Fragment>
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{
              width: '32px',
              height: '32px',
              borderRadius: '8px',
              background: 'linear-gradient(135deg, #ff7875, #ffa940)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff'
            }}>
              <FileTextOutlined />
            </div>
            <span style={{ fontSize: '16px', fontWeight: '600' }}>
              Chi tiết báo cáo #{reportId}
            </span>
          </div>
        }
        open={visible}
        onCancel={onCancel}
        footer={renderActionButtons()}
        width={800}
        loading={loading}
        style={{ top: 20 }}
        className="reports-detail-modal"
      >
      {report && (
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
                  <FileTextOutlined />
                  Loại đối tượng
                </span>
              }
            >
              <Tag 
                color={report.targetType === 'POST' ? '#ff7875' : '#ffa940'}
                icon={report.targetType === 'POST' ? <FileTextOutlined /> : <MessageOutlined />}
                style={{ fontWeight: '500' }}
              >
                {report.targetType === 'POST' ? 'Bài đăng' : 'Bình luận'}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item 
              label={
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <ClockCircleOutlined />
                  Trạng thái
                </span>
              }
            >
              <Tag 
                color={getStatusColor(report.status)}
                style={{ fontSize: '12px', fontWeight: '500' }}
              >
                {getStatusLabel(report.status)}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item 
              label={
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <UserOutlined />
                  Người báo cáo
                </span>
              }
            >
              {report.reporterUsername ?? 'Ẩn danh'}
            </Descriptions.Item>

            <Descriptions.Item label="Thời gian báo cáo">
              {moment(report.createdAt).format("DD/MM/YYYY HH:mm:ss")}
            </Descriptions.Item>

            {report.targetAuthorUsername && (
              <Descriptions.Item 
                label={
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <UserOutlined />
                    Tác giả nội dung
                  </span>
                }
              >
                {report.targetAuthorUsername}
              </Descriptions.Item>
            )}

            <Descriptions.Item label="Lý do báo cáo" span={report.targetAuthorUsername ? 2 : 2}>
              <Tag color="red" style={{ fontSize: '13px' }}>
                {getReasonLabel(report.reason)}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Mô tả chi tiết" span={2}>
              <div style={{ 
                background: '#f5f5f5', 
                padding: '12px', 
                borderRadius: '6px',
                minHeight: '60px'
              }}>
                {report.description ?? 'Không có mô tả'}
              </div>
            </Descriptions.Item>

            {report.adminNotes && (
              <Descriptions.Item label="Ghi chú admin" span={2}>
                <div style={{ 
                  background: '#fff7f0', 
                  padding: '12px', 
                  borderRadius: '6px',
                  border: '1px solid #ffa940'
                }}>
                  {report.adminNotes}
                </div>
              </Descriptions.Item>
            )}
          </Descriptions>

          {/* Thông tin đối tượng bị báo cáo */}
          <div className="reports-target-content">
            <h4>
              {report.targetType === 'POST' ? <FileTextOutlined /> : <MessageOutlined />}
              Thông tin {report.targetType === 'POST' ? 'bài đăng' : 'bình luận'} bị báo cáo
            </h4>
            
            {/* Thông tin cơ bản */}
            <div style={{ marginBottom: '12px' }}>
              <div style={{ 
                display: 'flex', 
                gap: '16px', 
                padding: '8px 12px',
                background: '#f8f9fa',
                borderRadius: '6px',
                fontSize: '13px'
              }}>
                <span><strong>ID đối tượng:</strong> #{report.targetId}</span>
                <span><strong>Loại:</strong> {report.targetType === 'POST' ? 'Bài đăng' : 'Bình luận'}</span>
                {report.targetAuthorUsername && (
                  <span><strong>Tác giả:</strong> {report.targetAuthorUsername}</span>
                )}
              </div>
            </div>

            {/* Nội dung */}
            <div className="content-box">
              {report.targetContent ? (
                <div>
                  <div style={{ 
                    fontSize: '12px', 
                    color: '#666', 
                    marginBottom: '8px',
                    fontStyle: 'italic'
                  }}>
                    Nội dung {report.targetType === 'POST' ? 'bài đăng' : 'bình luận'} bị báo cáo:
                  </div>
                  <div style={{ 
                    background: '#fff', 
                    padding: '12px', 
                    borderRadius: '4px',
                    border: '1px solid #e8e8e8',
                    lineHeight: '1.5'
                  }}>
                    {report.targetContent}
                  </div>
                </div>
              ) : (
                <div style={{ 
                  textAlign: 'center', 
                  color: '#999', 
                  padding: '24px',
                  fontStyle: 'italic'
                }}>
                  <FileTextOutlined style={{ fontSize: '24px', marginBottom: '8px', display: 'block' }} />
                  Nội dung không khả dụng
                  <div style={{ fontSize: '12px', marginTop: '8px', lineHeight: '1.4' }}>
                    <div>• {report.targetType === 'POST' ? 'Bài đăng' : 'Bình luận'} có thể đã bị xóa</div>
                    <div>• Hoặc người dùng đã xóa tài khoản</div>
                    <div>• ID đối tượng: #{report.targetId}</div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
      </Modal>

      {/* Report Action Modal - Đặt ngoài ReportDetailModal để tránh conflict */}
      {actionModalVisible && (
        <ReportActionModal
          visible={actionModalVisible}
          onCancel={handleCloseActionModal}
          report={report}
          onActionCompleted={handleActionCompleted}
        />
      )}
    </Fragment>
  );
};

export default ReportDetailModal;
