import { ApiResponse, PageResponse } from '../../types/user';
import { Post } from '../../types/post';

// Interface for repost-specific data
export interface Repost {
  id: number;
  userId: number;
  postId: number;
  createdAt: string;
  post: Post;
}

export interface RepostPageResponse extends PageResponse {
  content: Repost[];
}

// Interface for API response structure
interface ApiResponseBody<T = unknown> {
  status?: number;
  message?: string;
  data?: T;
}

// Get reposts by user ID with pagination
export const getUserReposts = async (
  userId: number,
  page = 0,
  size = 20
): Promise<ApiResponse<RepostPageResponse>> => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      return {
        status: 401,
        success: false,
        message: 'Token không tồn tại',
        data: {
          content: [],
          pageable: {
            pageNumber: 0,
            pageSize: 0,
            sort: { empty: true, sorted: false, unsorted: true },
            offset: 0,
            paged: false,
            unpaged: true
          },
          last: true,
          totalElements: 0,
          totalPages: 0,
          size: 0,
          number: 0,
          sort: { empty: true, sorted: false, unsorted: true },
          first: true,
          numberOfElements: 0,
          empty: true
        }
      };
    }

    const response = await fetch(
      `http://localhost:8080/yapping/api/reposts/user/${userId}?page=${page}&size=${size}`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      }
    );

    const result = await response.json() as ApiResponseBody<RepostPageResponse>;

    if (!response.ok) {
      return {
        status: response.status,
        success: false,
        message: result.message ?? 'Lỗi khi lấy danh sách bài đăng lại',
        data: {
          content: [],
          pageable: {
            pageNumber: 0,
            pageSize: 0,
            sort: { empty: true, sorted: false, unsorted: true },
            offset: 0,
            paged: false,
            unpaged: true
          },
          last: true,
          totalElements: 0,
          totalPages: 0,
          size: 0,
          number: 0,
          sort: { empty: true, sorted: false, unsorted: true },
          first: true,
          numberOfElements: 0,
          empty: true
        }
      };
    }

    return {
      status: result.status ?? 200,
      success: true,
      message: result.message ?? 'Lấy danh sách bài đăng lại thành công',
      data: result.data ?? {
        content: [],
        pageable: {
          pageNumber: 0,
          pageSize: 0,
          sort: { empty: true, sorted: false, unsorted: true },
          offset: 0,
          paged: false,
          unpaged: true
        },
        last: true,
        totalElements: 0,
        totalPages: 0,
        size: 0,
        number: 0,
        sort: { empty: true, sorted: false, unsorted: true },
        first: true,
        numberOfElements: 0,
        empty: true
      }
    };

  } catch (error) {
    console.error('Error fetching user reposts:', error);
    return {
      status: 500,
      success: false,
      message: 'Đã xảy ra lỗi khi lấy danh sách bài đăng lại',
      data: {
        content: [],
        pageable: {
          pageNumber: 0,
          pageSize: 0,
          sort: { empty: true, sorted: false, unsorted: true },
          offset: 0,
          paged: false,
          unpaged: true
        },
        last: true,
        totalElements: 0,
        totalPages: 0,
        size: 0,
        number: 0,
        sort: { empty: true, sorted: false, unsorted: true },
        first: true,
        numberOfElements: 0,
        empty: true
      }
    };
  }
};

// Create a repost
export const createRepost = async (postId: number): Promise<ApiResponse<Repost | null>> => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      return {
        status: 401,
        success: false,
        message: 'Token không tồn tại',
        data: null
      };
    }

    const response = await fetch(
      `http://localhost:8080/yapping/api/reposts`,
      {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ postId })
      }
    );

    const result = await response.json() as ApiResponseBody<Repost>;

    if (!response.ok) {
      return {
        status: response.status,
        success: false,
        message: result.message ?? 'Lỗi khi tạo bài đăng lại',
        data: null
      };
    }

    return {
      status: result.status ?? 200,
      success: true,
      message: result.message ?? 'Tạo bài đăng lại thành công',
      data: result.data ?? null
    };

  } catch (error) {
    console.error('Error creating repost:', error);
    return {
      status: 500,
      success: false,
      message: 'Đã xảy ra lỗi khi tạo bài đăng lại',
      data: null
    };
  }
};

// Delete a repost
export const deleteRepost = async (repostId: number): Promise<ApiResponse<null>> => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      return {
        status: 401,
        success: false,
        message: 'Token không tồn tại',
        data: null
      };
    }

    const response = await fetch(
      `http://localhost:8080/yapping/api/reposts/${repostId}`,
      {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      }
    );

    const result = await response.json() as ApiResponseBody<null>;

    if (!response.ok) {
      return {
        status: response.status,
        success: false,
        message: result.message ?? 'Lỗi khi xóa bài đăng lại',
        data: null
      };
    }

    return {
      status: result.status ?? 200,
      success: true,
      message: result.message ?? 'Xóa bài đăng lại thành công',
      data: null
    };

  } catch (error) {
    console.error('Error deleting repost:', error);
    return {
      status: 500,
      success: false,
      message: 'Đã xảy ra lỗi khi xóa bài đăng lại',
      data: null
    };
  }
};
