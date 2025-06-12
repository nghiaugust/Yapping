import React, { useState } from 'react';
import {
  Form,
  Input,
  Button,
  Upload,
  Select,
  Card,
  message,
  Row,
  Col,
  Typography,
  Image,
  Modal,
  Space,
  Progress,
  Tag,
  Divider
} from 'antd';
import {
  PictureOutlined,
  VideoCameraOutlined,
  DeleteOutlined,
  EyeOutlined,
  GlobalOutlined,
  TeamOutlined,
  LockOutlined
} from '@ant-design/icons';
import type { UploadFile, UploadProps } from 'antd';
import { createPost, createPostWithMedia } from '../../service/user/postService';
import type { CreatePostRequest, CreatePostWithMediaData } from '../../service/user/postService';

const { TextArea } = Input;
const { Option } = Select;
const { Title, Text } = Typography;

interface FormValues {
  content: string;
  visibility: 'PUBLIC' | 'FOLLOWERS_ONLY' | 'PRIVATE';
}

const CreatePost: React.FC = () => {
  const [form] = Form.useForm<FormValues>();
  const [loading, setLoading] = useState(false);
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState('');
  const [previewTitle, setPreviewTitle] = useState('');
  const [textContent, setTextContent] = useState('');

  const MAX_FILES = 5;
  const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
  const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'video/mp4', 'video/avi', 'video/mov'];

  const privacyOptions = [
    { value: 'PUBLIC' as const, label: 'Công khai', icon: <GlobalOutlined /> },
    { value: 'FOLLOWERS_ONLY' as const, label: 'Chỉ người theo dõi', icon: <TeamOutlined /> },
    { value: 'PRIVATE' as const, label: 'Riêng tư', icon: <LockOutlined /> }
  ];

  const handlePreview = async (file: UploadFile) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as File);
    }
    
    setPreviewImage(file.url ?? file.preview!);
    setPreviewVisible(true);
    setPreviewTitle(file.name ?? file.url?.substring(file.url.lastIndexOf('/') + 1) ?? '');
  };

  const getBase64 = (file: File): Promise<string> =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = () => reject(new Error('Failed to read file'));
    });

  const handleChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {
    setFileList(newFileList);
  };

  const beforeUpload = (file: File) => {
    const isValidType = ALLOWED_TYPES.includes(file.type);
    if (!isValidType) {
      message.error('Chỉ hỗ trợ file ảnh (JPEG, PNG, GIF) và video (MP4, AVI, MOV)!');
      return false;
    }

    const isValidSize = file.size <= MAX_FILE_SIZE;
    if (!isValidSize) {
      message.error('Kích thước file không được vượt quá 10MB!');
      return false;
    }

    if (fileList.length >= MAX_FILES) {
      message.error(`Chỉ được upload tối đa ${MAX_FILES} file!`);
      return false;
    }

    return false; // Prevent auto upload
  };

  const removeFile = (file: UploadFile) => {
    const newFileList = fileList.filter(item => item.uid !== file.uid);
    setFileList(newFileList);
  };

  const handleSubmit = async (values: FormValues) => {
    setLoading(true);
    try {
      const { content, visibility = 'PUBLIC' } = values;

      if (fileList.length > 0) {
        // Create post with media
        const postData: CreatePostWithMediaData = {
          content: content ?? '',
          visibility,
          files: fileList.map(file => file.originFileObj as File)
        };

        await createPostWithMedia(postData);
      } else {
        // Create simple text post
        const postData: CreatePostRequest = {
          content,
          visibility
        };

        await createPost(postData);
      }

      message.success('Tạo bài đăng thành công!');
      form.resetFields();
      setFileList([]);
      setTextContent('');
    } catch (error) {
      console.error('Error creating post:', error);
      message.error('Có lỗi xảy ra khi tạo bài đăng!');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => setPreviewVisible(false);

  const uploadButton = (
    <div>
      <PictureOutlined />
      <div style={{ marginTop: 8 }}>Upload</div>
    </div>
  );

  const isImage = (file: UploadFile) => {
    return file.type?.startsWith('image/');
  };

  return (
    <Card style={{ maxWidth: 800, margin: '0 auto' }}>
      <Title level={2} style={{ textAlign: 'center', marginBottom: 30 }}>
        Tạo Bài Đăng Mới
      </Title>

      <Form
        form={form}
        layout="vertical"
        onFinish={(values) => void handleSubmit(values)}
        initialValues={{ visibility: 'PUBLIC' }}
      >
        <Form.Item
          name="content"
          label="Nội dung bài đăng"
          rules={[
            { required: true, message: 'Vui lòng nhập nội dung bài đăng!' },
            { max: 500, message: 'Nội dung không được vượt quá 500 ký tự!' }
          ]}
        >
          <TextArea
            rows={4}
            placeholder="Bạn đang nghĩ gì?"
            value={textContent}
            onChange={(e) => setTextContent(e.target.value)}
            showCount
            maxLength={500}
          />
        </Form.Item>

        <Form.Item
          name="visibility"
          label="Quyền riêng tư"
          rules={[{ required: true, message: 'Vui lòng chọn quyền riêng tư!' }]}
        >
          <Select placeholder="Chọn quyền riêng tư">
            {privacyOptions.map(option => (
              <Option key={option.value} value={option.value}>
                <Space>
                  {option.icon}
                  {option.label}
                </Space>
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Divider />

        <Form.Item label="Ảnh/Video (Tối đa 5 file, mỗi file < 10MB)">
          <Upload
            listType="picture-card"
            fileList={fileList}
            onPreview={(file) => void handlePreview(file)}
            onChange={handleChange}
            beforeUpload={beforeUpload}
            multiple
            accept="image/*,video/*"
            showUploadList={{
              showPreviewIcon: true,
              showRemoveIcon: true,
              showDownloadIcon: false,
            }}
          >
            {fileList.length >= MAX_FILES ? null : uploadButton}
          </Upload>
        </Form.Item>

        {fileList.length > 0 && (
          <div style={{ marginBottom: 20 }}>
            <Title level={4}>File đã chọn:</Title>
            <Row gutter={[16, 16]}>
              {fileList.map(file => (
                <Col span={24} key={file.uid}>
                  <Card size="small">
                    <Row align="middle" justify="space-between">
                      <Col flex="auto">
                        <Space>
                          {isImage(file) ? <PictureOutlined /> : <VideoCameraOutlined />}
                          <Text>{file.name}</Text>
                          <Tag color={isImage(file) ? 'green' : 'blue'}>
                            {isImage(file) ? 'Ảnh' : 'Video'}
                          </Tag>
                        </Space>
                      </Col>
                      <Col>
                        <Space>
                          {isImage(file) && (
                            <Button
                              type="text"
                              icon={<EyeOutlined />}
                              onClick={() => void handlePreview(file)}
                            />
                          )}
                          <Button
                            type="text"
                            danger
                            icon={<DeleteOutlined />}
                            onClick={() => removeFile(file)}
                          />
                        </Space>
                      </Col>
                    </Row>
                    {file.percent && file.percent < 100 && (
                      <Progress percent={file.percent} size="small" />
                    )}
                  </Card>
                </Col>
              ))}
            </Row>
          </div>
        )}

        <Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Button
                type="default"
                size="large"
                block
                onClick={() => {
                  form.resetFields();
                  setFileList([]);
                  setTextContent('');
                }}
              >
                Hủy
              </Button>
            </Col>
            <Col span={12}>
              <Button
                type="primary"
                htmlType="submit"
                size="large"
                block
                loading={loading}
                disabled={!textContent.trim() && fileList.length === 0}
              >
                Đăng bài
              </Button>
            </Col>
          </Row>
        </Form.Item>
      </Form>

      <Modal
        open={previewVisible}
        title={previewTitle}
        footer={null}
        onCancel={handleCancel}
        width={800}
      >
        <Image
          alt="preview"
          style={{ width: '100%' }}
          src={previewImage}
          preview={false}
        />
      </Modal>
    </Card>
  );
};

export default CreatePost;