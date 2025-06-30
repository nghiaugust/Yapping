// src/service/user/userService.ts
import api from '../admin/api';
import { User, Role, ApiResponse } from '../../types';

// Extended User interface với thông tin thống kê
export interface UserProfile extends User {
  postsCount?: number;
  followersCount?: number;
  followingCount?: number;
}

// Request types cho user operations
export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  bio?: string;
  roles: Role[];
}

export interface UpdateUserRequest {
  fullName?: string;
  bio?: string;
  profilePicture?: string;
}

export interface PatchUserRequest {
  fullName?: string;
  roles?: Role[];
  isVerified?: boolean;
  status?: "ACTIVE" | "SUSPENDED" | "DELETED" | "PENDING_VERIFICATION";
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

// Hàm đăng ký người dùng mới
export const registerUser = async (userData: CreateUserRequest): Promise<ApiResponse<User>> => {
  try {
    const response = await api.post<ApiResponse<User>>('/users/register', userData);
    return response.data;
  } catch (error) {
    console.error('Lỗi khi đăng ký người dùng:', error);
    throw error;
  }
};

// Hàm cập nhật thông tin người dùng
export const updateUser = async (userId: number, userData: UpdateUserRequest): Promise<ApiResponse<User>> => {
  try {
    const response = await api.put<ApiResponse<User>>(`/users/${userId}`, userData);
    return response.data;
  } catch (error) {
    console.error(`Lỗi khi cập nhật người dùng ${userId}:`, error);
    throw error;
  }
};

// Hàm patch user (cho admin)
export const patchUser = async (userId: number, userData: PatchUserRequest): Promise<ApiResponse<User>> => {
  try {
    const response = await api.patch<ApiResponse<User>>(`/users/${userId}`, userData);
    return response.data;
  } catch (error) {
    console.error(`Lỗi khi patch người dùng ${userId}:`, error);
    throw error;
  }
};

// Hàm upload profile picture
export const uploadProfilePicture = async (file: File): Promise<ApiResponse<{ profilePicture: string }>> => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await api.post<ApiResponse<{ profilePicture: string }>>('/users/upload-profile-picture', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  } catch (error) {
    console.error('Lỗi khi upload profile picture:', error);
    throw error;
  }
};
