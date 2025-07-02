// src/types/post.ts

export interface PostUser {
  id: number;
  username: string;
  fullName: string;
  profilePicture: string | null;
}

export interface Media {
  id: number;
  postId: number;
  userId: number;
  mediaUrl: string;
  mediaType: "IMAGE" | "VIDEO";
  sortOrder: number;
  createdAt: string;
}

export interface Post {
  id: number;
  user: PostUser;
  parentPostId: number | null;
  content: string;
  visibility: "PUBLIC" | "FOLLOWERS_ONLY" | "PRIVATE";
  likeCount: number | null;
  commentCount: number | null;
  repostCount: number | null;
  quoteCount: number | null;
  createdAt: string;
  updatedAt: string;
  post_type: "TEXT" | "RESOURCE" | null; // Đồng bộ với Backend enum
  media: Media[] | null;
  isLiked?: boolean; // Frontend only field
}

export interface PostStatistics {
  totalPosts: number;
  totalTextPosts: number;
  totalResourcePosts: number;
  publicPosts: number;
  followersOnlyPosts: number;
  privatePosts: number;
  postsWithMedia: number;
  totalLikes: number;
  totalComments: number;
  totalReposts: number;
  averageInteraction: number;
  publicPercentage: number;
  followersOnlyPercentage: number;
  privatePercentage: number;
  textPostPercentage: number;
  resourcePostPercentage: number;
  todayPosts: number;
  weekPosts: number;
  monthPosts: number;
}

export interface PostPageResponse {
  content: Post[];
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
