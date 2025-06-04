// src/service/user/postService.ts
import api from '../admin/api';
import { Post, PostPageResponse } from '../../types/post';

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
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
