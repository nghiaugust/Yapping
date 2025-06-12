// src/pages/user/CreatePostUnified.tsx
import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Button,
  Upload,
  Select,
  Card,
  message,
  Typography,
  Image,
  Modal,
  Divider,
  Tabs,
  Space,
  Tag
} from 'antd';
import {
  PictureOutlined,
  FileTextOutlined,
  EditOutlined,
  GlobalOutlined,
  TeamOutlined,
  LockOutlined,
  LinkOutlined
} from '@ant-design/icons';
import type { UploadFile, UploadProps, TabsProps } from 'antd';
import { createPost, createPostWithMedia, createPostWithResource } from '../../service/user/postService';
import { getAllCategories, getSubcategoriesByCategoryId } from '../../service/user/categoryService';
import type { CreatePostRequest, CreatePostWithMediaData, CreatePostWithResourceData } from '../../service/user/postService';
import type { Category, Subcategory } from '../../service/user/categoryService';

const { TextArea } = Input;
const { Option } = Select;
const { Title } = Typography;

interface FormValues {
  content: string;
  visibility: 'PUBLIC' | 'FOLLOWERS_ONLY' | 'PRIVATE';
  title?: string;
  description?: string;
  driveLink?: string;
  categoryId?: number;
  subCategoryId?: number;
}

