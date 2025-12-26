import api from "./api";
import { ApiResponse, Notification } from "../types/health";

export interface SendNotificationRequest {
  title: string;
  message: string;
}

export const notificationService = {
  getAllNotifications: async (): Promise<Notification[]> => {
    const response = await api.get<ApiResponse<Notification[]>>(
      "/notifications/all"
    );
    return response.data.result;
  },

  getUnreadNotifications: async (): Promise<Notification[]> => {
    const response = await api.get<ApiResponse<Notification[]>>(
      "/notifications/unread"
    );
    return response.data.result;
  },

  markAsRead: async (id: number): Promise<Notification> => {
    const response = await api.put<ApiResponse<Notification>>(
      `/notifications/${id}/read`
    );
    return response.data.result;
  },

  markAllAsRead: async (): Promise<void> => {
    await api.put("/notifications/read-all");
  },

  // Gửi thông báo broadcast tới tất cả users
  sendBroadcast: async (data: SendNotificationRequest): Promise<void> => {
    await api.post("/notifications/broadcast", data);
  },
};
