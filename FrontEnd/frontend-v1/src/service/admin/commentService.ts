// src/service/admin/commentService.ts
import api from './api';

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

/**
 * Xóa bình luận (Admin only)
 * @param commentId ID của bình luận
 * @returns Promise với kết quả xóa
 */
export const deleteComment = async (commentId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/admin/comments/${commentId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting comment ${commentId}:`, error);
    throw error;
  }
};
