# Bảng Điều Khiển Quản Trị Chăm Sóc Sức Khỏe

Một bảng điều khiển quản trị React.js hiện đại để quản lý ứng dụng chăm sóc sức khỏe, được xây dựng với TypeScript, Tailwind CSS và tích hợp với backend Spring Boot.

## Tính Năng

### 🏥 Quản Lý Sức Khỏe
- **Bảng điều khiển**: Tổng quan về người dùng, thực phẩm, bài tập và chỉ số sức khỏe
- **Quản lý người dùng**: Xem và quản lý hồ sơ người dùng, theo dõi BMI, mục tiêu sức khỏe
- **Quản lý thực phẩm**: Quản lý thực phẩm, thông tin dinh dưỡng, kế hoạch bữa ăn
- **Quản lý bài tập**: Quản lý bài tập, chương trình tập luyện, chương trình thể dục

### 🔐 Xác Thực
- Hệ thống đăng nhập an toàn với hỗ trợ JWT token
- Bảo vệ route với authentication guards
- Quản lý phiên người dùng

### 🎨 Giao Diện Hiện Đại
- Thiết kế responsive với Tailwind CSS
- Hỗ trợ theme Tối/Sáng
- Biểu đồ tương tác và trực quan hóa dữ liệu
- Giao diện thân thiện với mobile

### 🌐 Đa Ngôn Ngữ
- Hỗ trợ tiếng Anh và tiếng Việt
- Chuyển đổi ngôn ngữ dễ dàng
- Lưu trữ ngôn ngữ trong localStorage

### 🔧 Tính Năng Kỹ Thuật
- TypeScript cho type safety
- React Router cho navigation
- Axios cho tích hợp API
- React Hot Toast cho thông báo
- Validation form với React Hook Form
- i18next cho đa ngôn ngữ

## Bắt Đầu

### Yêu Cầu
- Node.js (v16 trở lên)
- npm hoặc yarn
- Backend Spring Boot chạy trên `http://localhost:8080/app`

### Cài Đặt

1. Clone repository:
```bash
git clone <repository-url>
cd free-react-tailwind-admin-dashboard
```

2. Cài đặt dependencies:
```bash
npm install --legacy-peer-deps
```

3. Khởi động development server:
```bash
npm run dev
```

4. Mở trình duyệt và truy cập `http://localhost:5173`

### Tài Khoản Demo
- **Email**: admin@healthcare.com
- **Mật khẩu**: admin123

## Cấu Trúc Dự Án

```
src/
├── components/          # Các component tái sử dụng
│   ├── auth/           # Component xác thực
│   ├── health/         # Component sức khỏe
│   ├── header/         # Component header
│   ├── common/         # Component chung (LanguageToggle)
│   └── ui/             # Component UI chung
├── context/            # React contexts (Auth, Theme, Language)
├── pages/              # Các component trang
│   ├── Dashboard/      # Trang dashboard
│   ├── Foods/          # Trang quản lý thực phẩm
│   ├── Exercises/      # Trang quản lý bài tập
│   ├── Users/          # Trang quản lý người dùng
│   └── AuthPages/      # Trang xác thực
├── services/           # Lớp service API
├── types/              # Định nghĩa TypeScript types
├── i18n/               # Cấu hình đa ngôn ngữ
│   ├── i18n.ts         # Cấu hình i18next
│   └── locales/        # File dịch thuật
│       ├── en.json     # Tiếng Anh
│       └── vi.json     # Tiếng Việt
└── layout/             # Component layout
```

## Tích Hợp API

Dashboard tích hợp với backend Spring Boot chạy trên `http://localhost:8080/app`. Các endpoint được sử dụng:

- `GET /foods/all` - Lấy tất cả thực phẩm
- `GET /exercises/all` - Lấy tất cả bài tập
- `GET /users/all` - Lấy tất cả người dùng (mock data cho demo)
- Các endpoint xác thực (sẽ được triển khai)

## Scripts Có Sẵn

- `npm run dev` - Khởi động development server
- `npm run build` - Build cho production
- `npm run preview` - Preview production build
- `npm run lint` - Chạy ESLint

## Công Nghệ Sử Dụng

- **Frontend**: React 19, TypeScript, Tailwind CSS
- **Routing**: React Router v7
- **State Management**: React Context API
- **HTTP Client**: Axios
- **Charts**: ApexCharts
- **Icons**: Custom SVG icons
- **Notifications**: React Hot Toast
- **Forms**: React Hook Form
- **Internationalization**: i18next, react-i18next

## Tích Hợp Backend

Dashboard này được thiết kế để hoạt động với backend Spring Boot của ứng dụng Android Health Care. Đảm bảo backend server đang chạy trên `http://localhost:8080/app` trước khi sử dụng dashboard.

### Xác Thực API
Dashboard sử dụng Bearer token authentication. Token được lưu trữ trong localStorage và tự động được bao gồm trong các API request.

## Đa Ngôn Ngữ

### Chuyển Đổi Ngôn Ngữ
- Sử dụng dropdown ngôn ngữ ở header để chuyển đổi
- Hỗ trợ tiếng Anh (🇺🇸) và tiếng Việt (🇻🇳)
- Ngôn ngữ được lưu trong localStorage

### Thêm Ngôn Ngữ Mới
1. Tạo file translation mới trong `src/i18n/locales/`
2. Thêm ngôn ngữ vào `availableLanguages` trong `LanguageContext`
3. Import và thêm vào `resources` trong `i18n.ts`

### Cấu Trúc Translation Keys
```json
{
  "auth": {
    "signin": {
      "title": "Đăng Nhập Hệ Thống Quản Trị Sức Khỏe",
      "subtitle": "Nhập email và mật khẩu để đăng nhập!"
    }
  },
  "navigation": {
    "dashboard": "Trang chủ",
    "userManagement": "Quản lý người dùng"
  },
  "common": {
    "save": "Lưu",
    "cancel": "Hủy"
  }
}
```

## Đóng Góp

1. Fork repository
2. Tạo feature branch
3. Thực hiện thay đổi
4. Test kỹ lưỡng
5. Submit pull request

## Giấy Phép

Dự án này được cấp phép theo MIT License.

## Hỗ Trợ

Nếu bạn gặp vấn đề hoặc có câu hỏi, vui lòng tạo issue trên GitHub repository.