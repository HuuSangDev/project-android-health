package com.SelfCare.SelftCare.Exception;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.valueextraction.ExtractedValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandleException {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException appException)
    {
        ErrorCode erorrCode=appException.getErrorCode();
        ApiResponse apiResponse=new ApiResponse();
        // Sử dụng message từ exception nếu có, nếu không thì dùng message mặc định từ ErrorCode
        String message = appException.getMessage() != null && !appException.getMessage().equals(erorrCode.getMessage()) 
            ? appException.getMessage() 
            : erorrCode.getMessage();
        apiResponse.setMessage(message);
        apiResponse.setCode(erorrCode.getCode());

        return ResponseEntity.badRequest().body(apiResponse);

    }


    //bat loi valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Invalid input");

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage(errorMessage);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 4. Bắt lỗi ràng buộc (ví dụ: @Min, @Max trên @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("Dữ liệu không hợp lệ");

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage(errorMessage);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 5. Bắt lỗi chung (không đoán trước được)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(500);
        apiResponse.setMessage("Lỗi hệ thống: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
