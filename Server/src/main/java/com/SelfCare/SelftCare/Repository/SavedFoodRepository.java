package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.SavedFood;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedFoodRepository extends JpaRepository<SavedFood, Long> {

    // Tìm tất cả món ăn đã lưu của user
    @Query("SELECT sf FROM SavedFood sf JOIN FETCH sf.food WHERE sf.user.id = :userId ORDER BY sf.savedAt DESC")
    List<SavedFood> findByUserIdOrderBySavedAtDesc(@Param("userId") Long userId);

    // Kiểm tra user đã lưu món ăn này chưa
    Optional<SavedFood> findByUserAndFood(User user, Food food);

    // Xóa món ăn đã lưu
    void deleteByUserAndFood(User user, Food food);

    // Đếm số món ăn đã lưu của user
    long countByUserId(Long userId);
}