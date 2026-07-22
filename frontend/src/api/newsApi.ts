import axios from 'axios';
import { getToken } from '../auth/authStorage';
import type { NewsRequest, NewsResponse, NewsStatus } from '../types/news';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

const newsClient = axios.create({
  baseURL: API_BASE_URL
});

newsClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export async function createNews(request: NewsRequest): Promise<NewsResponse> {
  const response = await newsClient.post<NewsResponse>('/api/news', request);
  return response.data;
}

export async function getNewsById(id: number): Promise<NewsResponse> {
  const response = await newsClient.get<NewsResponse>(`/api/news/${id}`);
  return response.data;
}

export async function getAllNews(status?: NewsStatus): Promise<NewsResponse[]> {
  const response = await newsClient.get<NewsResponse[]>('/api/news', {
    params: status ? { status } : undefined
  });
  return response.data;
}

export async function updateNews(id: number, request: NewsRequest): Promise<NewsResponse> {
  const response = await newsClient.put<NewsResponse>(`/api/news/${id}`, request);
  return response.data;
}

export async function publishNews(id: number): Promise<NewsResponse> {
  const response = await newsClient.post<NewsResponse>(`/api/news/${id}/publish`);
  return response.data;
}

export async function archiveNews(id: number): Promise<NewsResponse> {
  const response = await newsClient.post<NewsResponse>(`/api/news/${id}/archive`);
  return response.data;
}

export async function deleteNews(id: number): Promise<void> {
  await newsClient.delete(`/api/news/${id}`);
}
