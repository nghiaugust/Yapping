// src/types/user.ts
export interface Role {
    id?: number;
    name: "ADMIN" | "USER";
  }
  
export interface User {
    id: number;
    username: string;
    email: string;
    password?: string | null;
    fullName: string;
    bio?: string;
    profilePicture?: string | null;
    isVerified: boolean;
    status: "ACTIVE" | "SUSPENDED" | "DELETED" | "PENDING_VERIFICATION";
    createdAt: string;
    updatedAt: string;
    roles: Role[];
  }

export interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

export interface PageResponse {
  content: any[];
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