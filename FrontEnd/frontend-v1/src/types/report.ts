// src/types/report.ts
// Đồng bộ với Report.java

export interface Report {
  id: number;
  reporterId: number;
  reportedUserId?: number;
  postId?: number;
  commentId?: number;
  reason: string;
  description?: string;
  status: "PENDING" | "REVIEWED" | "RESOLVED" | "DISMISSED";
  createdAt: string;
  updatedAt: string;
}

export interface ReportWithDetails extends Report {
  reporter: {
    id: number;
    username: string;
    fullName: string;
    profilePicture?: string;
  };
  reportedUser?: {
    id: number;
    username: string;
    fullName: string;
    profilePicture?: string;
  };
  post?: {
    id: number;
    content: string;
    user: {
      id: number;
      username: string;
      fullName: string;
    };
  };
  comment?: {
    id: number;
    content: string;
    user: {
      id: number;
      username: string;
      fullName: string;
    };
  };
}

export interface CreateReportRequest {
  reportedUserId?: number;
  postId?: number;
  commentId?: number;
  reason: string;
  description?: string;
}

export interface UpdateReportRequest {
  status: "PENDING" | "REVIEWED" | "RESOLVED" | "DISMISSED";
  adminNote?: string;
}

export interface ReportPageResponse {
  content: ReportWithDetails[];
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
