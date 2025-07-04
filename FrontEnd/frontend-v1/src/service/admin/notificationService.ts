// src/service/admin/notificationService.ts
import api from './api';

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

export interface CreateNotificationRequest {
  userId: number;
  actorId?: number;
  type: 'LIKE_POST' | 'LIKE_COMMENT' | 'COMMENT' | 'REPLY_POST' | 'REPLY_COMMENT' | 'FOLLOW' | 'MENTION_POST' | 'MENTION_COMMENT' | 'REPOST' | 'NEW_POST' | 'SYSTEM' | 'SHARE_POST';
  targetType: 'POST' | 'COMMENT' | 'USER';
  targetId?: number;
  targetOwnerId?: number;
  message?: string;
}

export interface NotificationDTO {
  id: number;
  userId: number;
  actorId?: number;
  type: string;
  targetType: string;
  targetId?: number;
  targetOwnerId?: number;
  isRead: boolean;
  message?: string;
  createdAt: string;
}

/**
 * Tạo thông báo mới
 * @param data Dữ liệu thông báo
 * @returns Promise với thông báo đã tạo
 */
export const createNotification = async (data: CreateNotificationRequest): Promise<ApiResponse<NotificationDTO>> => {
  try {
    const response = await api.post<ApiResponse<NotificationDTO>>('/notifications', data);
    return response.data;
  } catch (error) {
    console.error('Error creating notification:', error);
    throw error;
  }
};

/**
 * Lấy thông báo theo ID
 * @param notificationId ID thông báo
 * @returns Promise với thông tin thông báo
 */
export const getNotificationById = async (notificationId: number): Promise<ApiResponse<NotificationDTO>> => {
  try {
    const response = await api.get<ApiResponse<NotificationDTO>>(`/notifications/${notificationId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching notification:', error);
    throw error;
  }
};

/**
 * Xóa thông báo
 * @param notificationId ID thông báo
 * @returns Promise với kết quả xóa
 */
export const deleteNotification = async (notificationId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/notifications/${notificationId}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting notification:', error);
    throw error;
  }
};
