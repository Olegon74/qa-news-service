import axios from 'axios';
import type { LoginResponse } from '../types/auth';

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080').replace(/\/$/, '');

export async function login(username: string, password: string): Promise<LoginResponse> {
  const response = await axios.post<LoginResponse>(`${API_BASE_URL}/api/auth/login`, {
    username,
    password
  });
  return response.data;
}
