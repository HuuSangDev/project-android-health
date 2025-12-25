import api from './api';
import { ApiResponse, User, DailyLog } from '../types/health';

export const userService = {
  // Get all users (Admin only)
  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get<ApiResponse<User[]>>('/Users/all');
    return response.data.result;
  },

  // Get user by ID
  getUserById: async (id: number): Promise<User> => {
    const response = await api.get<ApiResponse<User>>(`/Users/${id}`);
    return response.data.result;
  },

  // Get current user profile
  getCurrentUserProfile: async (): Promise<User> => {
    const response = await api.get<ApiResponse<User>>('/userProfile/get-my-profile');
    return response.data.result;
  },

  // Update user profile
  updateUserProfile: async (formData: FormData): Promise<User> => {
    const response = await api.put<ApiResponse<User>>('/userProfile/update', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data.result;
  },

  // Delete user (Admin only)
  deleteUser: async (id: number): Promise<void> => {
    await api.delete(`/Users/${id}`);
  },

  // Get user daily logs
  getUserDailyLogs: async (userId: number): Promise<DailyLog[]> => {
    const response = await api.get<ApiResponse<DailyLog[]>>(`/daily-logs/user/${userId}`);
    return response.data.result;
  },

  // Get last 7 days logs
  getLast7DaysLogs: async (): Promise<DailyLog[]> => {
    const response = await api.get<ApiResponse<DailyLog[]>>('/daily-logs/last-7-days');
    return response.data.result;
  },

  // Get today's log
  getTodayLog: async (): Promise<DailyLog> => {
    const response = await api.get<ApiResponse<DailyLog>>('/daily-logs/today');
    return response.data.result;
  },

  // Create or update daily log
  createOrUpdateDailyLog: async (logData: Partial<DailyLog>): Promise<DailyLog> => {
    const response = await api.post<ApiResponse<DailyLog>>('/daily-logs/create-or-update', logData);
    return response.data.result;
  }
};