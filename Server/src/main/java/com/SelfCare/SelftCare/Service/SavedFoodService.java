package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.SaveFoodRequest;
import com.SelfCare.SelftCare.DTO.Response.SavedFoodResponse;
import com.SelfCare.SelftCare.Entity.Food;
import com.SelfCare.SelftCare.Entity.SavedFood;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Repository.FoodRepository;
import com.SelfCare.SelftCare.Repository.SavedFoodRepository;
import com.SelfCare.SelftCare.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SavedFoodService {

    SavedFoodRepository savedFoodRepository;
    UserRepository userRepository;
    FoodRepository foodRepository;

    @Transactional
    public String saveFood(String email, SaveFoodRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        // Kiểm tra đã lưu chưa
        Optional<SavedFood> existingSavedFood = savedFoodRepository.findByUserAndFood(user, food);
        
        if (existingSavedFood.isPresent()) {
            return "Món ăn đã được lưu trước đó";
        }

        // Lưu món ăn mới
        SavedFood savedFood = SavedFood.builder()
                .user(user)
                .food(food)
                .build();

        savedFoodRepository.save(savedFood);
        log.info("User {} saved food {}", email, food.getFoodName());
        
        return "Đã lưu món ăn thành công";
    }

    @Transactional
    public String unsaveFood(String email, Long foodId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        savedFoodRepository.deleteByUserAndFood(user, food);
        log.info("User {} unsaved food {}", email, food.getFoodName());
        
        return "Đã bỏ lưu món ăn";
    }

    public List<SavedFoodResponse> getSavedFoods(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<SavedFood> savedFoods = savedFoodRepository.findByUserIdOrderBySavedAtDesc(user.getId());

        return savedFoods.stream()
                .map(this::mapToSavedFoodResponse)
                .collect(Collectors.toList());
    }

    public boolean isFoodSaved(String email, Long foodId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        return savedFoodRepository.findByUserAndFood(user, food).isPresent();
    }

    private SavedFoodResponse mapToSavedFoodResponse(SavedFood savedFood) {
        Food food = savedFood.getFood();
        
        return SavedFoodResponse.builder()
                .savedFoodId(savedFood.getSavedFoodId())
                .foodId(food.getFoodId())
                .foodName(food.getFoodName())
                .imageUrl(food.getImageUrl())
                .caloriesPer100g(food.getCaloriesPer100g())
                .prepTime(food.getPrepTime())
                .cookTime(food.getCookTime())
                .mealType(food.getMealType() != null ? food.getMealType().name() : null)
                .difficultyLevel(food.getDifficultyLevel() != null ? food.getDifficultyLevel().name() : null)
                .savedAt(savedFood.getSavedAt())
                .build();
    }
}