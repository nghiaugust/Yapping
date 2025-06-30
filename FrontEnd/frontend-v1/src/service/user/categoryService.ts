// src/service/user/categoryService.ts
import api from '../admin/api';
import { Category, Subcategory, CategoryWithSubcategories, ApiResponse } from '../../types';

/**
 * Lấy danh sách tất cả categories
 * @returns Promise với danh sách categories
 */
export const getCategories = async (): Promise<ApiResponse<Category[]>> => {
  try {
    const response = await api.get<ApiResponse<Category[]>>('/categories');
    return response.data;
  } catch (error) {
    console.error('Error fetching categories:', error);
    throw error;
  }
};

/**
 * Lấy chi tiết category theo ID
 * @param categoryId ID của category
 * @returns Promise với thông tin category
 */
export const getCategoryById = async (categoryId: number): Promise<ApiResponse<Category>> => {
  try {
    const response = await api.get<ApiResponse<Category>>(`/categories/${categoryId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching category ${categoryId}:`, error);
    throw error;
  }
};

/**
 * Lấy category kèm subcategories
 * @param categoryId ID của category
 * @returns Promise với category và subcategories
 */
export const getCategoryWithSubcategories = async (categoryId: number): Promise<ApiResponse<CategoryWithSubcategories>> => {
  try {
    const response = await api.get<ApiResponse<CategoryWithSubcategories>>(`/categories/${categoryId}/subcategories`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching category with subcategories ${categoryId}:`, error);
    throw error;
  }
};

/**
 * Lấy danh sách subcategories của một category
 * @param categoryId ID của category
 * @returns Promise với danh sách subcategories
 */
export const getSubcategories = async (categoryId: number): Promise<ApiResponse<Subcategory[]>> => {
  try {
    const response = await api.get<ApiResponse<Subcategory[]>>(`/categories/${categoryId}/subcategories`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching subcategories for category ${categoryId}:`, error);
    throw error;
  }
};

/**
 * Lấy chi tiết subcategory theo ID
 * @param subcategoryId ID của subcategory
 * @returns Promise với thông tin subcategory
 */
export const getSubcategoryById = async (subcategoryId: number): Promise<ApiResponse<Subcategory>> => {
  try {
    const response = await api.get<ApiResponse<Subcategory>>(`/subcategories/${subcategoryId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching subcategory ${subcategoryId}:`, error);
    throw error;
  }
};
