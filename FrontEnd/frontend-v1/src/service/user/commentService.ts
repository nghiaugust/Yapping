// src/service/user/commentService.ts
import api from '../admin/api';
import { Comment, CommentPageResponse, CreateCommentRequest, ApiResponse } from '../../types/comment';

// Lấy danh sách bình luận của một bài viết
export const getPostComments = async (
  postId: number,
  page = 0,
  size = 10
): Promise<ApiResponse<CommentPageResponse>> => {
  try {
    console.log(`Fetching comments for post ${postId}, page ${page}, size ${size}`);
    const response = await api.get(
      `/comments/post/${postId}?page=${page}&size=${size}&sort=createdAt,desc`
    );
    
    console.log('Raw API response:', response);
    console.log('Response data:', response.data);
    
    // Kiểm tra cấu trúc response
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    if (response.data?.success) {
      // Response có cấu trúc: { status, success, message, data }
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      console.log('Response data.data:', response.data.data);
      return {
        success: true,
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        message: response.data.message ?? 'Lấy danh sách bình luận thành công',
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-member-access
        data: response.data.data
      };
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    } else if (response.data?.content !== undefined) {
      // Response trực tiếp là pagination data
      console.log('Direct pagination response:', response.data);
      return {
        success: true,
        message: 'Lấy danh sách bình luận thành công',
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        data: response.data
      };
    } else {
      console.error('Unexpected response structure:', response.data);
      return {
        success: false,
        message: 'Cấu trúc response không hợp lệ',
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          last: true,
          first: true,
          size: size,
          number: page,
          numberOfElements: 0,
          empty: true
        } as CommentPageResponse
      };
    }
  } catch (error) {
    console.error('Lỗi khi lấy danh sách bình luận:', error);
    return {
      success: false,
      message: 'Không thể lấy danh sách bình luận',
      data: {
        content: [],
        totalElements: 0,
        totalPages: 0,
        last: true,
        first: true,
        size: size,
        number: page,
        numberOfElements: 0,
        empty: true
      } as CommentPageResponse
    };
  }
};

// Tạo bình luận mới
export const createComment = async (
  commentData: CreateCommentRequest
): Promise<ApiResponse<Comment>> => {
  try {
    const response = await api.post<Comment>('/comments', commentData);
    
    return {
      success: true,
      message: 'Tạo bình luận thành công',
      data: response.data
    };
  } catch (error) {
    console.error('Lỗi khi tạo bình luận:', error);
    return {
      success: false,
      message: 'Không thể tạo bình luận',
      data: {} as Comment
    };
  }
};

// Thích/bỏ thích bình luận
export const likeComment = async (commentId: number): Promise<ApiResponse<void>> => {
  try {
    await api.post(`/comments/${commentId}/like`);
    
    return {
      success: true,
      message: 'Thực hiện thành công',
      data: undefined
    };
  } catch (error) {
    console.error('Lỗi khi thích bình luận:', error);
    return {
      success: false,
      message: 'Không thể thực hiện',
      data: undefined
    };
  }
};

// Xóa bình luận
export const deleteComment = async (commentId: number): Promise<ApiResponse<void>> => {
  try {
    await api.delete(`/comments/${commentId}`);
    
    return {
      success: true,
      message: 'Xóa bình luận thành công',
      data: undefined
    };
  } catch (error) {
    console.error('Lỗi khi xóa bình luận:', error);
    return {
      success: false,
      message: 'Không thể xóa bình luận',
      data: undefined
    };
  }
};

// Sửa bình luận
export const updateComment = async (
  commentId: number,
  content: string
): Promise<ApiResponse<Comment>> => {
  try {
    const response = await api.put<Comment>(`/comments/${commentId}`, { content });
    
    return {
      success: true,
      message: 'Cập nhật bình luận thành công',
      data: response.data
    };
  } catch (error) {
    console.error('Lỗi khi cập nhật bình luận:', error);
    return {
      success: false,
      message: 'Không thể cập nhật bình luận',
      data: {} as Comment
    };
  }
};
