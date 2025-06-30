// src/service/user/reportService.ts
import api from '../admin/api';
import { Report, ReportWithDetails, CreateReportRequest, ReportPageResponse, ApiResponse } from '../../types';

/**
 * Tạo báo cáo mới
 * @param data Dữ liệu báo cáo
 * @returns Promise với báo cáo đã tạo
 */
export const createReport = async (data: CreateReportRequest): Promise<ApiResponse<Report>> => {
  try {
    const response = await api.post<ApiResponse<Report>>('/reports', data);
    return response.data;
  } catch (error) {
    console.error('Error creating report:', error);
    throw error;
  }
};

/**
 * Lấy danh sách báo cáo của user hiện tại
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @returns Promise với danh sách báo cáo
 */
export const getUserReports = async (page = 0, size = 20): Promise<ApiResponse<ReportPageResponse>> => {
  try {
    const response = await api.get<ApiResponse<ReportPageResponse>>(`/reports/my-reports?page=${page}&size=${size}&sort=createdAt,desc`);
    return response.data;
  } catch (error) {
    console.error('Error fetching user reports:', error);
    throw error;
  }
};

/**
 * Lấy chi tiết báo cáo theo ID
 * @param reportId ID của báo cáo
 * @returns Promise với thông tin chi tiết báo cáo
 */
export const getReportById = async (reportId: number): Promise<ApiResponse<ReportWithDetails>> => {
  try {
    const response = await api.get<ApiResponse<ReportWithDetails>>(`/reports/${reportId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching report ${reportId}:`, error);
    throw error;
  }
};

/**
 * Hủy báo cáo (chỉ người tạo mới có thể hủy)
 * @param reportId ID của báo cáo
 * @returns Promise với response
 */
export const cancelReport = async (reportId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/reports/${reportId}`);
    return response.data;
  } catch (error) {
    console.error(`Error canceling report ${reportId}:`, error);
    throw error;
  }
};
