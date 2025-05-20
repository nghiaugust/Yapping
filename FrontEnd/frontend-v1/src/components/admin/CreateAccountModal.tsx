// src/components/admin/CreateAccountModal.tsx
import { Modal, Form, Input, Button, Select, message } from "antd";
import { useState } from "react";
import { createUser } from "../../service/admin/userService";

interface CreateAccountModalProps {
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

const CreateAccountModal: React.FC<CreateAccountModalProps> = ({
  visible,
  onClose,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: {
    username: string;
    email: string;
    password: string;
    fullName: string;
    bio: string;
    roles: ("ADMIN" | "USER")[];
  }) => {
    setLoading(true);
    try {
      // Khai báo userData với kiểu CreateUserData
      const userData: {
        username: string;
        email: string;
        password: string;
        fullName: string;
        bio?: string;
        roles: { name: "ADMIN" | "USER" }[];
      } = {
        username: values.username,
        email: values.email,
        password: values.password,
        fullName: values.fullName,
        bio: values.bio,
        roles: values.roles.map((role) => ({ name: role })),
      };
      await createUser(userData);
      message.success("Tạo tài khoản thành công!");
      form.resetFields();
      onSuccess();
      onClose();
    } catch (error) {
      message.error("Tạo tài khoản thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="Tạo Tài Khoản Mới"
      open={visible}
      onCancel={onClose}
      footer={null}
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{ roles: ["USER"] }}
      >
        <Form.Item
          name="username"
          label="Tên đăng nhập"
          rules={[{ required: true, message: "Vui lòng nhập tên đăng nhập!" }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="email"
          label="Email"
          rules={[
            { required: true, message: "Vui lòng nhập email!" },
            { type: "email", message: "Email không đúng định dạng!" },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="password"
          label="Mật khẩu"
          rules={[{ required: true, message: "Vui lòng nhập mật khẩu!" }]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          name="fullName"
          label="Họ và tên"
          rules={[{ required: true, message: "Vui lòng nhập họ và tên!" }]}
        >
          <Input />
        </Form.Item>
        <Form.Item name="bio" label="Tiểu sử">
          <Input.TextArea />
        </Form.Item>
        <Form.Item
          name="roles"
          label="Vai trò"
          rules={[{ required: true, message: "Vui lòng chọn ít nhất một vai trò!" }]}
        >
          <Select mode="multiple" placeholder="Chọn vai trò">
            <Select.Option value="ADMIN">Admin</Select.Option>
            <Select.Option value="USER">User</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}>
            Tạo Tài Khoản
          </Button>
          <Button style={{ marginLeft: 8 }} onClick={onClose}>
            Hủy
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateAccountModal;