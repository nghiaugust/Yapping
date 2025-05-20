// src/pages/user/Resources.tsx
import { Typography, Table, Input, Select } from "antd";
import { useState } from "react";

const { Title } = Typography;
const { Search } = Input;
const { Option } = Select;

const resources = [
  { key: "1", title: "Tài liệu React", category: "Lập trình", type: "PDF" },
  { key: "2", title: "Hướng dẫn TypeScript", category: "Lập trình", type: "Video" },
  { key: "3", title: "Thiết kế UI/UX", category: "Thiết kế", type: "PDF" },
];

const Resources: React.FC = () => {
  const [filteredData, setFilteredData] = useState(resources);

  const columns = [
    { title: "Tiêu đề", dataIndex: "title", key: "title" },
    { title: "Danh mục", dataIndex: "category", key: "category" },
    { title: "Loại", dataIndex: "type", key: "type" },
  ];

  const onSearch = (value: string) => {
    setFilteredData(
      resources.filter((item) =>
        item.title.toLowerCase().includes(value.toLowerCase())
      )
    );
  };

  const onFilter = (value: string) => {
    setFilteredData(
      value ? resources.filter((item) => item.category === value) : resources
    );
  };

  return (
    <>
      <Title level={2}>Tài Liệu</Title>
      <div style={{ marginBottom: "16px" }}>
        <Search
          placeholder="Tìm kiếm tài liệu"
          onSearch={onSearch}
          style={{ width: 200, marginRight: "16px" }}
        />
        <Select
          placeholder="Lọc theo danh mục"
          onChange={onFilter}
          style={{ width: 200 }}
          allowClear
        >
          <Option value="Lập trình">Lập trình</Option>
          <Option value="Thiết kế">Thiết kế</Option>
        </Select>
      </div>
      <Table columns={columns} dataSource={filteredData} />
    </>
  );
};

export default Resources;