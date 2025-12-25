import api from './api';
import { ApiResponse, Food, FoodCategory } from '../types/health';

export const foodService = {
  getAllFoods: async (): Promise<Food[]> => {
    const response = await api.get<ApiResponse<Food[]>>('/foods/all');
    return response.data.result;
  },

  getFoodById: async (id: number): Promise<Food> => {
    const response = await api.get<ApiResponse<Food>>(`/foods/${id}`);
    return response.data.result;
  },

  createFood: async (formData: FormData): Promise<Food> => {
    const response = await api.post<ApiResponse<Food>>('/foods/create', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data.result;
  },

  updateFood: async (id: number, formData: FormData): Promise<Food> => {
    const response = await api.put<ApiResponse<Food>>(`/foods/${id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data.result;
  },

  deleteFood: async (id: number): Promise<void> => {
    await api.delete(`/foods/${id}`);
  },

  // Food Categories
  getAllCategories: async (): Promise<FoodCategory[]> => {
    const response = await api.get<ApiResponse<FoodCategory[]>>('/categories/all');
    return response.data.result;
  },

  createCategory: async (data: { categoryName: string; description?: string }): Promise<FoodCategory> => {
    const response = await api.post<ApiResponse<FoodCategory>>('/categories/create', data);
    return response.data.result;
  },

  updateCategory: async (id: number, data: { categoryName: string; description?: string }): Promise<FoodCategory> => {
    const response = await api.put<ApiResponse<FoodCategory>>(`/categories/${id}`, data);
    return response.data.result;
  },

  deleteCategory: async (id: number): Promise<void> => {
    await api.delete(`/categories/${id}`);
  },
};