const CreatePostUnified: React.FC = () => {
  const [form] = Form.useForm<FormValues>();
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('text');
  
  // States for text/media post
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState('');
  const [previewTitle, setPreviewTitle] = useState('');
  
  // States for resource post
  const [categories, setCategories] = useState<Category[]>([]);
  const [subcategories, setSubcategories] = useState<Subcategory[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<number>();

  const MAX_FILES = 5;
  const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
  const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'video/mp4', 'video/avi', 'video/mov'];

  const privacyOptions = [
    { value: 'PUBLIC' as const, label: 'Công khai', icon: <GlobalOutlined /> },
    { value: 'FOLLOWERS_ONLY' as const, label: 'Chỉ người theo dõi', icon: <TeamOutlined /> },
    { value: 'PRIVATE' as const, label: 'Riêng tư', icon: <LockOutlined /> }
  ];
  // Load categories on component mount
  useEffect(() => {
    void loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const response = await getAllCategories();
      if (response.success) {
        setCategories(response.data);
      }
    } catch (error) {
      console.error('Error loading categories:', error);
      message.error('Không thể tải danh sách thể loại!');
    }
  };
  const handleCategoryChange = (categoryId: number) => {
    void handleCategoryChangeAsync(categoryId);
  };

  const handleCategoryChangeAsync = async (categoryId: number) => {
    setSelectedCategory(categoryId);
    form.setFieldsValue({ subCategoryId: undefined });
    
    try {
      const response = await getSubcategoriesByCategoryId(categoryId);
      if (response.success) {
        setSubcategories(response.data);
      }
    } catch (error) {
      console.error('Error loading subcategories:', error);
      setSubcategories([]);
    }
  };
  // Media upload handlers
  const handlePreview = (file: UploadFile) => {
    void handlePreviewAsync(file);
  };

  const handlePreviewAsync = async (file: UploadFile) => {
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
  const handleSubmit = (values: FormValues) => {
    void handleSubmitAsync(values);
  };

  const handleSubmitAsync = async (values: FormValues) => {
    setLoading(true);
    try {
      const { content, visibility = 'PUBLIC' } = values;

      if (activeTab === 'text') {
        // Text post with optional media
        if (fileList.length > 0) {
          const postData: CreatePostWithMediaData = {
            content: content ?? '',
            visibility,
            files: fileList.map(file => file.originFileObj as File)
          };
          await createPostWithMedia(postData);        } else {
          const postData: CreatePostRequest = {
            content,
            visibility,
            post_type: 'TEXT'
          };
          await createPost(postData);
        }
      } else if (activeTab === 'resource') {
        // Resource post
        const { title, description, driveLink, categoryId, subCategoryId } = values;
        
        if (!title || !driveLink || !categoryId) {
          message.error('Vui lòng điền đầy đủ thông tin bắt buộc!');
          return;
        }

        const postData: CreatePostWithResourceData = {
          content,
          visibility,
          title,
          description,
          driveLink,
          categoryId,
          subCategoryId
        };
        await createPostWithResource(postData);
      }

      message.success('Tạo bài đăng thành công!');
      form.resetFields();
      setFileList([]);
      setSelectedCategory(undefined);
      setSubcategories([]);
    } catch (error) {
      console.error('Error creating post:', error);
      message.error('Có lỗi xảy ra khi tạo bài đăng!');
    } finally {
      setLoading(false);
    }
  };

  const validateGoogleDriveLink = (_: unknown, value: string) => {
    if (activeTab !== 'resource') return Promise.resolve();
    if (!value) {
      return Promise.reject(new Error('Vui lòng nhập link Google Drive!'));
    }
    
    const googleDrivePattern = /^https:\/\/drive\.google\.com\/(file\/d\/|open\?id=)/;
    if (!googleDrivePattern.test(value)) {
      return Promise.reject(new Error('Link phải là link Google Drive hợp lệ!'));
    }
    
    return Promise.resolve();
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

  const tabItems: TabsProps['items'] = [
    {
      key: 'text',
      label: (
        <span>
          <EditOutlined />
          Bài đăng thường
        </span>
      ),
      children: (
        <div>
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
              showCount
              maxLength={500}
            />
          </Form.Item>

          <Form.Item label="Ảnh/Video (Tối đa 5 file, mỗi file < 10MB)">
            <Upload
              listType="picture-card"
              fileList={fileList}
              onPreview={handlePreview}
              onChange={handleChange}
              beforeUpload={beforeUpload}
              onRemove={removeFile}
              multiple
            >
              {fileList.length >= MAX_FILES ? null : uploadButton}
            </Upload>
          </Form.Item>

          {fileList.length > 0 && (
            <div style={{ marginBottom: 16 }}>
              <Space wrap>
                {fileList.map((file, index) => (
                  <Tag
                    key={file.uid}
                    closable
                    onClose={() => removeFile(file)}
                    color={isImage(file) ? 'blue' : 'green'}
                  >
                    {isImage(file) ? 'Ảnh' : 'Video'} {index + 1}
                  </Tag>
                ))}
              </Space>
            </div>
          )}
        </div>
      ),
    },
    {
      key: 'resource',
      label: (
        <span>
          <FileTextOutlined />
          Chia sẻ tài liệu
        </span>
      ),
      children: (
        <div>
          <Form.Item
            name="content"
            label="Nội dung bài đăng"
            rules={[
              { required: true, message: 'Vui lòng nhập nội dung bài đăng!' },
              { max: 500, message: 'Nội dung không được vượt quá 500 ký tự!' }
            ]}
          >
            <TextArea
              rows={3}
              placeholder="Mô tả về tài liệu bạn đang chia sẻ..."
              showCount
              maxLength={500}
            />
          </Form.Item>

          <Form.Item
            name="title"
            label="Tiêu đề tài liệu"
            rules={[
              { required: activeTab === 'resource', message: 'Vui lòng nhập tiêu đề tài liệu!' },
              { max: 200, message: 'Tiêu đề không được vượt quá 200 ký tự!' }
            ]}
          >
            <Input
              placeholder="VD: Hướng dẫn lập trình Java từ cơ bản đến nâng cao"
              showCount
              maxLength={200}
            />
          </Form.Item>

          <Form.Item
            name="description"
            label="Mô tả chi tiết (tùy chọn)"
          >
            <TextArea
              rows={2}
              placeholder="Mô tả chi tiết về nội dung tài liệu..."
              showCount
              maxLength={1000}
            />
          </Form.Item>

          <Form.Item
            name="driveLink"
            label="Link Google Drive"
            rules={[{ validator: validateGoogleDriveLink }]}
          >
            <Input
              prefix={<LinkOutlined />}
              placeholder="https://drive.google.com/file/d/..."
            />
          </Form.Item>

          <Form.Item
            name="categoryId"
            label="Thể loại"
            rules={[
              { required: activeTab === 'resource', message: 'Vui lòng chọn thể loại!' }
            ]}
          >
            <Select
              placeholder="Chọn thể loại"
              onChange={handleCategoryChange}
              loading={categories.length === 0}
            >
              {categories.map(category => (
                <Option key={category.id} value={category.id}>
                  {category.name}
                </Option>
              ))}
            </Select>
          </Form.Item>

          {selectedCategory && subcategories.length > 0 && (
            <Form.Item
              name="subCategoryId"
              label="Thể loại phụ (tùy chọn)"
            >
              <Select placeholder="Chọn thể loại phụ">
                {subcategories.map(subcategory => (
                  <Option key={subcategory.id} value={subcategory.id}>
                    {subcategory.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          )}
        </div>
      ),
    },
  ];

  return (
    <Card style={{ maxWidth: 800, margin: '0 auto' }}>
      <Title level={2} style={{ textAlign: 'center', marginBottom: 30 }}>
        Tạo Bài Đăng Mới
      </Title>

      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{ visibility: 'PUBLIC' }}
      >
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabItems}
          centered
          size="large"
        />

        <Divider />

        <Form.Item
          name="visibility"
          label="Quyền riêng tư"
          rules={[{ required: true, message: 'Vui lòng chọn quyền riêng tư!' }]}
        >
          <Select>
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

        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            size="large"
            block
            icon={activeTab === 'text' ? <EditOutlined /> : <FileTextOutlined />}
          >
            {loading ? 'Đang đăng...' : `Đăng ${activeTab === 'text' ? 'bài viết' : 'tài liệu'}`}
          </Button>
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

export default CreatePostUnified;
