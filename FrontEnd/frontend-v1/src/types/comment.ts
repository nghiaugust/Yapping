// src/types/comment.ts

export interface CommentUser {
  id: number;
  username: string;
  userFullName: string;
  userProfilePicture?: string;
}

export interface Comment {
  id: number;
  postId: number;
  userId: number;
  username: string;
  userFullName: string;
  userProfilePicture?: string;
  parentCommentId?: number;
  content: string;
  likeCount: number;
  createdAt: string;
  updatedAt: string;
  isLiked?: boolean;
  user?: CommentUser;
}

export interface CommentPageResponse {
  content: Comment[];
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
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}

export interface CreateCommentRequest {
  content: string;
  postId: number;
  parentCommentId?: number; // Để reply comment
}

export interface UpdateCommentRequest {
  content: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
