// src/types/bookmark.ts
// Đồng bộ với Bookmark.java và BookmarkId.java

export interface Bookmark {
  userId: number;
  postId: number;
  createdAt: string;
}

export interface BookmarkWithPost extends Bookmark {
  post: {
    id: number;
    content: string;
    visibility: "PUBLIC" | "FOLLOWERS_ONLY" | "PRIVATE";
    likeCount: number;
    commentCount: number;
    createdAt: string;
    user: {
      id: number;
      username: string;
      fullName: string;
      profilePicture?: string;
    };
    media?: {
      id: number;
      mediaUrl: string;
      mediaType: "IMAGE" | "VIDEO";
    }[];
  };
}

export interface BookmarkPageResponse {
  content: BookmarkWithPost[];
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
