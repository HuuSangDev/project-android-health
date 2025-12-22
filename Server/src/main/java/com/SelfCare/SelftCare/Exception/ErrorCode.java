package com.SelfCare.SelftCare.Exception;

public enum ErrorCode {

    EMAIL_EXISTED(101,"email existed"),
    USER_NOT_FOUND(104,"user not found "),
    PROFILE_NOT_FOUND(104,"user profile not found "),
    EMAIL_NOT_FOUND(102,"email not found"),
    UNAUTHENTICATED(999,"unauthenticated"),
    INVALID_OTP(103,"invalid otp"),
    FOOD_NOT_FOUND(201,"food not found"),
    FOOD_CATEGORY_NOT_FOUND(202,"food category not found"),
    EXERCISE_NOT_FOUND(203,"exercise not found"),
    EXERCISE_CATEGORY_NOT_FOUND(204,"exercise category not found"),
    FORBIDDEN(205,"permission denied"),
    GEMINI_API_ERROR(300,"gemini api error"),
    UNCATEGORIZED_EXCEPTION(500,"uncategorized exception"),
    INVALID_PASSWORD(106, "invalid password"),
    PASSWORD_NOT_MATCH(107, "password not match"),
    USER_PROFILE_NULL(188,"userprofile is null "),
    NOTIFICATION_NOT_FOUND(206,"notification not found")
    ;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
