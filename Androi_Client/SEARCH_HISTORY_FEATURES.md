# SearchFragment - Lịch sử tìm kiếm & Hiển thị hình ảnh

## ✨ Tính năng mới đã thêm

### 1. **Lưu lịch sử tìm kiếm**
- ✅ Tự động lưu khi user click vào item
- ✅ Lưu tối đa 20 items gần nhất
- ✅ Tránh duplicate items
- ✅ Sắp xếp theo thời gian (mới nhất trước)

### 2. **Hiển thị hình ảnh**
- ✅ Load ảnh từ API với Glide
- ✅ Ảnh tròn với CircleCrop transform
- ✅ Placeholder và error fallback
- ✅ Icon mặc định nếu không có ảnh

### 3. **Navigation đến chi tiết**
- ✅ Click món ăn → `FoodDetailActivity`
- ✅ Click bài tập → `WorkoutDetailActivity`
- ✅ Truyền ID qua Intent extras
- ✅ Error handling cho navigation

### 4. **UI/UX cải thiện**
- ✅ Section title động: "Lịch sử tìm kiếm" / "Kết quả tìm kiếm" / "Gợi ý cho bạn"
- ✅ Hiển thị lịch sử khi ô search trống
- ✅ Ảnh lớn hơn (48dp) để dễ nhìn
- ✅ Smooth transitions

## 🏗️ Kiến trúc Implementation

### SearchHistoryManager
```java
// Lưu lịch sử
historyManager.addToHistory(item, imageUrl, category);

// Lấy lịch sử
List<SearchHistory> history = historyManager.getHistory();
List<SearchItem> items = historyManager.getHistoryAsSearchItems();

// Quản lý
historyManager.removeFromHistory(id, type);
historyManager.clearHistory();
```

### SearchItem (Updated)
```java
// Constructor mới hỗ trợ imageUrl
SearchItem item = new SearchItem(id, name, type, imageUrl);

// Getter cho imageUrl
String imageUrl = item.getImageUrl();
```

### SearchAdapter (Enhanced)
```java
// Load ảnh với Glide
Glide.with(context)
    .load(item.getImageUrl())
    .placeholder(R.drawable.ic_food)
    .error(R.drawable.ic_food)
    .transform(new CircleCrop())
    .into(holder.imgIcon);
```

## 📱 User Flow

### Khi mở SearchFragment:
1. **Hiển thị lịch sử** (nếu có) với title "Lịch sử tìm kiếm"
2. **Load dữ liệu từ API** trong background
3. **Hiển thị ảnh** cho mỗi item (thật hoặc icon mặc định)

### Khi nhập tìm kiếm:
1. **Filter real-time** với title "Kết quả tìm kiếm"
2. **Hiển thị nút clear** search
3. **Highlight matching items**

### Khi click item:
1. **Lưu vào lịch sử** với đầy đủ thông tin
2. **Navigate đến detail** activity
3. **Close SearchFragment**

### Khi xóa search:
1. **Quay về lịch sử** (nếu có)
2. **Hoặc hiển thị gợi ý** (nếu không có lịch sử)

## 🗂️ Data Storage

### SharedPreferences Structure
```json
{
  "history_list": [
    {
      "id": 1,
      "name": "Salad trứng luộc",
      "type": 2,
      "imageUrl": "https://cloudinary.../salad.jpg",
      "category": "BREAKFAST",
      "timestamp": 1703123456789
    }
  ]
}
```

### Memory Maps
```java
// Lưu chi tiết để navigation
Map<Integer, FoodCreateResponse> foodDetailsMap;
Map<Integer, ExerciseResponse> exerciseDetailsMap;
```

## 🎨 UI Components

### Layout Updates
- `tvSectionTitle` - Dynamic section title
- `imgIcon` - Larger size (48dp) for better image display
- Glide integration for smooth image loading

### Visual States
- **Loading**: ProgressBar + "Đang tải dữ liệu..."
- **History**: "Lịch sử tìm kiếm" + history items
- **Search Results**: "Kết quả tìm kiếm" + filtered items  
- **Suggestions**: "Gợi ý cho bạn" + all items
- **Empty**: Search icon + "Nhập từ khóa để tìm kiếm"

## 🔧 Configuration

### Image Loading (Glide)
```java
// Circular images with fallbacks
Glide.with(context)
    .load(imageUrl)
    .placeholder(defaultIcon)
    .error(defaultIcon)
    .transform(new CircleCrop())
    .into(imageView);
```

### History Limits
```java
private static final int MAX_HISTORY_SIZE = 20;
```

### Navigation Intents
```java
// Food Detail
Intent intent = new Intent(context, FoodDetailActivity.class);
intent.putExtra("FOOD_ID", foodId);

// Exercise Detail  
Intent intent = new Intent(context, WorkoutDetailActivity.class);
intent.putExtra("EXERCISE_ID", exerciseId);
```

## 📊 Performance

### Optimizations
- ✅ Local history storage (SharedPreferences)
- ✅ Image caching với Glide
- ✅ Efficient list operations
- ✅ Memory-friendly data structures

### Memory Usage
- History: ~1KB per item × 20 items = ~20KB
- Images: Cached by Glide automatically
- Maps: Only store essential data for navigation

## 🧪 Testing

### Manual Test Cases
1. **Search & Save History**
   - Search "salad" → Click item → Check history saved
   
2. **Image Loading**
   - Verify images load from API URLs
   - Check fallback icons work
   
3. **Navigation**
   - Click food item → Opens FoodDetailActivity
   - Click exercise item → Opens WorkoutDetailActivity
   
4. **History Management**
   - Check duplicate prevention
   - Verify chronological order
   - Test history limit (20 items)

### Edge Cases
- ✅ No internet → Fallback icons
- ✅ Invalid image URLs → Error icons  
- ✅ Empty history → Show suggestions
- ✅ Navigation errors → Toast messages

## 🚀 Future Enhancements

### Possible Additions
- [ ] Search history categories/filters
- [ ] Export/import history
- [ ] History analytics
- [ ] Favorite items (separate from history)
- [ ] Search suggestions based on history
- [ ] Voice search integration
- [ ] Barcode scanning for foods

### Performance Improvements
- [ ] Lazy loading for large datasets
- [ ] Image preloading
- [ ] Background sync for history
- [ ] Compression for stored data

---

**Status**: ✅ **COMPLETED & READY**
**Features**: Search History + Image Display + Navigation
**Build Status**: ✅ SUCCESS