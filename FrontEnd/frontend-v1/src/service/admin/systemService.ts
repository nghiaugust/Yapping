// src/service/admin/systemService.ts
import api from './api';
import { ApiResponse } from '../../types';

export interface SystemStatistics {
  totalUsers: number;
  activeUsers: number;
  suspendedUsers: number;
  totalPosts: number;
  postsToday: number;
  postsThisWeek: number;
  postsThisMonth: number;
  totalComments: number;
  commentsToday: number;
  totalLikes: number;
  likesToday: number;
  totalFollows: number;
  followsToday: number;
  totalReports: number;
  pendingReports: number;
  totalResources: number;
  resourcesThisWeek: number;
}

export interface UserActivity {
  userId: number;
  username: string;
  fullName: string;
  profilePicture?: string;
  lastActiveAt: string;
  postsCount: number;
  commentsCount: number;
  likesCount: number;
  followersCount: number;
  followingCount: number;
}

export interface ContentModeration {
  flaggedPosts: {
    id: number;
    content: string;
    user: {
      id: number;
      username: string;
      fullName: string;
    };
    reportCount: number;
    createdAt: string;
  }[];
  flaggedComments: {
    id: number;
    content: string;
    user: {
      id: number;
      username: string;
      fullName: string;
    };
    reportCount: number;
    createdAt: string;
  }[];
  suspiciousUsers: {
    id: number;
    username: string;
    fullName: string;
    reportCount: number;
    reason: string;
  }[];
}

/**
 * Lấy thống kê tổng quan hệ thống
 * @returns Promise với dữ liệu thống kê
 */
export const getSystemStatistics = async (): Promise<ApiResponse<SystemStatistics>> => {
  try {
    const response = await api.get<ApiResponse<SystemStatistics>>('/admin/system/statistics');
    return response.data;
  } catch (error) {
    console.error('Error fetching system statistics:', error);
    throw error;
  }
};

/**
 * Lấy hoạt động người dùng gần đây
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @param sortBy Sắp xếp theo (lastActiveAt, postsCount, followersCount)
 * @returns Promise với danh sách hoạt động người dùng
 */
export const getUserActivities = async (
  page = 0, 
  size = 20, 
  sortBy: 'lastActiveAt' | 'postsCount' | 'followersCount' = 'lastActiveAt'
): Promise<ApiResponse<{
  content: UserActivity[];
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}>> => {
  try {
    const response = await api.get<ApiResponse<{
      content: UserActivity[];
      totalElements: number;
      totalPages: number;
      last: boolean;
      first: boolean;
    }>>(`/admin/system/user-activities?page=${page}&size=${size}&sort=${sortBy},desc`);
    return response.data;
  } catch (error) {
    console.error('Error fetching user activities:', error);
    throw error;
  }
};

/**
 * Lấy nội dung cần kiểm duyệt
 * @returns Promise với dữ liệu nội dung cần kiểm duyệt
 */
export const getContentModeration = async (): Promise<ApiResponse<ContentModeration>> => {
  try {
    const response = await api.get<ApiResponse<ContentModeration>>('/admin/system/content-moderation');
    return response.data;
  } catch (error) {
    console.error('Error fetching content moderation data:', error);
    throw error;
  }
};

/**
 * Lấy thống kê theo thời gian
 * @param startDate Ngày bắt đầu (YYYY-MM-DD)
 * @param endDate Ngày kết thúc (YYYY-MM-DD)
 * @returns Promise với dữ liệu thống kê theo thời gian
 */
export const getStatisticsByDateRange = async (
  startDate: string, 
  endDate: string
): Promise<ApiResponse<{
  dates: string[];
  newUsers: number[];
  newPosts: number[];
  newComments: number[];
  newLikes: number[];
  newFollows: number[];
}>> => {
  try {
    const response = await api.get<ApiResponse<{
      dates: string[];
      newUsers: number[];
      newPosts: number[];
      newComments: number[];
      newLikes: number[];
      newFollows: number[];
    }>>(`/admin/system/statistics/date-range?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching statistics for date range ${startDate} to ${endDate}:`, error);
    throw error;
  }
};

/**
 * Backup hệ thống
 * @returns Promise với thông tin backup
 */
export const createSystemBackup = async (): Promise<ApiResponse<{
  backupId: string;
  fileName: string;
  fileSize: number;
  createdAt: string;
  downloadUrl: string;
}>> => {
  try {
    const response = await api.post<ApiResponse<{
      backupId: string;
      fileName: string;
      fileSize: number;
      createdAt: string;
      downloadUrl: string;
    }>>('/admin/system/backup');
    return response.data;
  } catch (error) {
    console.error('Error creating system backup:', error);
    throw error;
  }
};

/**
 * Lấy danh sách backup
 * @returns Promise với danh sách backup
 */
export const getSystemBackups = async (): Promise<ApiResponse<{
  backupId: string;
  fileName: string;
  fileSize: number;
  createdAt: string;
  downloadUrl: string;
}[]>> => {
  try {
    const response = await api.get<ApiResponse<{
      backupId: string;
      fileName: string;
      fileSize: number;
      createdAt: string;
      downloadUrl: string;
    }[]>>('/admin/system/backups');
    return response.data;
  } catch (error) {
    console.error('Error fetching system backups:', error);
    throw error;
  }
};
