// src/service/user/mentionService.ts
import api from '../admin/api';
import { Mention, MentionWithDetails, CreateMentionRequest, ApiResponse } from '../../types';

/**
 * Lấy danh sách mentions của một user
 * @param userId ID của user
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @returns Promise với danh sách mentions
 */
export const getUserMentions = async (userId: number, page = 0, size = 20): Promise<ApiResponse<MentionWithDetails[]>> => {
  try {
    const response = await api.get<ApiResponse<MentionWithDetails[]>>(`/users/${userId}/mentions?page=${page}&size=${size}&sort=createdAt,desc`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching mentions for user ${userId}:`, error);
    throw error;
  }
};

/**
 * Lấy danh sách mentions trong một post
 * @param postId ID của post
 * @returns Promise với danh sách mentions trong post
 */
export const getPostMentions = async (postId: number): Promise<ApiResponse<MentionWithDetails[]>> => {
  try {
    const response = await api.get<ApiResponse<MentionWithDetails[]>>(`/posts/${postId}/mentions`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching mentions for post ${postId}:`, error);
    throw error;
  }
};

/**
 * Lấy danh sách mentions trong một comment
 * @param commentId ID của comment
 * @returns Promise với danh sách mentions trong comment
 */
export const getCommentMentions = async (commentId: number): Promise<ApiResponse<MentionWithDetails[]>> => {
  try {
    const response = await api.get<ApiResponse<MentionWithDetails[]>>(`/comments/${commentId}/mentions`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching mentions for comment ${commentId}:`, error);
    throw error;
  }
};

/**
 * Tạo mentions mới
 * @param data Dữ liệu mention
 * @returns Promise với mentions đã tạo
 */
export const createMentions = async (data: CreateMentionRequest): Promise<ApiResponse<Mention[]>> => {
  try {
    const response = await api.post<ApiResponse<Mention[]>>('/mentions', data);
    return response.data;
  } catch (error) {
    console.error('Error creating mentions:', error);
    throw error;
  }
};

/**
 * Xóa mention
 * @param mentionId ID của mention
 * @returns Promise với response
 */
export const deleteMention = async (mentionId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/mentions/${mentionId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting mention ${mentionId}:`, error);
    throw error;
  }
};

/**
 * Tìm kiếm user để mention (suggestions)
 * @param query Chuỗi tìm kiếm
 * @param limit Số lượng kết quả tối đa
 * @returns Promise với danh sách user suggestions
 */
export const searchUsersForMention = async (query: string, limit = 10): Promise<ApiResponse<{
  id: number;
  username: string;
  fullName: string;
  profilePicture?: string;
}[]>> => {
  try {
    const response = await api.get<ApiResponse<{
      id: number;
      username: string;
      fullName: string;
      profilePicture?: string;
    }[]>>(`/users/search?q=${encodeURIComponent(query)}&limit=${limit}&mentionable=true`);
    return response.data;
  } catch (error) {
    console.error(`Error searching users for mention with query "${query}":`, error);
    throw error;
  }
};
