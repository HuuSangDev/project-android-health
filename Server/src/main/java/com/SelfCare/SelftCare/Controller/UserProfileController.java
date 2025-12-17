package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.UpdateAvatarRequest;
import com.SelfCare.SelftCare.DTO.Request.UpdateUserProfileRequest;
import com.SelfCare.SelftCare.DTO.Request.UserProfileRequest;
import com.SelfCare.SelftCare.DTO.Response.UserResponse;
import com.SelfCare.SelftCare.Service.UserProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Builder
@RequestMapping("/userProfile")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserProfileController {


    UserProfileService userProfileService;


    @PutMapping(value = "update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponse> UserProfileUpdate(@ModelAttribute UpdateUserProfileRequest request) throws IOException {
        var Auth= SecurityContextHolder.getContext().getAuthentication();
        String name=Auth.getName();
        return ApiResponse.<UserResponse>builder()
                .message("update profile success")
                .result(userProfileService.updateProfile(name,request))
                .build();
    }

    @GetMapping("/get-my-profile")
    ApiResponse<UserResponse> getMyProfile()
    {
        var Auth= SecurityContextHolder.getContext().getAuthentication();
        String name=Auth.getName();
        return ApiResponse.<UserResponse>builder()
                .message("get my profile success ")
                .result(userProfileService.getMyProfile(name))
                .build();
    }

    @PutMapping("/avatar")
    public UserResponse updateAvatar(
            @RequestParam("avatar") MultipartFile avatar
    ) throws IOException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userProfileService.updateAvatar(
                email,
                new UpdateAvatarRequest(avatar)
        );
    }
}
