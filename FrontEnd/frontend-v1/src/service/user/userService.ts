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
