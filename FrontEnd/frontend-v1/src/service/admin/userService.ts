// src/service/admin/userService.ts
import api from "./api";
import { User, Role } from "../../types/user";

interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}

interface CreateUserData {
  username: string;
  email: string;
  password: string;
  fullName: string;
  bio?: string;
  roles: Role[];
}

interface UpdateUserData {
  fullName?: string;
  roles?: Role[];
  isVerified?: boolean;
  status?: "ACTIVE" | "SUSPENDED" | "DELETED" | "PENDING_VERIFICATION";
}

// Lấy danh sách tài khoản
export const getUsers = async (): Promise<User[]> => {
  const response = await api.get<ApiResponse<User[]>>("/users");
  return response.data.data;
};

// Lấy chi tiết tài khoản
export const getUserById = async (id: number): Promise<User> => {
  const response = await api.get<ApiResponse<User>>(`/users/${id}`);
  return response.data.data;
};

// Tạo tài khoản mới
export const createUser = async (data: CreateUserData): Promise<User> => {
  const response = await api.post<ApiResponse<User>>("/users", data);
  return response.data.data;
};

// Cập nhật tài khoản
export const updateUser = async (id: number, data: UpdateUserData): Promise<User> => {
  const response = await api.patch<ApiResponse<User>>(`/users/${id}`, data);
  return response.data.data;
};

// Xóa tài khoản
export const deleteUser = async (id: number): Promise<void> => {
  await api.delete(`/users/${id}`);
};