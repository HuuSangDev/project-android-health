import { useState, useEffect } from 'react';
import api from '../../services/api';

export default function ApiStatus() {
  const [status, setStatus] = useState<'checking' | 'connected' | 'disconnected' | 'cors-error'>('checking');
  const [message, setMessage] = useState('Đang kiểm tra kết nối...');
  const [details, setDetails] = useState<string>('');

  useEffect(() => {
    checkApiStatus();
  }, []);

  const checkApiStatus = async () => {
    try {
      setStatus('checking');
      setMessage('Đang kiểm tra kết nối...');
      
      // Thử gọi một endpoint đơn giản
      await api.get('/health');
      setStatus('connected');
      setMessage('Kết nối server thành công');
      setDetails('');
    } catch (error: any) {
      console.error('API Status Error:', error);
      
      if (error.code === 'ECONNREFUSED') {
        setStatus('disconnected');
        setMessage('Server backend không chạy');
        setDetails('Vui lòng khởi động Spring Boot server trên localhost:8080');
      } else if (error.response?.status === 401) {
        setStatus('connected');
        setMessage('Server backend đang chạy');
        setDetails('Authentication required (bình thường)');
      } else if (error.message?.includes('CORS') || error.message?.includes('Access-Control-Allow-Origin')) {
        setStatus('cors-error');
        setMessage('Lỗi CORS - Server cần cấu hình CORS');
        setDetails('Vui lòng restart Spring Boot server sau khi cập nhật SecurityConfig');
      } else if (error.code === 'ENOTFOUND') {
        setStatus('disconnected');
        setMessage('Không thể resolve localhost:8080');
        setDetails('Kiểm tra server có đang chạy không');
      } else {
        setStatus('disconnected');
        setMessage(`Lỗi kết nối: ${error.message}`);
        setDetails(error.response?.data?.message || '');
      }
    }
  };

  const getStatusColor = () => {
    switch (status) {
      case 'connected':
        return 'text-green-600 bg-green-50 border-green-200';
      case 'cors-error':
        return 'text-orange-600 bg-orange-50 border-orange-200';
      case 'disconnected':
        return 'text-red-600 bg-red-50 border-red-200';
      default:
        return 'text-yellow-600 bg-yellow-50 border-yellow-200';
    }
  };

  const getStatusIcon = () => {
    switch (status) {
      case 'connected':
        return '✅';
      case 'cors-error':
        return '⚠️';
      case 'disconnected':
        return '❌';
      default:
        return '⏳';
    }
  };

  return (
    <div className={`fixed bottom-4 right-4 p-3 rounded-lg border text-sm max-w-sm ${getStatusColor()}`}>
      <div className="flex items-start gap-2">
        <span className="text-lg">{getStatusIcon()}</span>
        <div className="flex-1">
          <div className="font-medium">{message}</div>
          {details && (
            <div className="text-xs mt-1 opacity-75">{details}</div>
          )}
          <button 
            onClick={checkApiStatus}
            className="mt-2 text-xs underline hover:no-underline"
            disabled={status === 'checking'}
          >
            {status === 'checking' ? 'Đang kiểm tra...' : 'Kiểm tra lại'}
          </button>
        </div>
      </div>
    </div>
  );
}