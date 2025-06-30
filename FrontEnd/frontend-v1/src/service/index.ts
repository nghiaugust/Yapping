// src/service/index.ts
// Central export file for all service functions

// User services
export * from './user/userService';
export * from './user/postService';
export * from './user/commentService';
export * from './user/followService';
export * from './user/likeService';
export * from './user/notificationService';
export * from './user/repostService';
export * from './user/categoryService';
export * from './user/resourceService';
export * from './user/mentionService';
export * from './user/bookmarkService';
export * from './user/hashtagService';

// User report service (with specific exports to avoid conflicts)
export { 
  createReport as createUserReport,
  getUserReports,
  cancelReport 
} from './user/reportService';

// Admin services (with aliases to avoid conflicts)
export { 
  getUsers as getAllUsers,
  getUserById as getAdminUserById,
  createUser as createAdminUser,
  updateUser as updateAdminUser 
} from './admin/userService';

export { 
  getAllReports,
  getReportById as getAdminReportById,
  updateReportStatus,
  deleteReport as deleteAdminReport,
  getReportStatistics,
  bulkUpdateReports 
} from './admin/reportService';

export * from './admin/systemService';

// Core API client
export { default as api } from './admin/api';
