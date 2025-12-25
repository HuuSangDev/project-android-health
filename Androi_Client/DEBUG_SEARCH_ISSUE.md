# Debug SearchFragment - Không hiển thị dữ liệu

## 🔍 Các bước debug

### 1. **Kiểm tra Logcat**
Mở Android Studio → Logcat và filter theo tag `SearchFragment`:

```
adb logcat | grep SearchFragment
```

Tìm các log messages:
- `"Starting to load data from API..."`
- `"Current token: EXISTS"` hoặc `"Current token: NULL"`
- `"Foods API response received"`
- `"Received X foods from API"`
- `"Total items after loading foods"`

### 2. **Kiểm tra Backend Server**

#### Kiểm tra server có chạy không:
```bash
# Trong terminal Server folder
mvn spring-boot:run
```

#### Test API trực tiếp:
```bash
# Test không cần auth (nếu có)
curl http://localhost:8080/app/foods/all

# Test với auth token
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/app/foods/all
```

### 3. **Kiểm tra Network Configuration**

#### Nếu dùng Emulator:
- Base URL: `http://10.0.2.2:8080/`
- Server chạy trên localhost:8080

#### Nếu dùng Physical Device:
- Cần đổi IP trong `ApiClient.java`:
```java
private static final String BASE_URL = "http://192.168.1.XXX:8080/";
```

### 4. **Kiểm tra Authentication Token**

Trong Logcat tìm:
```
Current token: EXISTS (length: XXX)
```

Nếu thấy `Current token: NULL`:
1. User chưa login
2. Token đã expire
3. Token không được lưu đúng cách

### 5. **Test với dữ liệu giả**

Nếu API không hoạt động, app sẽ tự động tạo test data sau 3 giây.
Trong Logcat sẽ thấy:
```
No data loaded from API, creating test data
Created 6 test items
```

## 🚨 Các lỗi thường gặp

### 1. **Network Error**
```
Error loading foods: java.net.ConnectException: Failed to connect
```
**Giải pháp:**
- Kiểm tra server có chạy không
- Kiểm tra IP address đúng không
- Kiểm tra firewall/antivirus

### 2. **Authentication Error**
```
Failed to load foods. Response code: 401
```
**Giải pháp:**
- User cần login lại
- Kiểm tra token format
- Kiểm tra token expiration

### 3. **Empty Response**
```
API returned error code: 404
```
**Giải pháp:**
- Kiểm tra endpoint URL
- Kiểm tra database có dữ liệu không

### 4. **UI Not Updating**
```
Updating UI. Filtered items count: 0
All items count: 5
```
**Giải pháp:**
- Kiểm tra `showAllItems()` được gọi chưa
- Kiểm tra adapter setup
- Kiểm tra RecyclerView visibility

## 🔧 Quick Fixes

### Fix 1: Force show test data
Trong `SearchFragment.onCreate()` thêm:
```java
// Temporary: Force test data
createTestData();
```

### Fix 2: Bypass authentication
Trong `initViews()` đổi:
```java
// Tạm thời không dùng token
apiService = ApiClient.getClient().create(ApiService.class);
```

### Fix 3: Check network on device
Thêm vào `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 📱 Test Steps

1. **Mở SearchFragment**
2. **Kiểm tra Logcat** - có log "Starting to load data from API..." không?
3. **Đợi 3 giây** - có test data xuất hiện không?
4. **Nhập "salad"** - có filter được không?
5. **Click item** - có navigate được không?

## 🎯 Expected Behavior

### Khi hoạt động đúng:
1. Fragment mở → Loading state
2. API call → Nhận dữ liệu
3. Hiển thị danh sách → "Kết quả tìm kiếm"
4. Nhập từ khóa → Filter real-time
5. Click item → Mở detail

### Debug Output mong muốn:
```
SearchFragment: Starting to load data from API...
SearchFragment: Current token: EXISTS (length: 200+)
SearchFragment: Loading foods from API...
SearchFragment: Foods API response received. Code: 200
SearchFragment: API Response code: 200
SearchFragment: Received 8 foods from API
SearchFragment: Added food: salad
SearchFragment: Total items after loading foods: 8
SearchFragment: showAllItems called. All items size: 8
SearchFragment: Updating UI. Filtered items count: 8
```

## 🔄 Next Steps

1. **Run app với debug logs**
2. **Check Logcat output**
3. **Identify specific error**
4. **Apply appropriate fix**
5. **Test again**

Nếu vẫn không hoạt động, hãy share Logcat output để debug tiếp!