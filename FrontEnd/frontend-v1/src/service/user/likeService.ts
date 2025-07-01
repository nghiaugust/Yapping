// src/service/user/likeService.ts
import api from '../admin/api';
import { Like, LikePageResponse, ApiResponse } from '../../types/like';

// Lấy danh sách lượt thích của người dùng hiện tại
export const getUserLikes = async (
  userId: number,
  page = 0,
  size = 100 // Lấy nhiều để có đủ dữ liệu cho việc check
): Promise<ApiResponse<LikePageResponse>> => {
  try {
    const response = await api.get(
      `/likes/user/${userId}?page=${page}&size=${size}`
    );
    
    if (response.data?.success) {
      return {
        success: true,
        message: response.data.message ?? 'Lấy danh sách lượt thích thành công',
        data: response.data.data
      };
    } else {
      return {
        success: false,
        message: 'Không thể lấy danh sách lượt thích',
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          pageable: {
            pageNumber: 0,
            pageSize: size,
            sort: { empty: true, sorted: false, unsorted: true },
            offset: 0,
            paged: true,
            unpaged: false
          },
          last: true,
          size: size,
          number: 0,
          sort: { empty: true, sorted: false, unsorted: true },
          numberOfElements: 0,
          first: true,
          empty: true
        }
      };
    }
  } catch (error) {
    console.error('Lỗi khi lấy danh sách lượt thích:', error);
    return {
      success: false,
      message: 'Lỗi khi lấy danh sách lượt thích',
      data: {
        content: [],
        totalElements: 0,
        totalPages: 0,
        pageable: {
          pageNumber: 0,
          pageSize: size,
          sort: { empty: true, sorted: false, unsorted: true },
          offset: 0,
          paged: true,
          unpaged: false
        },
        last: true,
        size: size,
        number: 0,
        sort: { empty: true, sorted: false, unsorted: true },
        numberOfElements: 0,
        first: true,
        empty: true
      }
    };
  }
};

// Xóa lượt thích (unlike)
export const deleteLike = async (likeId: number): Promise<ApiResponse<null>> => {
  try {
    const response = await api.delete(`/likes/${likeId}`);
    
    return {
      success: Boolean((response.data as { success?: boolean })?.success),
      message: String((response.data as { message?: string })?.message ?? 'Đã bỏ thích thành công'),
      data: null
    };
  } catch (error) {
    console.error('Lỗi khi bỏ thích:', error);
    return {
      success: false,
      message: 'Không thể bỏ thích',
      data: null
    };
  }
};
