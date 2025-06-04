// src/utils/formatDate.ts
import { formatDistanceToNow, format } from 'date-fns';
import { vi } from 'date-fns/locale';

export const formatDateString = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

/**
 * Định dạng thời gian hiển thị dạng "x thời gian trước"
 * @param dateString Chuỗi ngày tháng cần định dạng
 * @returns Chuỗi đã định dạng
 */
export const formatTimeAgo = (dateString: string): string => {
  try {
    return formatDistanceToNow(new Date(dateString), { addSuffix: true, locale: vi });
  } catch (e: unknown) {
    if (e instanceof Error) {
      console.error('Lỗi định dạng thời gian:', e.message);
    } else {
      console.error('Lỗi định dạng thời gian không xác định');
    }
    return dateString;
  }
};

/**
 * Định dạng ngày tháng theo mẫu cụ thể
 * @param dateString Chuỗi ngày tháng cần định dạng
 * @param pattern Mẫu định dạng (mặc định: 'dd/MM/yyyy HH:mm')
 * @returns Chuỗi đã định dạng
 */
export const formatDateTime = (dateString: string, pattern = 'dd/MM/yyyy HH:mm'): string => {
  try {
    return format(new Date(dateString), pattern, { locale: vi });
  } catch (e: unknown) {
    if (e instanceof Error) {
      console.error('Lỗi định dạng thời gian:', e.message);
    } else {
      console.error('Lỗi định dạng thời gian không xác định');
    }
    return dateString;
  }
};