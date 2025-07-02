// src/service/admin/reportService.ts
import api from './api';
import { Report, ReportWithDetails, UpdateReportRequest, ReportPageResponse, ApiResponse } from '../../types/report';

/**
 * Lấy danh sách tất cả báo cáo (Admin only)
 * @param page Số trang
 * @param size Số lượng trên mỗi trang
 * @param status Lọc theo trạng thái (optional)
 * @param targetType Lọc theo loại đối tượng (optional)
 * @returns Promise với danh sách báo cáo
 */
export const getAllReports = async (
  page = 0, 
  size = 20, 
  status?: "PENDING" | "REVIEWING" | "RESOLVED_ACTION_TAKEN" | "RESOLVED_NO_ACTION",
  targetType?: "POST" | "COMMENT"
): Promise<ApiResponse<ReportPageResponse>> => {
  try {
    let url = `/reports?page=${page}&size=${size}&sort=createdAt,desc`;
    if (status) url += `&status=${status}`;
    if (targetType) url += `&targetType=${targetType}`;
    
    const response = await api.get<ApiResponse<ReportPageResponse>>(url);
    return response.data;
  } catch (error) {
    console.error('Error fetching all reports:', error);
    throw error;
  }
};

/**
 * Lấy chi tiết báo cáo (Admin only)
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
 * Cập nhật trạng thái báo cáo (Admin only)
 * @param reportId ID của báo cáo
 * @param data Dữ liệu cập nhật
 * @returns Promise với báo cáo đã cập nhật
 */
export const updateReportStatus = async (reportId: number, data: UpdateReportRequest): Promise<ApiResponse<Report>> => {
  try {
    const response = await api.patch<ApiResponse<Report>>(`/reports/${reportId}`, data);
    return response.data;
  } catch (error) {
    console.error(`Error updating report ${reportId}:`, error);
    throw error;
  }
};

/**
 * Xóa báo cáo (Admin only)
 * @param reportId ID của báo cáo
 * @returns Promise với response
 */
export const deleteReport = async (reportId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await api.delete<ApiResponse<void>>(`/reports/${reportId}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting report ${reportId}:`, error);
    throw error;
  }
};

/**
 * Lấy thống kê báo cáo
 * @returns Promise với dữ liệu thống kê
 */
export const getReportStatistics = async (): Promise<ApiResponse<{
  totalReports: number;
  pendingReports: number;
  reviewedReports: number;
  resolvedReports: number;
  dismissedReports: number;
  reportsThisWeek: number;
  reportsThisMonth: number;
}>> => {
  try {
    const response = await api.get<ApiResponse<{
      totalReports: number;
      pendingReports: number;
      reviewedReports: number;
      resolvedReports: number;
      dismissedReports: number;
      reportsThisWeek: number;
      reportsThisMonth: number;
    }>>('/reports/statistics');
    return response.data;
  } catch (error) {
    console.error('Error fetching report statistics:', error);
    throw error;
  }
};

/**
 * Bulk update trạng thái nhiều báo cáo
 * @param reportIds Danh sách ID báo cáo
 * @param status Trạng thái mới
 * @returns Promise với response
 */
export const bulkUpdateReports = async (
  reportIds: number[], 
  status: "PENDING" | "REVIEWED" | "RESOLVED" | "DISMISSED"
): Promise<ApiResponse<{ updatedCount: number }>> => {
  try {
    const response = await api.patch<ApiResponse<{ updatedCount: number }>>('/admin/reports/bulk-update', {
      reportIds,
      status
    });
    return response.data;
  } catch (error) {
    console.error('Error bulk updating reports:', error);
    throw error;
  }
};
