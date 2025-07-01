// src/types/like.ts

export interface Like {
  id: number;
  userId: number | null;
  username: string;
  userFullName: string;
  userProfilePicture: string | null;
  targetType: "POST" | "COMMENT";
  targetId: number;
  createdAt: string;
}

export interface LikePageResponse {
  content: Like[];
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

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
