# Hướng dẫn Setup FCM Push Notification

## 1. Tạo Firebase Project và lấy Service Account Key

### Bước 1: Tạo Firebase Project

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" hoặc chọn project có sẵn
3. Đặt tên project và làm theo hướng dẫn

### Bước 2: Lấy Service Account Key

1. Vào **Project Settings** (icon bánh răng)
2. Chọn tab **Service accounts**
3. Click **Generate new private key**
4. Download file JSON

### Bước 3: Đặt file vào project

1. Đổi tên file thành `serviceAccountKey.json`
2. Copy vào thư mục: `Server/src/main/resources/firebase/`

⚠️ **QUAN TRỌNG**: Không commit file này lên git! File đã được thêm vào .gitignore

---

## 2. Test với Postman

### Bước 1: Login lấy JWT Token

```
POST http://localhost:8080/app/auth/login
Content-Type: application/json

{
    "email": "your-email@example.com",
    "password": "your-password"
}
```

Response sẽ trả về token:

```json
{
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Bước 2: Đăng ký FCM Device Token

```
POST http://localhost:8080/app/device-tokens
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
    "token": "your-fcm-device-token-from-android",
    "platform": "ANDROID"
}
```

Response:

```json
{
  "result": {
    "id": 1,
    "token": "your-fcm-device-token",
    "platform": "ANDROID",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  "message": "Device token registered successfully"
}
```

### Bước 3: Tạo Food/Exercise để trigger Push Notification

```
POST http://localhost:8080/app/foods/create
Authorization: Bearer <admin-jwt-token>
Content-Type: multipart/form-data

foodName: Test Food
caloriesPer100g: 100
categoryId: 1
goal: WEIGHT_LOSS
... (các field khác)
```

Khi tạo thành công:

- WebSocket sẽ broadcast notification
- FCM sẽ gửi push notification tới tất cả devices đã đăng ký

---

## 3. API Endpoints

### Device Token APIs

| Method | Endpoint                       | Mô tả                        |
| ------ | ------------------------------ | ---------------------------- |
| POST   | `/app/device-tokens`           | Đăng ký FCM token            |
| DELETE | `/app/device-tokens/{token}`   | Hủy đăng ký token (logout)   |
| DELETE | `/app/device-tokens/all`       | Hủy tất cả token của user    |
| GET    | `/app/device-tokens/my-tokens` | Lấy danh sách token của user |

### Request Body cho đăng ký token:

```json
{
  "token": "string (required)",
  "platform": "ANDROID | IOS | WEB (required)"
}
```

---

## 4. Cấu trúc Push Notification

### Notification Payload

```json
{
  "notification": {
    "title": "Món ăn mới",
    "body": "Món ăn \"Salad\" vừa được thêm vào hệ thống"
  },
  "data": {
    "type": "FOOD",
    "targetId": "123",
    "notificationId": "456"
  }
}
```

### Data fields:

- `type`: Loại notification (FOOD, EXERCISE)
- `targetId`: ID của food/exercise để mở chi tiết
- `notificationId`: ID của notification trong database

---

## 5. Xử lý trên Android

### Nhận FCM Token

```kotlin
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        // Gửi token lên server
        apiService.registerDeviceToken(DeviceTokenRequest(token, "ANDROID"))
    }
}
```

### Xử lý Push Notification

```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val type = data["type"]
        val targetId = data["targetId"]

        // Tạo notification và xử lý click
        when (type) {
            "FOOD" -> openFoodDetail(targetId)
            "EXERCISE" -> openExerciseDetail(targetId)
        }
    }

    override fun onNewToken(token: String) {
        // Token mới được tạo, gửi lên server
        sendTokenToServer(token)
    }
}
```

---

## 6. Troubleshooting

### FCM không gửi được

1. Kiểm tra file `serviceAccountKey.json` đã đặt đúng vị trí
2. Kiểm tra log server: `Firebase Admin SDK initialized successfully`
3. Kiểm tra token đã được đăng ký và `isActive = true`

### Token bị deactivate

- FCM tự động deactivate token khi:
  - User uninstall app
  - Token hết hạn
  - Token không hợp lệ
- Kiểm tra bảng `device_tokens` trong database

### Test FCM trực tiếp

Sử dụng Firebase Console > Cloud Messaging để gửi test notification
