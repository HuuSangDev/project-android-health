import { useState, useEffect } from "react";
import { notificationService } from "../../services/notificationService";
import { Notification } from "../../types/health";
import PageMeta from "../../components/common/PageMeta";
import toast from "react-hot-toast";

interface NotificationFormData {
  title: string;
  message: string;
}

const initialFormData: NotificationFormData = {
  title: "",
  message: "",
};

export default function NotificationList() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<"all" | "unread">("all");
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] =
    useState<NotificationFormData>(initialFormData);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadNotifications();
  }, [filter]);

  const loadNotifications = async () => {
    try {
      setLoading(true);
      const data =
        filter === "unread"
          ? await notificationService.getUnreadNotifications()
          : await notificationService.getAllNotifications();
      setNotifications(data);
    } catch (error) {
      toast.error("Không thể tải thông báo");
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationService.markAsRead(id);
      toast.success("Đã đánh dấu đã đọc");
      loadNotifications();
    } catch (error) {
      toast.error("Có lỗi xảy ra");
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      toast.success("Đã đánh dấu tất cả đã đọc");
      loadNotifications();
    } catch (error) {
      toast.error("Có lỗi xảy ra");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.title.trim()) {
      toast.error("Vui lòng nhập tiêu đề");
      return;
    }
    if (!formData.message.trim()) {
      toast.error("Vui lòng nhập nội dung");
      return;
    }

    try {
      setSubmitting(true);
      await notificationService.sendBroadcast({
        title: formData.title,
        message: formData.message,
      });
      toast.success("Đã gửi thông báo tới tất cả người dùng");
      setShowModal(false);
      setFormData(initialFormData);
      loadNotifications();
    } catch (error) {
      console.error("Failed to send notification:", error);
      toast.error("Không thể gửi thông báo");
    } finally {
      setSubmitting(false);
    }
  };

  const getTypeColor = (type: string) => {
    const colors: Record<string, string> = {
      SYSTEM: "bg-blue-100 text-blue-800",
      FOOD: "bg-orange-100 text-orange-800",
      EXERCISE: "bg-green-100 text-green-800",
      REMINDER: "bg-yellow-100 text-yellow-800",
      ACHIEVEMENT: "bg-purple-100 text-purple-800",
      PROMOTION: "bg-pink-100 text-pink-800",
    };
    return colors[type] || "bg-gray-100 text-gray-800";
  };

  const getTypeLabel = (type: string) => {
    const labels: Record<string, string> = {
      SYSTEM: "Hệ thống",
      FOOD: "Món ăn",
      EXERCISE: "Bài tập",
      REMINDER: "Nhắc nhở",
      ACHIEVEMENT: "Thành tựu",
      PROMOTION: "Khuyến mãi",
    };
    return labels[type] || type;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-brand-500"></div>
      </div>
    );
  }

  return (
    <>
      <PageMeta
        title="Thông báo | Health Care Admin"
        description="Quản lý thông báo"
      />
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-800 dark:text-white">
              Thông báo
            </h1>
            <p className="text-gray-500">Quản lý thông báo hệ thống</p>
          </div>
          <div className="flex gap-2 flex-wrap">
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value as "all" | "unread")}
              className="px-3 py-2 border rounded-lg dark:bg-gray-800 dark:border-gray-700 dark:text-white"
            >
              <option value="all">Tất cả</option>
              <option value="unread">Chưa đọc</option>
            </select>
            <button
              onClick={handleMarkAllAsRead}
              className="px-4 py-2 text-brand-500 hover:text-brand-600 border border-brand-500 rounded-lg"
            >
              Đánh dấu tất cả đã đọc
            </button>
            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 bg-brand-500 text-white rounded-lg hover:bg-brand-600 flex items-center gap-2"
            >
              <svg
                className="h-5 w-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 4v16m8-8H4"
                />
              </svg>
              Tạo thông báo
            </button>
          </div>
        </div>

        <div className="space-y-3">
          {notifications.map((notif) => (
            <div
              key={notif.id}
              className={`rounded-xl border p-4 ${
                notif.isRead
                  ? "bg-white dark:bg-white/[0.03] border-gray-200 dark:border-gray-800"
                  : "bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800"
              }`}
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span
                      className={`px-2 py-0.5 text-xs font-medium rounded-full ${getTypeColor(
                        notif.type
                      )}`}
                    >
                      {getTypeLabel(notif.type)}
                    </span>
                    {!notif.isRead && (
                      <span className="w-2 h-2 bg-blue-500 rounded-full"></span>
                    )}
                  </div>
                  <h3 className="font-semibold text-gray-800 dark:text-white">
                    {notif.title}
                  </h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {notif.message}
                  </p>
                  <p className="text-xs text-gray-400 mt-2">
                    {new Date(notif.createdAt).toLocaleString("vi-VN")}
                  </p>
                </div>
                {!notif.isRead && (
                  <button
                    onClick={() => handleMarkAsRead(notif.id)}
                    className="text-sm text-brand-500 hover:text-brand-600 whitespace-nowrap"
                  >
                    Đánh dấu đã đọc
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>

        {notifications.length === 0 && (
          <div className="text-center py-12 text-gray-500">
            Không có thông báo nào
          </div>
        )}
      </div>

      {/* Modal tạo thông báo */}
      {showModal && (
        <div className="fixed inset-0 z-[99999] overflow-y-auto">
          <div
            className="fixed inset-0 bg-black/60 backdrop-blur-sm"
            onClick={() => setShowModal(false)}
          ></div>
          <div className="flex items-center justify-center min-h-screen px-4 py-8">
            <div className="relative w-full max-w-lg p-6 bg-white dark:bg-gray-800 shadow-2xl rounded-2xl">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                  Tạo thông báo mới
                </h3>
                <button
                  onClick={() => setShowModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <svg
                    className="h-6 w-6"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Tiêu đề *
                  </label>
                  <input
                    type="text"
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-500 dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                    placeholder="Nhập tiêu đề thông báo..."
                    value={formData.title}
                    onChange={(e) =>
                      setFormData({ ...formData, title: e.target.value })
                    }
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Nội dung *
                  </label>
                  <textarea
                    required
                    rows={4}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-500 dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                    placeholder="Nhập nội dung thông báo..."
                    value={formData.message}
                    onChange={(e) =>
                      setFormData({ ...formData, message: e.target.value })
                    }
                  />
                </div>

                <div className="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-3">
                  <div className="flex items-start gap-2">
                    <svg
                      className="h-5 w-5 text-blue-500 mt-0.5"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                      />
                    </svg>
                    <p className="text-sm text-blue-700 dark:text-blue-300">
                      Thông báo sẽ được gửi tới{" "}
                      <strong>tất cả người dùng</strong> qua realtime và push
                      notification.
                    </p>
                  </div>
                </div>

                <div className="flex justify-end gap-3 pt-4 border-t">
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 dark:bg-gray-700 dark:text-gray-300"
                  >
                    Hủy
                  </button>
                  <button
                    type="submit"
                    disabled={submitting}
                    className="px-4 py-2 bg-brand-500 text-white rounded-lg hover:bg-brand-600 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                  >
                    {submitting && (
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    )}
                    {submitting ? "Đang gửi..." : "Gửi thông báo"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
