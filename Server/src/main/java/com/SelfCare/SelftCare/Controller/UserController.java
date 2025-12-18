package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.ChangePasswordRequest;
import com.SelfCare.SelftCare.DTO.Request.UserProfileRequest;
import com.SelfCare.SelftCare.DTO.Request.UserRegisterRequest;
import com.SelfCare.SelftCare.DTO.Response.UserResponse;
import com.SelfCare.SelftCare.Service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Builder
@RequestMapping("/Users")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/Register")
    ApiResponse<UserResponse>UserRegister(@RequestBody @Valid UserRegisterRequest request)
    {
        return ApiResponse.<UserResponse>builder()
                .message("Register user success")
                .result(userService.userRegister(request))
                .build();
    }

    @PutMapping("/change-password")
    ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .result("Đổi mật khẩu thành công")
                .build();

    }




}
