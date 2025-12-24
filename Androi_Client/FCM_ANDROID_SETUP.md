# Hướng dẫn Setup FCM cho Android

## 1. Tạo Firebase Project

### Bước 1: Truy cập Firebase Console

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" hoặc chọn project có sẵn

### Bước 2: Thêm Android App vào Firebase

1. Click icon Android để thêm app
2. Nhập package name: `com.example.app_selfcare`
3. (Optional) Nhập app nickname
4. (Optional) Nhập SHA-1 certificate fingerprint
5. Click "Register app"

### Bước 3: Download google-services.json

1. Download file `google-services.json`
2. Copy file vào thư mục: `Androi_Client/app/`

⚠️ **QUAN TRỌNG**: Thêm `google-services.json` vào `.gitignore`!

---

## 2. Cấu trúc files đã tạo

```
Androi_Client/app/
├── google-services.json          # File config Firebase (cần download)
├── src/main/java/.../
│   ├── Service/
│   │   └── MyFirebaseMessagingService.java  # Xử lý FCM messages
│   ├── utils/
│   │   └── FcmTokenManager.java             # Quản lý FCM token
│   └── Data/Model/
│       ├── Request/DeviceTokenRequest.java
│       └── Response/DeviceTokenResponse.java
```

---

## 3. Luồng hoạt động

### Khi User Login:

1. `LoginActivity` gọi `FcmTokenManager.registerToken()`
2. `FcmTokenManager` lấy FCM token từ Firebase
3. Gửi token lên server qua API `POST /app/device-tokens`
4. Server lưu token vào database

### Khi có Push Notification:

1. Server gửi FCM message qua Firebase
2. `MyFirebaseMessagingService.onMessageReceived()` được gọi
3. Hiển thị notification với title, body
4. Khi user click → mở Activity tương ứng (FoodDetail/WorkoutDetail)

### Khi User Logout:

1. Gọi `FcmTokenManager.unregisterToken()`
2. Server deactivate token trong database

---

## 4. Request Notification Permission (Android 13+)

Thêm code vào Activity chính (HomeActivity hoặc LoginActivity):

```java
// Kiểm tra và request permission
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                REQUEST_NOTIFICATION_PERMISSION);
    }
}
```

---

## 5. Test Push Notification

### Cách 1: Từ Firebase Console

1. Vào Firebase Console > Cloud Messaging
2. Click "Send your first message"
3. Nhập title, body
4. Chọn target: Single device hoặc Topic
5. Click "Send test message"

### Cách 2: Từ Backend

1. Tạo Food/Exercise mới qua Admin
2. Server sẽ tự động gửi push notification

### Cách 3: Test với cURL

```bash
curl -X POST "https://fcm.googleapis.com/fcm/send" \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "DEVICE_FCM_TOKEN",
    "notification": {
      "title": "Test",
      "body": "Hello from FCM"
    },
    "data": {
      "type": "FOOD",
      "targetId": "1"
    }
  }'
```

---

## 6. Troubleshooting

### FCM token không lấy được

- Kiểm tra `google-services.json` đã đặt đúng vị trí
- Kiểm tra internet connection
- Kiểm tra Google Play Services đã cài đặt

### Notification không hiển thị

- Kiểm tra permission POST_NOTIFICATIONS (Android 13+)
- Kiểm tra notification channel đã tạo
- Kiểm tra app không bị kill bởi battery optimization

### Token không gửi được lên server

- Kiểm tra đã login (có JWT token)
- Kiểm tra API endpoint đúng
- Kiểm tra log trong Logcat với tag "FcmTokenManager"

---

## 7. Files cần thêm vào .gitignore

```gitignore
# Firebase
google-services.json
```
