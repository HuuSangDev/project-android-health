# SearchFragment - Tích hợp API Backend

## Tổng quan
SearchFragment đã được cập nhật để gọi API thật từ backend và hiển thị dữ liệu món ăn và bài tập thực tế.

## Các thay đổi chính

### 1. **Tích hợp API Backend**
- Gọi `getAllFoods()` để lấy danh sách món ăn
- Gọi `getExercises()` để lấy danh sách bài tập
- Xử lý response và error handling

### 2. **Tính năng tìm kiếm thời gian thực**
- TextWatcher để lọc dữ liệu khi người dùng nhập
- Hiển thị/ẩn nút clear search
- Filter theo tên món ăn/bài tập (không phân biệt hoa thường)

### 3. **Cải thiện UX**
- Loading state khi đang tải dữ liệu
- Empty state khi không có kết quả
- Error handling với Toast message
- Icon phân biệt món ăn (cam) và bài tập (xanh)

### 4. **Navigation**
- Click vào món ăn → mở `FoodDetailActivity`
- Click vào bài tập → mở `WorkoutDetailActivity`
- Truyền ID qua Intent extras

## Cấu trúc dữ liệu

### SearchItem
```java
public class SearchItem {
    public static final int TYPE_WORKOUT = 1;
    public static final int TYPE_FOOD = 2;
    
    private int id;        // ID từ backend
    private String name;   // Tên món ăn/bài tập
    private int type;      // Loại (food/workout)
}
```

### API Endpoints sử dụng
- `GET /app/foods/all` - Lấy tất cả món ăn
- `GET /app/exercises/all` - Lấy tất cả bài tập

## Cách sử dụng

### 1. **Mở SearchFragment**
```java
SearchFragment fragment = new SearchFragment();
getSupportFragmentManager()
    .beginTransaction()
    .replace(R.id.fragment_container, fragment)
    .addToBackStack(null)
    .commit();
```

### 2. **Tìm kiếm**
- Nhập từ khóa vào ô search
- Kết quả được filter tự động
- Click vào item để xem chi tiết

### 3. **States**
- **Loading**: Hiển thị ProgressBar khi đang tải
- **Empty**: Hiển thị khi chưa có từ khóa hoặc không có kết quả
- **Results**: Hiển thị danh sách kết quả tìm kiếm

## Customization

### Thay đổi icon
```java
// Trong SearchAdapter.onBindViewHolder()
if (item.getType() == SearchItem.TYPE_FOOD) {
    holder.imgIcon.setImageResource(R.drawable.ic_food);
    holder.imgIcon.setColorFilter(context.getResources().getColor(R.color.orange_primary));
}
```

### Thêm filter nâng cao
```java
private void filterItems(String query) {
    filteredItems.clear();
    
    for (SearchItem item : allItems) {
        // Thêm logic filter phức tạp hơn
        if (matchesQuery(item, query)) {
            filteredItems.add(item);
        }
    }
    
    updateUI();
}
```

## Error Handling

### Network Errors
- Hiển thị Toast message thông báo lỗi
- Log error để debug
- Không crash app

### Empty Data
- Hiển thị empty state với icon và text
- Hướng dẫn người dùng thử lại

## Performance

### Optimizations
- Sử dụng `runOnUiThread()` để update UI
- Filter local data thay vì gọi API mỗi lần
- RecyclerView với ViewHolder pattern

### Memory Management
- Clear data khi Fragment bị destroy
- Null check trước khi update UI

## Testing

### Manual Testing
1. Mở SearchFragment
2. Kiểm tra loading state
3. Nhập từ khóa tìm kiếm
4. Verify kết quả hiển thị đúng
5. Click vào item để mở detail

### API Testing
- Kiểm tra kết nối internet
- Test với dữ liệu trống
- Test với API error

## Troubleshooting

### Không load được dữ liệu
1. Kiểm tra kết nối internet
2. Verify API endpoints trong ApiService
3. Check logs để xem error message

### Search không hoạt động
1. Kiểm tra TextWatcher setup
2. Verify filter logic
3. Check adapter.notifyDataSetChanged()

### Navigation không hoạt động
1. Kiểm tra Intent setup
2. Verify Activity exists
3. Check extras được truyền đúng

## Future Enhancements

### Có thể thêm
- Search history
- Filter theo category
- Sort results
- Pagination cho large datasets
- Voice search
- Barcode scanning (cho food)

### API Improvements
- Search endpoint với query parameter
- Debounce search requests
- Cache search results
- Offline support