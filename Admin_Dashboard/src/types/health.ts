export interface User {
  id: number;
  fullName: string;
  email: string;
  role?: 'USER' | 'ADMIN';
  createdAt?: string;
  userProfileResponse?: UserProfile;
}

export interface UserProfile {
  id?: number;
  gender?: string;
  dateOfBirth?: string;
  height?: number;
  weight?: number;
  healthGoal?: string;
  activityLevel?: string;
  avatarUrl?: string;
}

export interface Food {
  foodId: number;
  foodName: string;
  caloriesPer100g: number;
  proteinPer100g: number;
  fatPer100g: number;
  fiberPer100g: number;
  sugarPer100g: number;
  imageUrl?: string;
  instructions?: string;
  prepTime: number;
  cookTime: number;
  servings: number;
  difficultyLevel: string;
  mealType: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
  categoryResponse?: FoodCategory;
  goal: string;
  createdAt: string;
}

export interface Exercise {
  exerciseId: number;
  exerciseName: string;
  caloriesPerMinute: number;
  description: string;
  instructions: string;
  difficultyLevel: string;
  equipmentNeeded: string;
  muscleGroups: string;
  imageUrl?: string;
  videoUrl?: string;
  category?: ExerciseCategory;
  goal: string;
  createdAt: string;
}

export interface FoodCategory {
  categoryId: number;
  categoryName: string;
  description?: string;
}

export interface ExerciseCategory {
  categoryId: number;
  categoryName: string;
  description?: string;
  iconUrl?: string;
}

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: 'SYSTEM' | 'REMINDER' | 'ACHIEVEMENT' | 'PROMOTION';
  targetId?: number;
  isRead: boolean;
  createdAt: string;
}

export interface DailyLog {
  id: number;
  userId: number;
  date: string;
  totalCaloriesConsumed: number;
  totalCaloriesBurned: number;
  waterIntake: number;
  sleepHours: number;
  weight?: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  result: T;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}