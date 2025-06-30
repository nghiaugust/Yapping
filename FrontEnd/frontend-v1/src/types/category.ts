// src/types/category.ts
// Đồng bộ với Category.java và Subcategory.java

export interface Category {
  id: number;
  name: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Subcategory {
  id: number;
  categoryId: number;
  name: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
  category?: Category; // Optional populated field
}

export interface CategoryWithSubcategories extends Category {
  subcategories: Subcategory[];
}

export interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}
