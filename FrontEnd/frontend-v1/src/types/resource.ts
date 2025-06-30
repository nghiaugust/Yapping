// src/types/resource.ts
// Đồng bộ với Resource.java và PostWithResourceDTO.java

export interface Resource {
  id: number;
  postId: number;
  userId: number;
  title: string;
  description?: string;
  driveLink: string;
  categoryId?: number;
  subCategoryId?: number;
  downloadCount?: number;
  createdAt: string;
  updatedAt: string;
}

export interface ResourceWithDetails extends Resource {
  category?: {
    id: number;
    name: string;
  };
  subCategory?: {
    id: number;
    name: string;
  };
  user?: {
    id: number;
    username: string;
    fullName: string;
  };
}

export interface CreateResourceRequest {
  title: string;
  description?: string;
  driveLink: string;
  categoryId: number;
  subCategoryId?: number;
}

export interface ResourcePageResponse {
  content: ResourceWithDetails[];
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
