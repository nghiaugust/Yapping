// src/service/user/hashtagService.ts
import api from '../admin/api';
import { ApiResponse } from '../../types';

export interface Hashtag {
  id: number;
  name: string;
  usageCount: number;
  createdAt: string;
}

export interface PostHashtag {
  id: number;
  postId: number;
  hashtagId: number;
  hashtag: Hashtag;
}

export interface TrendingHashtag extends Hashtag {
  recentUsageCount: number; // Số lần sử dụng gần đây (7 ngày)
}

/**
 * Lấy danh sách hashtags trending
 * @param limit Số lượng hashtags (mặc định 10)
 * @param days Số ngày để tính trending (mặc định 7)
 * @returns Promise với danh sách trending hashtags
 */
export const getTrendingHashtags = async (limit = 10, days = 7): Promise<ApiResponse<TrendingHashtag[]>> => {
  try {
    const response = await api.get<ApiResponse<TrendingHashtag[]>>(`/hashtags/trending?limit=${limit}&days=${days}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching trending hashtags:', error);
    throw error;
  }
};

/**
 * Tìm kiếm hashtags
 * @param query Từ khóa tìm kiếm
 * @param limit Số lượng kết quả tối đa
 * @returns Promise với danh sách hashtags
 */
export const searchHashtags = async (query: string, limit = 20): Promise<ApiResponse<Hashtag[]>> => {
  try {
    const response = await api.get<ApiResponse<Hashtag[]>>(`/hashtags/search?q=${encodeURIComponent(query)}&limit=${limit}`);
    return response.data;
  } catch (error) {
    console.error(`Error searching hashtags with query "${query}":`, error);
    throw error;
  }
};

/**
 * Lấy chi tiết hashtag theo tên
 * @param hashtagName Tên hashtag (không có #)
 * @returns Promise với thông tin hashtag
 */
export const getHashtagByName = async (hashtagName: string): Promise<ApiResponse<Hashtag>> => {
  try {
    const response = await api.get<ApiResponse<Hashtag>>(`/hashtags/${encodeURIComponent(hashtagName)}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching hashtag "${hashtagName}":`, error);
    throw error;
  }
};

/**
 * Lấy danh sách bài đăng theo hashtag
 * @param hashtagName Tên hashtag
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @returns Promise với danh sách bài đăng
 */
export const getPostsByHashtag = async (
  hashtagName: string, 
  page = 0, 
  size = 20
): Promise<ApiResponse<{
  content: {
    id: number;
    content: string;
    createdAt: string;
    user: {
      id: number;
      username: string;
      fullName: string;
      profilePicture?: string;
    };
    likeCount: number;
    commentCount: number;
    repostCount: number;
  }[];
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}>> => {
  try {
    const response = await api.get<ApiResponse<{
      content: {
        id: number;
        content: string;
        createdAt: string;
        user: {
          id: number;
          username: string;
          fullName: string;
          profilePicture?: string;
        };
        likeCount: number;
        commentCount: number;
        repostCount: number;
      }[];
      totalElements: number;
      totalPages: number;
      last: boolean;
      first: boolean;
      numberOfElements: number;
      empty: boolean;
    }>>(`/hashtags/${encodeURIComponent(hashtagName)}/posts?page=${page}&size=${size}&sort=createdAt,desc`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching posts for hashtag "${hashtagName}":`, error);
    throw error;
  }
};

/**
 * Lấy suggestions hashtags khi user đang gõ
 * @param query Chuỗi tìm kiếm
 * @param limit Số lượng suggestions
 * @returns Promise với danh sách hashtag suggestions
 */
export const getHashtagSuggestions = async (query: string, limit = 5): Promise<ApiResponse<Hashtag[]>> => {
  try {
    const response = await api.get<ApiResponse<Hashtag[]>>(`/hashtags/suggestions?q=${encodeURIComponent(query)}&limit=${limit}`);
    return response.data;
  } catch (error) {
    console.error(`Error getting hashtag suggestions for "${query}":`, error);
    throw error;
  }
};
