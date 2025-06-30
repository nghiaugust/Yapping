// src/types/user.ts
export interface Role {
    id?: number;
    name: string; // Thay đổi từ "ADMIN" | "USER" thành string để linh hoạt hơn
  }
  
export interface User {
    id: number;
    username: string;
    email: string;
    password?: string | null; // Chỉ dùng khi tạo/cập nhật
    fullName: string;
    bio?: string;
    profilePicture?: string | null;
    isVerified: boolean;
    status: "ACTIVE" | "SUSPENDED" | "DELETED" | "PENDING_VERIFICATION";
    createdAt: string; // ISO string format từ Instant
    updatedAt: string; // ISO string format từ Instant
    roles: Role[];
  }

export interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

export interface PageResponse {
  content: unknown[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}