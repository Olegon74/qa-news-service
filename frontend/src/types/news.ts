export type NewsStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
export type NewsStatusFilter = 'ALL' | NewsStatus;

export interface NewsRequest {
  title: string;
  previewText: string;
  fullText: string;
  author: string;
}

export interface NewsResponse {
  id: number;
  title: string;
  previewText: string;
  fullText: string;
  author: string;
  status: NewsStatus;
  createdAt: string;
  updatedAt: string;
  publishedAt: string | null;
  archivedAt: string | null;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}
