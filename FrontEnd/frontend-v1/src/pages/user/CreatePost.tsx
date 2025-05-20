// src/pages/user/CreatePost.tsx
import { Typography, Form, Input, Button, message } from "antd";

const { Title } = Typography;
const { TextArea } = Input;

const CreatePost: React.FC = () => {
  const onFinish = (values: { content: string }) => {
    console.log("Bài đăng mới:", values);
    message.success("Đăng bài thành công!");
  };

  return (
    <>
      <Title level={2}>Tạo Bài Đăng Mới</Title>
      <Form name="create-post" layout="vertical" onFinish={onFinish}>
        <Form.Item
          label="Nội dung bài đăng"
          name="content"
          rules={[{ required: true, message: "Vui lòng nhập nội dung bài đăng!" }]}
        >
          <TextArea rows={4} />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Đăng Bài
          </Button>
        </Form.Item>
      </Form>
    </>
  );
};

export default CreatePost;