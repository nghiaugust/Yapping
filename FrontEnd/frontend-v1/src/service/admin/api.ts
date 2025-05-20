// src/service/admin/api.ts
import axios, { AxiosResponse, AxiosError } from 'axios';

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

const api = axios.create({
  baseURL: 'http://localhost:8080/yapping/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  <T>(response: AxiosResponse<ApiResponse<T>>) => {
    return response;
  },
  (error: AxiosError) => {
    console.error('API Error:', error.response?.data ?? error.message);
    if (error.response?.status === 401) {
      console.warn('Unauthorized - Token may have expired');
      // You might want to handle token refresh here
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      localStorage.removeItem('user_id');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;