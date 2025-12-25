package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Đếm users được tạo sau một thời điểm nhất định (để tính active users)
    long countByCreatedAtAfter(LocalDateTime dateTime);
}
