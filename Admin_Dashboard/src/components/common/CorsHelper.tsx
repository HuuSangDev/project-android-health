import { useState } from 'react';

export default function CorsHelper() {
  const [showHelp, setShowHelp] = useState(false);

  return (
    <>
      {showHelp && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-800 p-6 rounded-lg max-w-md mx-4">
            <h3 className="text-lg font-bold mb-4">Khắc phục lỗi CORS</h3>
            <div className="space-y-3 text-sm">
              <p><strong>Bước 1:</strong> Restart Spring Boot server</p>
              <p><strong>Bước 2:</strong> Hoặc tạm thời disable CORS trong browser:</p>
              <div className="bg-gray-100 dark:bg-gray-700 p-2 rounded text-xs font-mono">
                chrome.exe --user-data-dir=/tmp/chrome --disable-web-security --disable-features=VizDisplayCompositor
              </div>
              <p><strong>Bước 3:</strong> Hoặc sử dụng tài khoản demo:</p>
              <div className="bg-blue-50 dark:bg-blue-900 p-2 rounded">
                <p>Email: admin@healthcare.com</p>
                <p>Password: admin123</p>
              </div>
            </div>
            <button 
              onClick={() => setShowHelp(false)}
              className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
              Đóng
            </button>
          </div>
        </div>
      )}
      
      <button
        onClick={() => setShowHelp(true)}
        className="fixed bottom-4 left-4 p-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 text-xs"
        title="Hướng dẫn khắc phục CORS"
      >
        ❓
      </button>
    </>
  );
}