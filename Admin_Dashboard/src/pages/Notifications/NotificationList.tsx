import { useState, useEffect } from 'react';
import { notificationService } from '../../services/notificationService';
import { Notification } from '../../types/health';
import PageMeta from '../../components/common/PageMeta';
import toast from 'react-hot-toast';

export default function NotificationList() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'all' | 'unread'>('all');

  useEffect(() => { loadNotifications(); }, [filter]);

  const loadNotifications = async () => {
    try {
      setLoading(true);
      const data = filter === 'unread' 
        ? await notificationService.getUnreadNotifications()
        : await notificationService.getAllNotifications();
      setNotifications(data);
    } catch (error) {
      toast.error('Không thể tải thông báo');
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationService.markAsRead(id);
      toast.success('Đã đánh dấu đã đọc');
      loadNotifications();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      toast.success('Đã đánh dấu tất cả đã đọc');
      loadNotifications();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  };

  const getTypeColor = (type: string) => {
    const colors: Record<string, string> = {
      'SYSTEM': 'bg-blue-100 text-blue-800',
      'REMINDER': 'bg-yellow-100 text-yellow-800',
      'ACHIEVEMENT': 'bg-green-100 text-green-800',
      'PROMOTION': 'bg-purple-100 text-purple-800',
    };
    return colors[type] || 'bg-gray-100 text-gray-800';
  };

  const getTypeLabel = (type: string) => {
    const labels: Record<string, string> = {
      'SYSTEM': 'Hệ thống',
      'REMINDER': 'Nhắc nhở',
      'ACHIEVEMENT': 'Thành tựu',
      'PROMOTION': 'Khuyến mãi',
    };
    return labels[type] || type;
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64"><div className="animate-spin rounded-full h-16 w-16 border-b-2 border-brand-500"></div></div>;
  }

  return (
    <>
      <PageMeta title="Thông báo | Health Care Admin" description="Quản lý thông báo" />
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-800 dark:text-white">Thông báo</h1>
            <p className="text-gray-500">Quản lý thông báo hệ thống</p>
          </div>
          <div className="flex gap-2">
            <select value={filter} onChange={(e) => setFilter(e.target.value as 'all' | 'unread')} className="px-3 py-2 border rounded-lg dark:bg-gray-800 dark:border-gray-700 dark:text-white">
              <option value="all">Tất cả</option>
              <option value="unread">Chưa đọc</option>
            </select>
            <button onClick={handleMarkAllAsRead} className="px-4 py-2 text-brand-500 hover:text-brand-600 border border-brand-500 rounded-lg">
              Đánh dấu tất cả đã đọc
            </button>
          </div>
        </div>

        <div className="space-y-3">
          {notifications.map((notif) => (
            <div key={notif.id} className={`rounded-xl border p-4 ${notif.isRead ? 'bg-white dark:bg-white/[0.03] border-gray-200 dark:border-gray-800' : 'bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800'}`}>
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className={`px-2 py-0.5 text-xs font-medium rounded-full ${getTypeColor(notif.type)}`}>
                      {getTypeLabel(notif.type)}
                    </span>
                    {!notif.isRead && <span className="w-2 h-2 bg-blue-500 rounded-full"></span>}
                  </div>
                  <h3 className="font-semibold text-gray-800 dark:text-white">{notif.title}</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{notif.message}</p>
                  <p className="text-xs text-gray-400 mt-2">{new Date(notif.createdAt).toLocaleString('vi-VN')}</p>
                </div>
                {!notif.isRead && (
                  <button onClick={() => handleMarkAsRead(notif.id)} className="text-sm text-brand-500 hover:text-brand-600 whitespace-nowrap">
                    Đánh dấu đã đọc
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>

        {notifications.length === 0 && <div className="text-center py-12 text-gray-500">Không có thông báo nào</div>}
      </div>
    </>
  );
}
