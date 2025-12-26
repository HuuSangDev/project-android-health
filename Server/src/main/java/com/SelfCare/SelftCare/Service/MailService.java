package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.UserForgotPassword;
import com.SelfCare.SelftCare.DTO.Request.UserVerifyRequest;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {

    JavaMailSender javaMailSender;
    StringRedisTemplate redisTemplate;
    UserRepository userRepository;

    // 🔹 Tạo OTP 6 số ngẫu nhiên
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // 🔹 Gửi OTP đến email người dùng
    public void sendOtp(UserForgotPassword userForgotPassword) {

        String mail=userForgotPassword.getEmail();
        // Kiểm tra email tồn tại trong hệ thống
        if (!userRepository.existsByEmail(mail)) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
            
        }

        String otp = generateOtp();
        redisTemplate.opsForValue().set("otp:" + mail, otp, 5, TimeUnit.MINUTES);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail);
        message.setSubject("Mã xác thực đặt lại mật khẩu");
        message.setText("Mã OTP của bạn là: " + otp + "\nCó hiệu lực trong 5 phút.");
        javaMailSender.send(message);
    }

    public boolean verifyOtp(UserVerifyRequest request ) {
        String email=request.getEmail();
        String inputOtp=request.getOtp();
        String storedOtp = redisTemplate.opsForValue().get("otp:" + email);

        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            // Khi OTP đúng -> đánh dấu đã xác thực
            redisTemplate.delete("otp:" + email);
            redisTemplate.opsForValue().set("otp_verified:" + email, "true", 5, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    // 🔹 Kiểm tra email đã xác thực OTP chưa
    public boolean isOtpVerified(String email) {
        String verified = redisTemplate.opsForValue().get("otp_verified:" + email);
        return verified != null && verified.equals("true");
    }

    // 🔹 Xóa flag OTP và xác thực (sau khi reset xong)
    public void clearOtp(String email) {
        redisTemplate.delete("otp:" + email);
        redisTemplate.delete("otp_verified:" + email);
    }
}
