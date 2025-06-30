// src/service/user/postService.ts
import api from '../admin/api';
import { Post, PostPageResponse, ApiResponse } from '../../types';

export interface CreatePostRequest {
  content: string;
  visibility?: 'PUBLIC' | 'FOLLOWERS_ONLY' | 'PRIVATE';
  parentPostId?: number;
  post_type: 'TEXT' | 'RESOURCE';
}

export interface UpdatePostRequest {
  content?: string;
  visibility?: 'PUBLIC' | 'FOLLOWERS_ONLY' | 'PRIVATE';
}

export interface CreatePostWithMediaData {
  content: string;
  visibility?: 'PUBLIC' | 'FOLLOWERS_ONLY' | 'PRIVATE';
  files: File[];
}

export interface CreatePostWithResourceData {
  content: string;
  visibility?: 'PUBLIC' | 'FOLLOWERS_ONLY' | 'PRIVATE';
  parentPostId?: number;
  title: string;
  description?: string;
  driveLink: string;
  categoryId: number;
  subCategoryId?: number;
}

/**
 * Lấy danh sách bài đăng công khai
 * @param page Số trang (bắt đầu từ 0)
 * @param size Số lượng bài đăng trên mỗi trang
 * @returns Promise với dữ liệu bài đăng phân trang
 */
export const getPublicPosts = async (page = 0, size = 20) => {
  try {
    const response = await api.get<ApiResponse<PostPageResponse>>(`/posts?page=${page}&size=${size}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching public posts:', error);
    throw error;
  }
};

/**
 * Lấy thông tin chi tiết của một bài đăng
 * @param id ID của bài đăng
 * @returns Promise với dữ liệu chi tiết bài đăng
 */
export const getPostById = async (id: number) => {
  try {
    const response = await api.get<ApiResponse<Post>>(`/posts/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching post with ID ${id}:`, error);
    throw error;
  }
};

/**
 * Like một bài đăng
 * @param id ID của bài đăng cần like
 * @returns Promise với dữ liệu bài đăng sau khi đã like
 */
export const likePost = async (id: number) => {
  try {
    const response = await api.post<ApiResponse<Post>>(`/posts/${id}/like`);
    return response.data;
  } catch (error) {
    console.error(`Error liking post with ID ${id}:`, error);
    throw error;
  }
};

/**
 * Lấy danh sách bài đăng của một người dùng cụ thể
 * @param userId ID của người dùng
 * @param page Số trang (bắt đầu từ 0)
 * @param size Số lượng bài đăng trên mỗi trang
 * @returns Promise với dữ liệu bài đăng phân trang
 */
export const getUserPosts = async (userId: number, page = 0, size = 20) => {
  try {
    const response = await api.get<ApiResponse<PostPageResponse>>(`/posts/user/${userId}?page=${page}&size=${size}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching posts for user ${userId}:`, error);
    throw error;
  }
};

/**
 * Tạo bài đăng đơn (chỉ text)
 * @param postData Dữ liệu bài đăng
 * @returns Promise với dữ liệu bài đăng đã tạo
 */
export const createPost = async (postData: CreatePostRequest) => {
  try {
    const response = await api.post<ApiResponse<Post>>('/posts', postData);
    return response.data;
  } catch (error) {
    console.error('Error creating post:', error);
    throw error;
  }
};

/**
 * Tạo bài đăng kèm media (ảnh/video)
 * @param postData Dữ liệu bài đăng kèm file
 * @returns Promise với dữ liệu bài đăng đã tạo
 */
export const createPostWithMedia = async (postData: CreatePostWithMediaData) => {
  try {
    const formData = new FormData();
    formData.append('content', postData.content);
    formData.append('visibility', postData.visibility ?? 'PUBLIC');
    
    // Thêm tất cả files vào FormData
    postData.files.forEach((file) => {
      formData.append('files', file);
    });

    const response = await api.post<ApiResponse<Post>>('/posts-with-media', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error creating post with media:', error);
    throw error;
  }
};

/**
 * Tạo bài đăng kèm tài liệu
 * @param postData Dữ liệu bài đăng kèm tài liệu
 * @returns Promise với dữ liệu bài đăng đã tạo
 */
export const createPostWithResource = async (postData: CreatePostWithResourceData) => {
  try {
    const response = await api.post<ApiResponse<Post>>('/posts-with-resource', postData);
    return response.data;
  } catch (error) {
    console.error('Error creating post with resource:', error);
    throw error;
  }
};

/**
 * Cập nhật bài đăng
 * @param postId ID của bài đăng cần cập nhật
 * @param postData Dữ liệu cập nhật
 * @returns Promise với dữ liệu bài đăng đã cập nhật
 */
export const updatePost = async (postId: number, postData: UpdatePostRequest): Promise<ApiResponse<Post>> => {
  try {
    const response = await api.put<ApiResponse<Post>>(`/posts/${postId}`, postData);
    return response.data;
  } catch (error) {
    console.error(`Error updating post ${postId}:`, error);
    throw error;
  }
};

/**
 * Xóa bài đăng
 * @param postId ID của bài đăng cần xóa
 * @returns Promise với response thành công
 */
export const deletePost = async (postId: number): Promise<ApiResponse<null>> => {
  try {
    const response = await api.delete<ApiResponse<null>>(`/posts/${postId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting post ${postId}:`, error);
    throw error;
  }
};

/**
 * Unlike một bài đăng
 * @param postId ID của bài đăng cần unlike
 * @returns Promise với dữ liệu bài đăng sau khi đã unlike
 */
export const unlikePost = async (postId: number): Promise<ApiResponse<Post>> => {
  try {
    const response = await api.delete<ApiResponse<Post>>(`/posts/${postId}/like`);
    return response.data;
  } catch (error) {
    console.error(`Error unliking post ${postId}:`, error);
    throw error;
  }
};

/**
 * Lấy feed bài đăng của người dùng đang theo dõi
 * @param page Số trang (bắt đầu từ 0)
 * @param size Số lượng bài đăng trên mỗi trang
 * @returns Promise với dữ liệu feed phân trang
 */
export const getFeedPosts = async (page = 0, size = 20): Promise<ApiResponse<PostPageResponse>> => {
  try {
    const response = await api.get<ApiResponse<PostPageResponse>>(`/posts/feed?page=${page}&size=${size}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching feed posts:', error);
    throw error;
  }
};
