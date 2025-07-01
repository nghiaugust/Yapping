// src/service/user/userService.ts
import api from '../admin/api';

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  fullName: string;
  bio: string;
  profilePicture: string | null;
  isVerified: boolean;
  status: string;
  createdAt: string;
  updatedAt: string;
  postsCount?: number;
  followersCount?: number;
  followingCount?: number;
  roles: {
    id: number;
    name: string;
  }[];
}

export interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

// Hàm lấy thông tin của người dùng hiện tại (đang đăng nhập)
export const getCurrentUser = async (): Promise<ApiResponse<UserProfile>> => {
  try {
    const response = await api.get<ApiResponse<UserProfile>>('/users/me');
    return response.data;
  } catch (error) {
    console.error('Lỗi khi lấy thông tin người dùng:', error);
    throw error;
  }
};

// Hàm lấy thông tin người dùng theo ID
export const getUserById = async (userId: number): Promise<ApiResponse<UserProfile>> => {
  try {
    const response = await api.get<ApiResponse<UserProfile>>(`/users/${userId}`);
    return response.data;
  } catch (error) {
    console.error(`Lỗi khi lấy thông tin người dùng ${userId}:`, error);
    throw error;
  }
};
