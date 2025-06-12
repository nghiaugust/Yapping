// src/service/user/followService.ts
import api from '../admin/api';
import { Follow, ApiResponse } from '../../types/follow';

/**
 * Lấy danh sách người đang theo dõi (following)
 * @param userId ID của người dùng
 * @returns Promise với danh sách người đang theo dõi
 */
export const getFollowing = async (userId: number): Promise<ApiResponse<Follow[]>> => {
  try {
    const response = await api.get<ApiResponse<Follow[]>>(`/users/${userId}/following`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching following for user ${userId}:`, error);
    throw error;
  }
};

/**
 * Lấy danh sách người theo dõi (followers)
 * @param userId ID của người dùng
 * @returns Promise với danh sách người theo dõi
 */
export const getFollowers = async (userId: number): Promise<ApiResponse<Follow[]>> => {
  try {
    const response = await api.get<ApiResponse<Follow[]>>(`/users/${userId}/followers`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching followers for user ${userId}:`, error);
    throw error;
  }
};

/**
 * Follow một người dùng
 * @param userId ID của người dùng cần follow
 * @returns Promise với response
 */
export const followUser = async (userId: number): Promise<ApiResponse<Follow>> => {
  try {
    const response = await api.post<ApiResponse<Follow>>(`/users/${userId}/follow`);
    return response.data;
  } catch (error) {
    console.error(`Error following user ${userId}:`, error);
    throw error;
  }
};

/**
 * Unfollow một người dùng
 * @param userId ID của người dùng cần unfollow
 * @returns Promise với response
 */
export const unfollowUser = async (userId: number): Promise<ApiResponse<null>> => {
  try {
    const response = await api.delete<ApiResponse<null>>(`/users/${userId}/unfollow`);
    return response.data;
  } catch (error) {
    console.error(`Error unfollowing user ${userId}:`, error);
    throw error;
  }
};
