// src/components/admin/report/ReportActionModal.tsx
import { Modal, Form, Input, Button, message, Divider } from "antd";
import { useState } from "react";
import { 
  DeleteOutlined, 
  NotificationOutlined, 
  ExclamationCircleOutlined,
  FileTextOutlined,
  MessageOutlined
} from "@ant-design/icons";
import { updateReportStatus } from "../../../service/admin/reportService";
import { deletePost } from "../../../service/admin/postService";
import { deleteComment } from "../../../service/admin/commentService";
import { createNotification } from "../../../service/admin/notificationService";
import { ReportWithDetails } from "../../../types/report";

const { TextArea } = Input;

interface ReportActionModalProps {
  visible: boolean;
  onCancel: () => void;
  report: ReportWithDetails | null;
  onActionCompleted: () => void;
}

interface FormValues {
  notificationMessage: string;
}

const ReportActionModal: React.FC<ReportActionModalProps> = ({
  visible,
  onCancel,
  report,
  onActionCompleted,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const getDefaultMessage = (): string => {
    if (!report) return "";
    const contentType = report.targetType === 'POST' ? 'bài đăng' : 'bình luận';
    return `${contentType.charAt(0).toUpperCase() + contentType.slice(1)} của bạn vi phạm quy tắc cộng đồng và đã bị xóa.`;
  };

  const handleDeleteOnly = async () => {
    if (!report?.targetId) {
      message.error('Không tìm thấy ID đối tượng để xóa');
      return;
    }

    setLoading(true);
    try {
      if (report.targetType === 'POST') {
        await deletePost(report.targetId);
        message.success('Đã xóa bài đăng thành công');
      } else {
        await deleteComment(report.targetId);
        message.success('Đã xóa bình luận thành công');
      }

      // Cập nhật trạng thái báo cáo
      await updateReportStatus(report.id, {
        status: 'RESOLVED_ACTION_TAKEN',
        adminNotes: `Đã xóa ${report.targetType === 'POST' ? 'bài đăng' : 'bình luận'}`
      });

      message.success('Xử lý báo cáo thành công');
      onActionCompleted();
      onCancel();
    } catch (error) {
      console.error('Error deleting content:', error);
      message.error('Lỗi khi xóa nội dung');
    } finally {
      setLoading(false);
    }
  };

  const handleNotifyOnly = async (values: FormValues) => {
    if (!report?.targetAuthorId) {
      message.error('Không tìm thấy ID chủ sở hữu để gửi thông báo');
      return;
    }

    setLoading(true);
    try {
      // Gửi thông báo
      await createNotification({
        userId: report.targetAuthorId,
        type: 'SYSTEM',
        targetType: report.targetType,
        targetId: report.targetId,
        targetOwnerId: report.targetAuthorId,
        message: values.notificationMessage
      });

      // Cập nhật trạng thái báo cáo
      await updateReportStatus(report.id, {
        status: 'RESOLVED_ACTION_TAKEN',
        adminNotes: 'Đã gửi thông báo cảnh báo'
      });

      message.success('Đã gửi thông báo thành công');
      onActionCompleted();
      onCancel();
    } catch (error) {
      console.error('Error sending notification:', error);
      message.error('Lỗi khi gửi thông báo');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAndNotify = async (values: FormValues) => {
    if (!report?.targetId || !report?.targetAuthorId) {
      message.error('Không đủ thông tin để thực hiện hành động');
      return;
    }

    setLoading(true);
    try {
      // Xóa nội dung
      if (report.targetType === 'POST') {
        await deletePost(report.targetId);
      } else {
        await deleteComment(report.targetId);
      }

      // Gửi thông báo
      await createNotification({
        userId: report.targetAuthorId,
        type: 'SYSTEM',
        targetType: report.targetType,
        targetId: report.targetId,
        targetOwnerId: report.targetAuthorId,
        message: values.notificationMessage
      });

      // Cập nhật trạng thái báo cáo
      await updateReportStatus(report.id, {
        status: 'RESOLVED_ACTION_TAKEN',
        adminNotes: `Đã xóa ${report.targetType === 'POST' ? 'bài đăng' : 'bình luận'} và gửi thông báo`
      });

      message.success('Đã xóa nội dung và gửi thông báo thành công');
      onActionCompleted();
      onCancel();
    } catch (error) {
      console.error('Error in delete and notify:', error);
      message.error('Lỗi khi thực hiện hành động');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  if (!report) return null;

  const contentType = report.targetType === 'POST' ? 'bài đăng' : 'bình luận';
  const deleteButtonText = report.targetType === 'POST' ? 'Xóa bài đăng' : 'Xóa bình luận';

  return (
    <Modal
      title={
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <div style={{
            width: '32px',
            height: '32px',
            borderRadius: '8px',
            background: 'linear-gradient(135deg, #ff4d4f, #ff7875)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff'
          }}>
            <ExclamationCircleOutlined />
          </div>
          <span style={{ fontSize: '16px', fontWeight: '600' }}>
            Xử lý vi phạm - {contentType}
          </span>
        </div>
      }
      open={visible}
      onCancel={handleCancel}
      footer={null}
      width={600}
      style={{ top: 20 }}
      maskClosable={true}
      closable={true}
      destroyOnClose={true}
      zIndex={1001}
    >
      <div style={{ marginTop: '16px' }}>
        {/* Thông tin báo cáo */}
        <div style={{
          background: '#fff2f0',
          border: '1px solid #ffccc7',
          borderRadius: '8px',
          padding: '16px',
          marginBottom: '20px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
            {report.targetType === 'POST' ? (
              <FileTextOutlined style={{ color: '#ff4d4f' }} />
            ) : (
              <MessageOutlined style={{ color: '#ff4d4f' }} />
            )}
            <span style={{ fontWeight: '600', color: '#ff4d4f' }}>
              {contentType.charAt(0).toUpperCase() + contentType.slice(1)} vi phạm
            </span>
          </div>
          <p style={{ margin: 0, fontSize: '14px', color: '#8c8c8c' }}>
            Báo cáo từ: <strong>{report.reporterUsername ?? 'Ẩn danh'}</strong>
          </p>
          <p style={{ margin: 0, fontSize: '14px', color: '#8c8c8c' }}>
            Tác giả: <strong>{report.targetAuthorUsername ?? 'Không xác định'}</strong>
          </p>
        </div>

        <Form
          form={form}
          layout="vertical"
          initialValues={{
            notificationMessage: getDefaultMessage()
          }}
        >
          <Form.Item
            name="notificationMessage"
            label={
              <span style={{ fontWeight: '600', color: '#262626' }}>
                <NotificationOutlined style={{ marginRight: '4px' }} />
                Nội dung thông báo
              </span>
            }
            rules={[
              { required: true, message: 'Vui lòng nhập nội dung thông báo!' },
              { min: 10, message: 'Nội dung thông báo phải có ít nhất 10 ký tự!' }
            ]}
          >
            <TextArea
              rows={4}
              placeholder={`Nhập nội dung thông báo cho người dùng về việc ${contentType} bị xóa...`}
              style={{
                borderRadius: '8px',
                border: '1px solid #d9d9d9'
              }}
            />
          </Form.Item>

          <Divider style={{ margin: '16px 0' }} />

          {/* Action Buttons */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              <Button
                type="primary"
                danger
                icon={<DeleteOutlined />}
                loading={loading}
                onClick={() => void handleDeleteOnly()}
                style={{
                  borderRadius: '6px',
                  fontWeight: '500',
                  boxShadow: '0 2px 8px rgba(255, 77, 79, 0.2)'
                }}
              >
                {deleteButtonText}
              </Button>

              <Button
                type="primary"
                icon={<NotificationOutlined />}
                loading={loading}
                onClick={() => void form.validateFields().then(handleNotifyOnly)}
                style={{
                  background: '#1890ff',
                  borderColor: '#1890ff',
                  borderRadius: '6px',
                  fontWeight: '500',
                  boxShadow: '0 2px 8px rgba(24, 144, 255, 0.2)'
                }}
              >
                Gửi thông báo
              </Button>

              <Button
                type="primary"
                icon={<DeleteOutlined />}
                loading={loading}
                onClick={() => void form.validateFields().then(handleDeleteAndNotify)}
                style={{
                  background: '#722ed1',
                  borderColor: '#722ed1',
                  borderRadius: '6px',
                  fontWeight: '500',
                  boxShadow: '0 2px 8px rgba(114, 46, 209, 0.2)'
                }}
              >
                Xóa và gửi thông báo
              </Button>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '8px' }}>
              <Button
                onClick={handleCancel}
                style={{
                  borderRadius: '6px',
                  fontWeight: '500'
                }}
              >
                Hủy
              </Button>
            </div>
          </div>
        </Form>
      </div>
    </Modal>
  );
};

export default ReportActionModal;
