// src/service/admin/postService.ts
import api from './api';
import { Post, PostPageResponse } from '../../types/post';

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

interface PostStatistics {
  totalPosts: number;
  publicPosts: number;
  privatePosts: number;
  followersOnlyPosts: number;
  textPosts: number;
  resourcePosts: number;
  postsThisWeek: number;
  postsThisMonth: number;
}

/**
 * Lấy danh sách tất cả bài đăng (Admin only)
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @param visibility Lọc theo quyền riêng tư (optional)
 * @param postType Lọc theo loại bài đăng (optional)
 * @returns Promise với danh sách bài đăng
 */
export const getAllPosts = async (
  page = 0, 
  size = 20, 
  visibility?: "PUBLIC" | "FOLLOWERS_ONLY" | "PRIVATE",
  postType?: "TEXT" | "RESOURCE"
): Promise<ApiResponse<PostPageResponse>> => {
  try {
    let url = `/admin/posts?page=${page}&size=${size}&sort=createdAt,desc`;
    if (visibility) url += `&visibility=${visibility}`;
    if (postType) url += `&postType=${postType}`;
    
    const response = await api.get<ApiResponse<PostPageResponse>>(url);
    return response.data;
  } catch (error) {
    console.error('Error fetching all posts:', error);
    throw error;
  }
};

/**
 * Lấy chi tiết bài đăng (Admin only)
 * @param postId ID của bài đăng
 * @returns Promise với thông tin chi tiết bài đăng
 */
export const getPostById = async (postId: number): Promise<ApiResponse<Post>> => {
  try {
    const response = await api.get<ApiResponse<Post>>(`/admin/posts/${postId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching post ${postId}:`, error);
    throw error;
  }
};

/**
 * Xóa bài đăng (Admin only)
 * @param postId ID của bài đăng
 * @returns Promise với kết quả xóa
 */
export const deletePost = async (postId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/admin/posts/${postId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting post ${postId}:`, error);
    throw error;
  }
};

/**
 * Thay đổi trạng thái hiển thị bài đăng (Admin only)
 * @param postId ID của bài đăng
 * @param visibility Trạng thái hiển thị mới
 * @returns Promise với kết quả cập nhật
 */
export const updatePostVisibility = async (
  postId: number, 
  visibility: "PUBLIC" | "FOLLOWERS_ONLY" | "PRIVATE"
): Promise<ApiResponse<Post>> => {
  try {
    const response = await api.patch<ApiResponse<Post>>(`/admin/posts/${postId}/visibility`, {
      visibility
    });
    return response.data;
  } catch (error) {
    console.error(`Error updating post visibility ${postId}:`, error);
    throw error;
  }
};

/**
 * Lấy thống kê bài đăng (Admin only)
 * @returns Promise với dữ liệu thống kê
 */
export const getPostStatistics = async (): Promise<ApiResponse<PostStatistics>> => {
  try {
    const response = await api.get<ApiResponse<PostStatistics>>('/admin/posts/statistics');
    return response.data;
  } catch (error) {
    console.error('Error fetching post statistics:', error);
    throw error;
  }
};
