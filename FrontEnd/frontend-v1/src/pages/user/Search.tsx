// src/pages/user/Search.tsx
import { Typography, Input, List, Tabs } from "antd";

const { Title } = Typography;
const { Search: SearchInput } = Input;
const { TabPane } = Tabs;

const searchResults = {
  posts: [
    { id: 1, content: "Bài đăng về React", type: "Post" },
    { id: 2, content: "Học TypeScript dễ dàng", type: "Post" },
  ],
  resources: [
    { id: 1, title: "Tài liệu React", type: "Resource" },
    { id: 2, title: "Hướng dẫn TypeScript", type: "Resource" },
  ],
  users: [
    { id: 1, username: "User1", type: "User" },
    { id: 2, username: "User2", type: "User" },
  ],
};

const Search: React.FC = () => {
  const onSearch = (value: string) => {
    console.log("Tìm kiếm:", value);
  };

  return (
    <>
      <Title level={2}>Tìm Kiếm</Title>
      <SearchInput
        placeholder="Tìm kiếm bài đăng, tài liệu, người dùng, hashtag..."
        onSearch={onSearch}
        style={{ marginBottom: "16px" }}
      />
      <Tabs defaultActiveKey="posts">
        <TabPane tab="Bài Đăng" key="posts">
          <List
            dataSource={searchResults.posts}
            renderItem={(item) => <List.Item>{item.content}</List.Item>}
          />
        </TabPane>
        <TabPane tab="Tài Liệu" key="resources">
          <List
            dataSource={searchResults.resources}
            renderItem={(item) => <List.Item>{item.title}</List.Item>}
          />
        </TabPane>
        <TabPane tab="Người Dùng" key="users">
          <List
            dataSource={searchResults.users}
            renderItem={(item) => <List.Item>{item.username}</List.Item>}
          />
        </TabPane>
      </Tabs>
    </>
  );
};

export default Search;