# Fix Lỗi 400 "userprofile is null" cho Admin

## Vấn đề
Khi tài khoản admin đăng nhập và gọi các API như:
- `/app/exercises/all`
- `/app/foods/all`
- `/app/foods/category/{categoryId}`
- `/app/foods/meal/{mealType}`

Server trả về lỗi 400 với message: `{"code":188,"message":"userprofile is null "}`

## Nguyên nhân
- Các API này yêu cầu user phải có `UserProfile` với `HealthGoal` đã được thiết lập
- Tài khoản admin thường không có profile hoặc health goal
- Logic cũ luôn kiểm tra và throw exception nếu profile null

## Giải pháp
Sửa logic để admin có thể xem tất cả dữ liệu mà không cần profile:

### 1. ExerciseController & ExerciseService
- Thêm tham số `isAdmin` vào các method
- Nếu `isAdmin = true`: trả về tất cả exercises (không lọc theo goal)
- Nếu `isAdmin = false`: lọc theo goal của user (logic cũ)

**Files đã sửa:**
- `ExerciseController.java`: Phát hiện role ADMIN và truyền vào service
- `ExerciseService.java`: 
  - `getAllExercisesByUserGoal(email, isAdmin)`
  - `getExercisesByCategory(email, categoryId, isAdmin)`
- `ExerciseRepository.java`: Thêm method `findByExerciseCategory_CategoryId(Long categoryId)`

### 2. FoodController & FoodService
Áp dụng logic tương tự:

**Files đã sửa:**
- `FoodController.java`: 
  - `/all`: Tự động phát hiện admin và gọi đúng method
  - `/category/{categoryId}`: Thêm logic admin
  - `/meal/{mealType}`: Thêm logic admin
- `FoodService.java`:
  - `getFoodsByCategory(email, categoryId, isAdmin)`
  - `getFoodsByMealType(email, mealType, isAdmin)`
- `FoodRepository.java`: Thêm method `findByMealType(MealType mealType)`

## Cách phát hiện Admin
```java
var authentication = SecurityContextHolder.getContext().getAuthentication();
boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
```

## Kết quả
- Admin có thể gọi các API mà không cần có UserProfile
- User thường vẫn cần có UserProfile với HealthGoal
- Không cần endpoint riêng cho admin (trừ `/all-admin` đã có sẵn)
- Cache key được cập nhật để phân biệt admin và user

## Test
1. Đăng nhập bằng tài khoản admin
2. Gọi API `/app/exercises/all` → Trả về tất cả exercises
3. Gọi API `/app/foods/all` → Trả về tất cả foods
4. Đăng nhập bằng tài khoản user thường
5. Gọi các API trên → Trả về dữ liệu theo goal của user
