package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.UpdateAvatarRequest;
import com.SelfCare.SelftCare.DTO.Request.UpdateUserProfileRequest;
import com.SelfCare.SelftCare.DTO.Request.UserProfileRequest;
import com.SelfCare.SelftCare.DTO.Response.UserProfileResponse;
import com.SelfCare.SelftCare.DTO.Response.UserResponse;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Entity.UserProfile;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Mapper.UserProfileMapper;
import com.SelfCare.SelftCare.Repository.UserProfileRepository;
import com.SelfCare.SelftCare.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserProfileService {


    UserProfileRepository userProfileRepository;
    UserRepository userRepository;
    UserProfileMapper userProfileMapper;
    FileUploadsService fileUploadsService;


    @Transactional

    public UserResponse updateProfile(String email, UpdateUserProfileRequest request) throws IOException {
       ;
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Lấy Profile, nếu chưa có thì có thể tạo mới (tuỳ logic app bạn, ở đây mình giữ nguyên throw exception)
        UserProfile profile = user.getUserProfile();


        // 3. Xử lý Upload Avatar (Chỉ upload nếu người dùng có gửi file)
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            try {
                // Gọi hàm upload Cloudinary vừa viết
                String avatarUrl = fileUploadsService.uploadImage(request.getAvatar());


                profile.setAvatarUrl(avatarUrl);
            } catch (IOException e) {

                throw new IOException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage());
            }
        }


        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getHeight() != null) profile.setHeight(request.getHeight());
        if (request.getWeight() != null) profile.setWeight(request.getWeight());
        if (request.getHealthGoal() != null) profile.setHealthGoal(request.getHealthGoal());

        // 5. Lưu vào DB
        userProfileRepository.save(profile);

        // 6. Trả về Response
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userProfileResponse(UserProfileResponse.builder()
                        .avatarUrl(profile.getAvatarUrl()) // Trả về link Cloudinary
                        .dateOfBirth(profile.getDateOfBirth())
                        .gender(profile.getGender())
                        .height(profile.getHeight())
                        .weight(profile.getWeight())
                        .healthGoal(profile.getHealthGoal())
                        .build())
                .build();
    }



    public UserResponse getMyProfile(String email )
    {

        User user=userRepository.findByEmail(email)
                .orElseThrow(() -> new  AppException(ErrorCode.EMAIL_NOT_FOUND));

        return toUseResponse(user);
    }


    //truyen vao 1 object user
    private UserResponse toUseResponse(User user)
    {
        var profile = user.getUserProfile();
        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .avatarUrl(profile.getAvatarUrl())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .healthGoal(profile.getHealthGoal()) // Giả sử field này khớp kiểu dữ liệu
                .build();


        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userProfileResponse(profileResponse)
                .build();

    }

    @Transactional

    public UserResponse updateAvatar(String email, UpdateAvatarRequest request) throws IOException {

        // 1. Check user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }

        // 2. Validate avatar
        if (request.getAvatar() == null || request.getAvatar().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Avatar không được để trống");
        }

        try {
            // 3. Upload Cloudinary
            String avatarUrl = fileUploadsService.uploadImage(request.getAvatar());

            // 4. Update avatar
            profile.setAvatarUrl(avatarUrl);
            userProfileRepository.save(profile);

            // 5. Return response
            return toUseResponse(user);

        } catch (IOException e) {
            throw new IOException("Lỗi khi upload avatar: " + e.getMessage());
        }
    }



}
