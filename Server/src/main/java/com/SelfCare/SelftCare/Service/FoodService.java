package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.CreateFoodRequest;
import com.SelfCare.SelftCare.DTO.Request.FoodCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Request.FoodSearchRequest;
import com.SelfCare.SelftCare.DTO.Request.UpdateFoodRequest;
import com.SelfCare.SelftCare.DTO.Response.FoodCategoryResponse;
import com.SelfCare.SelftCare.DTO.Response.FoodCreateResponse;
import com.SelfCare.SelftCare.Entity.Food;
import com.SelfCare.SelftCare.Entity.FoodCategory;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Entity.UserProfile;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.MealType;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Mapper.FoodMapper;
import com.SelfCare.SelftCare.Repository.FoodCategoryRepository;
import com.SelfCare.SelftCare.Repository.FoodRepository;
import com.SelfCare.SelftCare.Repository.UserRepository;
import com.SelfCare.SelftCare.Specification.FoodSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FoodService {

    FoodRepository foodRepository;
    FoodCategoryRepository foodCategoryRepository;
    FileUploadsService fileUploadsService;
    FoodMapper foodMapper;
    UserRepository userRepository;
    NotificationService notificationService;



    // Tạm bỏ cache để tránh lỗi deserialize từ Redis
    // @Cacheable(value = "foodDetail", key = "#foodId")
    public FoodCreateResponse getFoodDetail(Long foodId) {
        log.info(">>> QUERY DB: getFoodDetail(" + foodId + ")");
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
        return foodMapper.toFoodResponse(food);
    }



    @CacheEvict(
            value = { "allFoods", "foodsByMeal", "foodDetail" },
            allEntries = true
    )
    public FoodCreateResponse createFood(CreateFoodRequest request) throws IOException {

        // 1. Lấy category nếu có
        FoodCategory category = null;
        if (request.getCategoryId() != null) {
            category = foodCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.FOOD_CATEGORY_NOT_FOUND));
        }

        // 2. Xử lý Upload Avatar (nếu có)
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                imageUrl = fileUploadsService.uploadImage(request.getImage());
            } catch (IOException e) {
                throw new IOException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage());
            }
        }

        // 3. Map thủ công CreateFoodRequest → Food
        Food food = Food.builder()
                .foodName(request.getFoodName())
                .caloriesPer100g(request.getCaloriesPer100g())

                // nutritional values
                .proteinPer100g(orZero(request.getProteinPer100g()))
                .fatPer100g(orZero(request.getFatPer100g()))
                .fiberPer100g(orZero(request.getFiberPer100g()))
                .sugarPer100g(orZero(request.getSugarPer100g()))

                // recipe info
                .instructions(request.getInstructions())
                .prepTime(request.getPrepTime())
                .cookTime(request.getCookTime())
                .servings(request.getServings() != null ? request.getServings() : 1)//neu ko gui khau phan an thi mac dinh la 1
                .mealType(request.getMealType() !=null ? request.getMealType() : MealType.ALL)
                .difficultyLevel(request.getDifficultyLevel())


                // image
                .imageUrl(imageUrl)

                // category
                .foodCategory(category)
                .goal(request.getGoal())
                .build();


        Food saved = foodRepository.save(food);
        
        // Gửi thông báo qua WebSocket
        notificationService.notifyNewFood(saved.getFoodId(), saved.getFoodName());
        
        // 5. Build Response
        return FoodCreateResponse.builder()
                .foodId(saved.getFoodId())
                .foodName(saved.getFoodName())
                .caloriesPer100g(saved.getCaloriesPer100g())
                .proteinPer100g(saved.getProteinPer100g())
                .fatPer100g(saved.getFatPer100g())
                .fiberPer100g(saved.getFiberPer100g())
                .sugarPer100g(saved.getSugarPer100g())
                .instructions(saved.getInstructions())
                .prepTime(saved.getPrepTime())
                .cookTime(saved.getCookTime())
                .servings(saved.getServings())
                .difficultyLevel(saved.getDifficultyLevel())
                .imageUrl(saved.getImageUrl())
                .createdAt(saved.getCreatedAt())
                .categoryResponse(buildCategoryResponse(category))
                .goal(saved.getGoal())
                .build();
    }


    public Page<FoodCreateResponse> searchFood(FoodSearchRequest req) {
        log.info("[SEARCH_FOOD] request={}", req);
        try {
            Specification<Food> spec = Specification
                    .where(FoodSpecification.nameContains(req.getKeyword()))
                    .and(FoodSpecification.hasMealType(req.getMealType()))
                    .and(FoodSpecification.hasDifficulty(req.getDifficultyLevel()))
                    .and(FoodSpecification.caloriesBetween(
                            req.getMinCalories(),
                            req.getMaxCalories()
                    ))
                    .and(FoodSpecification.hasCategory(req.getCategoryId()))
                    .and(FoodSpecification.hasCategoryName(req.getCategoryName()));

            Sort sort = Sort.by(
                    Sort.Direction.fromString(req.getSortDir()),
                    req.getSortBy()
            );

            Pageable pageable = PageRequest.of(
                    req.getPage(),
                    req.getSize(),
                    sort
            );

            Page<Food> pageResult = foodRepository.findAll(spec, pageable);

            if (pageResult.isEmpty()) {
                log.warn("[SEARCH_FOOD] No food found, request={}", req);
                throw new AppException(ErrorCode.FOOD_NOT_FOUND);
            }

            log.info("[SEARCH_FOOD] totalElements={}, totalPages={}",
                    pageResult.getTotalElements(),
                    pageResult.getTotalPages()
            );

            return pageResult.map(foodMapper::toFoodResponse);

        } catch (AppException ex) {
            // đã là lỗi nghiệp vụ → throw lại
            throw ex;

        } catch (Exception ex) {
            // lỗi không xác định
            log.error("[SEARCH_FOOD_ERROR] request={}", req, ex);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }


    // ==== Tách riêng hàm build CategoryResponse ====
    private FoodCategoryResponse buildCategoryResponse(FoodCategory category) {
        if (category == null) return null;
        return FoodCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }




    @Cacheable(
            value = "foodsByGoal",
            key = "#email"
    )
    public List<FoodCreateResponse> getAllFoods(String email) {
        User user=userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserProfile profile= user.getUserProfile();
        if (profile == null || profile.getHealthGoal() == null) {
            throw new AppException(ErrorCode.USER_PROFILE_NULL);
        }

        Goal goal = profile.getHealthGoal();
        List<Food> foods = foodRepository.findByGoal(goal);
        return foods.stream()
                .map(foodMapper::toFoodResponse)
                .collect(Collectors.toList());

    }

    @Cacheable(value = "foodsByMeal", key = "#email + '_' + #mealType")
    public List<FoodCreateResponse> getFoodsByMealType(String email,MealType mealType) {


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // 2. Lấy goal từ profile
        UserProfile profile = user.getUserProfile();
        if (profile == null || profile.getHealthGoal() == null) {
            throw new AppException(ErrorCode.USER_PROFILE_NULL);
        }

        Goal goal = profile.getHealthGoal();
        List<Food> foods = foodRepository.findByGoalAndMealType(goal, mealType);


        return foods.stream()
                .map(food -> foodMapper.toFoodResponse(food))
                .collect(Collectors.toList());
    }

    // Lấy danh sách món ăn theo category
    @Cacheable(value = "foodsByCategory", key = "#email + '_' + #categoryId")
    public List<FoodCreateResponse> getFoodsByCategory(String email, Long categoryId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        UserProfile profile = user.getUserProfile();
        if (profile == null || profile.getHealthGoal() == null) {
            throw new AppException(ErrorCode.USER_PROFILE_NULL);
        }

        Goal goal = profile.getHealthGoal();
        List<Food> foods = foodRepository.findByGoalAndFoodCategory_CategoryId(goal, categoryId);

        return foods.stream()
                .map(foodMapper::toFoodResponse)
                .collect(Collectors.toList());
    }


    @Caching(evict = {
            @CacheEvict(value = "foodDetail", key = "#foodId"),
            @CacheEvict(value = "foodsByGoal", allEntries = true),
            @CacheEvict(value = "foodsByMeal", allEntries = true)
    })
    public void deleteFood(Long foodId) {

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        foodRepository.delete(food);
    }

    @Caching(evict = {
            @CacheEvict(value = "foodDetail", key = "#foodId"),
            @CacheEvict(value = "foodsByGoal", allEntries = true),
            @CacheEvict(value = "foodsByMeal", allEntries = true)
    })
    public FoodCreateResponse updateFood(Long foodId, UpdateFoodRequest request) throws IOException {

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        // Category: DTO đã validate categoryId không null (nếu em bắt buộc)
        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_CATEGORY_NOT_FOUND));
        food.setFoodCategory(category);

        // Ảnh: thường sẽ để optional (DTO không bắt buộc)
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = fileUploadsService.uploadImage(request.getImage());
            food.setImageUrl(imageUrl);
        }
        // Nếu không gửi ảnh -> giữ nguyên ảnh cũ (không set null)

        // Map thẳng (DTO đảm bảo dữ liệu hợp lệ)
        food.setFoodName(request.getFoodName());
        food.setCaloriesPer100g(request.getCaloriesPer100g());

        food.setProteinPer100g(orZero(request.getProteinPer100g()));
        food.setFatPer100g(orZero(request.getFatPer100g()));
        food.setFiberPer100g(orZero(request.getFiberPer100g()));
        food.setSugarPer100g(orZero(request.getSugarPer100g()));

        food.setInstructions(request.getInstructions());
        food.setPrepTime(request.getPrepTime());
        food.setCookTime(request.getCookTime());
        food.setServings(request.getServings());

        food.setMealType(request.getMealType());
        food.setDifficultyLevel(request.getDifficultyLevel());

        food.setGoal(request.getGoal());

        Food saved = foodRepository.save(food);
        return foodMapper.toFoodResponse(saved);
    }

    private Double orZero(Double v) {
        return v != null ? v : 0.0;
    }



}

