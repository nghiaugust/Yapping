// src/types/report.ts
// Đồng bộ với Report.java

export interface Report {
  id: number;
  reporterId: number;
  reporterUsername?: string;
  targetType: 'POST' | 'COMMENT';
  targetId: number;
  reason: 'SPAM' | 'HARASSMENT' | 'HATE_SPEECH' | 'INAPPROPRIATE_CONTENT' | 'INTELLECTUAL_PROPERTY' | 'MISINFORMATION' | 'OTHER';
  description?: string;
  status: 'PENDING' | 'REVIEWING' | 'RESOLVED_ACTION_TAKEN' | 'RESOLVED_NO_ACTION';
  createdAt: string;
  updatedAt: string;
}

export interface ReportWithDetails extends Report {
  targetContent?: string;
  targetAuthorId?: number;
  targetAuthorUsername?: string;
  adminNotes?: string;
}

export interface CreateReportRequest {
  targetType: 'POST' | 'COMMENT';
  targetId: number;
  reason: 'SPAM' | 'HARASSMENT' | 'HATE_SPEECH' | 'INAPPROPRIATE_CONTENT' | 'INTELLECTUAL_PROPERTY' | 'MISINFORMATION' | 'OTHER';
  description?: string;
}

export interface UpdateReportRequest {
  status: 'PENDING' | 'REVIEWING' | 'RESOLVED_ACTION_TAKEN' | 'RESOLVED_NO_ACTION';
  adminNotes?: string;
}

export interface ReportPageResponse {
  content: Report[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}
