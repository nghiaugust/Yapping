// src/service/user/resourceService.ts
import api from '../admin/api';
import { Resource, ResourceWithDetails, CreateResourceRequest, ResourcePageResponse, ApiResponse } from '../../types';

/**
 * Lấy danh sách tài nguyên công khai
 * @param page Số trang (bắt đầu từ 0)
 * @param size Số lượng tài nguyên trên mỗi trang
 * @param categoryId ID category để lọc (optional)
 * @param subCategoryId ID subcategory để lọc (optional)
 * @returns Promise với dữ liệu tài nguyên phân trang
 */
export const getPublicResources = async (
  page = 0, 
  size = 20, 
  categoryId?: number, 
  subCategoryId?: number
): Promise<ApiResponse<ResourcePageResponse>> => {
  try {
    let url = `/resources?page=${page}&size=${size}&sort=createdAt,desc`;
    if (categoryId) url += `&categoryId=${categoryId}`;
    if (subCategoryId) url += `&subCategoryId=${subCategoryId}`;
    
    const response = await api.get<ApiResponse<ResourcePageResponse>>(url);
    return response.data;
  } catch (error) {
    console.error('Error fetching public resources:', error);
    throw error;
  }
};

/**
 * Lấy chi tiết tài nguyên theo ID
 * @param resourceId ID của tài nguyên
 * @returns Promise với thông tin chi tiết tài nguyên
 */
export const getResourceById = async (resourceId: number): Promise<ApiResponse<ResourceWithDetails>> => {
  try {
    const response = await api.get<ApiResponse<ResourceWithDetails>>(`/resources/${resourceId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching resource ${resourceId}:`, error);
    throw error;
  }
};

/**
 * Tạo tài nguyên mới
 * @param data Dữ liệu tài nguyên mới
 * @returns Promise với tài nguyên đã tạo
 */
export const createResource = async (data: CreateResourceRequest): Promise<ApiResponse<Resource>> => {
  try {
    const response = await api.post<ApiResponse<Resource>>('/resources', data);
    return response.data;
  } catch (error) {
    console.error('Error creating resource:', error);
    throw error;
  }
};

/**
 * Cập nhật tài nguyên
 * @param resourceId ID của tài nguyên
 * @param data Dữ liệu cập nhật
 * @returns Promise với tài nguyên đã cập nhật
 */
export const updateResource = async (resourceId: number, data: Partial<CreateResourceRequest>): Promise<ApiResponse<Resource>> => {
  try {
    const response = await api.patch<ApiResponse<Resource>>(`/resources/${resourceId}`, data);
    return response.data;
  } catch (error) {
    console.error(`Error updating resource ${resourceId}:`, error);
    throw error;
  }
};

/**
 * Xóa tài nguyên
 * @param resourceId ID của tài nguyên
 * @returns Promise với response
 */
export const deleteResource = async (resourceId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/resources/${resourceId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting resource ${resourceId}:`, error);
    throw error;
  }
};

/**
 * Lấy danh sách tài nguyên của user
 * @param userId ID của user
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @returns Promise với danh sách tài nguyên của user
 */
export const getUserResources = async (userId: number, page = 0, size = 20): Promise<ApiResponse<ResourcePageResponse>> => {
  try {
    const response = await api.get<ApiResponse<ResourcePageResponse>>(`/users/${userId}/resources?page=${page}&size=${size}&sort=createdAt,desc`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching resources for user ${userId}:`, error);
    throw error;
  }
};

/**
 * Tăng download count cho tài nguyên
 * @param resourceId ID của tài nguyên
 * @returns Promise với response
 */
export const incrementDownloadCount = async (resourceId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.post<ApiResponse<void>>(`/resources/${resourceId}/download`);
    return response.data;
  } catch (error) {
    console.error(`Error incrementing download count for resource ${resourceId}:`, error);
    throw error;
  }
};

/**
 * Tìm kiếm tài nguyên
 * @param keyword Từ khóa tìm kiếm
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @param categoryId ID category để lọc (optional)
 * @returns Promise với kết quả tìm kiếm
 */
export const searchResources = async (
  keyword: string, 
  page = 0, 
  size = 20, 
  categoryId?: number
): Promise<ApiResponse<ResourcePageResponse>> => {
  try {
    let url = `/resources/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`;
    if (categoryId) url += `&categoryId=${categoryId}`;
    
    const response = await api.get<ApiResponse<ResourcePageResponse>>(url);
    return response.data;
  } catch (error) {
    console.error(`Error searching resources with keyword "${keyword}":`, error);
    throw error;
  }
};
