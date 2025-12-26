import api from "./api";
import { ApiResponse, Exercise, ExerciseCategory } from "../types/health";

export const exerciseService = {
  getAllExercises: async (): Promise<Exercise[]> => {
    const response = await api.get<ApiResponse<Exercise[]>>(
      "/exercises/all-admin"
    );
    return response.data.result;
  },

  getExerciseById: async (id: number): Promise<Exercise> => {
    const response = await api.get<ApiResponse<Exercise>>(`/exercises/${id}`);
    return response.data.result;
  },

  createExercise: async (formData: FormData): Promise<Exercise> => {
    const response = await api.post<ApiResponse<Exercise>>(
      "/exercises/create",
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );
    return response.data.result;
  },

  updateExercise: async (id: number, formData: FormData): Promise<Exercise> => {
    const response = await api.put<ApiResponse<Exercise>>(
      `/exercises/${id}`,
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );
    return response.data.result;
  },

  deleteExercise: async (id: number): Promise<void> => {
    await api.delete(`/exercises/${id}`);
  },

  // Exercise Categories
  getAllCategories: async (): Promise<ExerciseCategory[]> => {
    const response = await api.get<ApiResponse<ExerciseCategory[]>>(
      "/exercise-categories/all"
    );
    return response.data.result;
  },

  createCategory: async (data: {
    categoryName: string;
    description?: string;
    iconUrl?: string;
  }): Promise<ExerciseCategory> => {
    const response = await api.post<ApiResponse<ExerciseCategory>>(
      "/exercise-categories/create",
      data
    );
    return response.data.result;
  },

  updateCategory: async (
    id: number,
    data: { categoryName: string; description?: string; iconUrl?: string }
  ): Promise<ExerciseCategory> => {
    const response = await api.put<ApiResponse<ExerciseCategory>>(
      `/exercise-categories/${id}`,
      data
    );
    return response.data.result;
  },

  deleteCategory: async (id: number): Promise<void> => {
    await api.delete(`/exercise-categories/${id}`);
  },
};
