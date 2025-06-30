// src/service/user/bookmarkService.ts
import api from '../admin/api';
import { Bookmark, BookmarkPageResponse, ApiResponse } from '../../types';

/**
 * Bookmark một bài đăng
 * @param postId ID của bài đăng
 * @returns Promise với bookmark đã tạo
 */
export const bookmarkPost = async (postId: number): Promise<ApiResponse<Bookmark>> => {
  try {
    const response = await api.post<ApiResponse<Bookmark>>(`/posts/${postId}/bookmark`);
    return response.data;
  } catch (error) {
    console.error(`Error bookmarking post ${postId}:`, error);
    throw error;
  }
};

/**
 * Unbookmark một bài đăng
 * @param postId ID của bài đăng
 * @returns Promise với response
 */
export const unbookmarkPost = async (postId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/posts/${postId}/bookmark`);
    return response.data;
  } catch (error) {
    console.error(`Error unbookmarking post ${postId}:`, error);
    throw error;
  }
};

/**
 * Lấy danh sách bookmarks của user hiện tại
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @returns Promise với danh sách bookmarks
 */
export const getUserBookmarks = async (page = 0, size = 20): Promise<ApiResponse<BookmarkPageResponse>> => {
  try {
    const response = await api.get<ApiResponse<BookmarkPageResponse>>(`/bookmarks?page=${page}&size=${size}&sort=createdAt,desc`);
    return response.data;
  } catch (error) {
    console.error('Error fetching user bookmarks:', error);
    throw error;
  }
};

/**
 * Kiểm tra xem bài đăng đã được bookmark chưa
 * @param postId ID của bài đăng
 * @returns Promise với trạng thái bookmark
 */
export const isPostBookmarked = async (postId: number): Promise<ApiResponse<{ isBookmarked: boolean }>> => {
  try {
    const response = await api.get<ApiResponse<{ isBookmarked: boolean }>>(`/posts/${postId}/bookmark/status`);
    return response.data;
  } catch (error) {
    console.error(`Error checking bookmark status for post ${postId}:`, error);
    throw error;
  }
};

/**
 * Xóa bookmark theo ID
 * @param bookmarkId ID của bookmark
 * @returns Promise với response
 */
export const deleteBookmark = async (bookmarkId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/bookmarks/${bookmarkId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting bookmark ${bookmarkId}:`, error);
    throw error;
  }
};
