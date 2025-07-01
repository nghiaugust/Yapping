// src/service/user/categoryService.ts
import api from '../admin/api';

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  createdAt: string;
}

export interface Subcategory {
  id: number;
  categoryId: number;
  categoryName: string;
  name: string;
  description?: string;
  createdAt: string;
}

/**
 * Lấy tất cả categories
 */
export const getAllCategories = async (): Promise<ApiResponse<Category[]>> => {
  try {
    const response = await api.get<ApiResponse<Category[]>>('/categories');
    return response.data;
  } catch (error) {
    console.error('Error fetching categories:', error);
    throw error;
  }
};

/**
 * Lấy subcategories theo categoryId
 */
export const getSubcategoriesByCategoryId = async (categoryId: number): Promise<ApiResponse<Subcategory[]>> => {
  try {
    const response = await api.get<ApiResponse<Subcategory[]>>(`/subcategories/category/${categoryId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching subcategories:', error);
    throw error;
  }
};
