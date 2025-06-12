// src/service/user/notificationService.ts
import api from '../admin/api';
import { NotificationPageResponse } from '../../types/notification';

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

/**
 * Lấy tất cả thông báo của người dùng hiện tại
 */
export const getNotifications = async (
  page = 0, 
  size = 20, 
  sortBy = 'createdAt', 
  direction = 'desc'
): Promise<ApiResponse<NotificationPageResponse>> => {
  try {
    const response = await api.get<ApiResponse<NotificationPageResponse>>(
      `/notifications?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`
    );
    return response.data;
  } catch (error) {
    console.error('Error fetching notifications:', error);
    throw error;
  }
};

/**
 * Đánh dấu tất cả thông báo là đã đọc
 */
export const markAllNotificationsAsRead = async (): Promise<ApiResponse<number>> => {
  try {
    const response = await api.put<ApiResponse<number>>('/notifications/mark-all-read');
    return response.data;
  } catch (error) {
    console.error('Error marking all notifications as read:', error);
    throw error;
  }
};

/**
 * Đếm số thông báo chưa đọc của người dùng hiện tại
 */
export const countUnreadNotifications = async (): Promise<ApiResponse<number>> => {
  try {
    const response = await api.get<ApiResponse<number>>('/notifications/count-unread');
    return response.data;
  } catch (error) {
    console.error('Error counting unread notifications:', error);
    throw error;
  }
};

/**
 * Đánh dấu một thông báo cụ thể là đã đọc
 */
export const markNotificationAsRead = async (notificationId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.put<ApiResponse<void>>(`/notifications/${notificationId}/mark-read`);
    return response.data;
  } catch (error) {
    console.error('Error marking notification as read:', error);
    throw error;
  }
};
