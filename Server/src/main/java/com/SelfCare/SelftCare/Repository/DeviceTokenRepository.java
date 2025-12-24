package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    /**
     * Tìm device token theo token string
     */
    Optional<DeviceToken> findByToken(String token);

    /**
     * Lấy tất cả token active của một user
     */
    List<DeviceToken> findAllByUserIdAndIsActiveTrue(Long userId);

    /**
     * Lấy tất cả token active trong hệ thống (để gửi broadcast)
     */
    List<DeviceToken> findAllByIsActiveTrue();

    /**
     * Lấy tất cả token string active (để gửi FCM)
     */
    @Query("SELECT dt.token FROM DeviceToken dt WHERE dt.isActive = true")
    List<String> findAllActiveTokenStrings();

    /**
     * Lấy token string active của một user
     */
    @Query("SELECT dt.token FROM DeviceToken dt WHERE dt.user.id = :userId AND dt.isActive = true")
    List<String> findActiveTokenStringsByUserId(@Param("userId") Long userId);

    /**
     * Deactivate token (khi FCM báo token invalid)
     */
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.isActive = false WHERE dt.token = :token")
    void deactivateToken(@Param("token") String token);

    /**
     * Deactivate nhiều token cùng lúc
     */
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.isActive = false WHERE dt.token IN :tokens")
    void deactivateTokens(@Param("tokens") List<String> tokens);

    /**
     * Kiểm tra token đã tồn tại chưa
     */
    boolean existsByToken(String token);
}
