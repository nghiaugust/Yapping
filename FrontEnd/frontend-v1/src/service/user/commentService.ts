// src/service/user/commentService.ts
import api from '../admin/api';
import { Comment, CommentPageResponse, CreateCommentRequest, UpdateCommentRequest, ApiResponse } from '../../types';

// Lấy danh sách bình luận của một bài viết
export const getPostComments = async (
  postId: number,
  page = 0,
  size = 10
): Promise<ApiResponse<CommentPageResponse>> => {
  try {
    console.log(`Fetching comments for post ${postId}, page ${page}, size ${size}`);
    const response = await api.get<ApiResponse<CommentPageResponse>>(
      `/comments/post/${postId}?page=${page}&size=${size}&sort=createdAt,desc`
    );
    
    console.log('Raw API response:', response);
    return response.data;
  } catch (error) {
    console.error(`Error fetching comments for post ${postId}:`, error);
    throw error;
  }
};

// Tạo bình luận mới
export const createComment = async (commentData: CreateCommentRequest): Promise<ApiResponse<Comment>> => {
  try {
    const response = await api.post<ApiResponse<Comment>>('/comments', commentData);
    return response.data;
  } catch (error) {
    console.error('Error creating comment:', error);
    throw error;
  }
};

// Cập nhật bình luận
export const updateComment = async (commentId: number, commentData: UpdateCommentRequest): Promise<ApiResponse<Comment>> => {
  try {
    const response = await api.put<ApiResponse<Comment>>(`/comments/${commentId}`, commentData);
    return response.data;
  } catch (error) {
    console.error(`Error updating comment ${commentId}:`, error);
    throw error;
  }
};

// Xóa bình luận
export const deleteComment = async (commentId: number): Promise<ApiResponse<null>> => {
  try {
    const response = await api.delete<ApiResponse<null>>(`/comments/${commentId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting comment ${commentId}:`, error);
    throw error;
  }
};

// Like bình luận
export const likeComment = async (commentId: number): Promise<ApiResponse<Comment>> => {
  try {
    const response = await api.post<ApiResponse<Comment>>(`/comments/${commentId}/like`);
    return response.data;
  } catch (error) {
    console.error(`Error liking comment ${commentId}:`, error);
    throw error;
  }
};

// Unlike bình luận
export const unlikeComment = async (commentId: number): Promise<ApiResponse<Comment>> => {
  try {
    const response = await api.delete<ApiResponse<Comment>>(`/comments/${commentId}/like`);
    return response.data;
  } catch (error) {
    console.error(`Error unliking comment ${commentId}:`, error);
    throw error;
  }
};

// Lấy reply của một comment
export const getCommentReplies = async (
  commentId: number,
  page = 0,
  size = 10
): Promise<ApiResponse<CommentPageResponse>> => {
  try {
    const response = await api.get<ApiResponse<CommentPageResponse>>(
      `/comments/${commentId}/replies?page=${page}&size=${size}&sort=createdAt,asc`
    );
    return response.data;
  } catch (error) {
    console.error(`Error fetching replies for comment ${commentId}:`, error);
    throw error;
  }
};
