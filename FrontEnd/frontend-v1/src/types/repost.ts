// src/types/repost.ts
// Đồng bộ với Repost.java

export interface Repost {
  id: number;
  userId: number;
  postId: number;
  content?: string; // Quote content nếu có
  createdAt: string;
}

export interface RepostWithDetails extends Repost {
  user: {
    id: number;
    username: string;
    fullName: string;
    profilePicture?: string;
  };
  originalPost: {
    id: number;
    content: string;
    createdAt: string;
    user: {
      id: number;
      username: string;
      fullName: string;
      profilePicture?: string;
    };
  };
}

export interface CreateRepostRequest {
  postId: number;
  content?: string; // Cho quote repost
}

export interface RepostPageResponse {
  content: RepostWithDetails[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
