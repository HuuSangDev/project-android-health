package com.example.app_selfcare.Data.remote;

import com.example.app_selfcare.Data.Model.Request.ChatRequest;
import com.example.app_selfcare.Data.Model.Request.ChangePasswordRequest;
import com.example.app_selfcare.Data.Model.Request.FoodCategoryCreateRequest;
import com.example.app_selfcare.Data.Model.Request.FoodSearchRequest;
import com.example.app_selfcare.Data.Model.Request.ForgotPasswordRequest;
import com.example.app_selfcare.Data.Model.Request.ResetPasswordRequest;
import com.example.app_selfcare.Data.Model.Request.UserLoginRequest;
import com.example.app_selfcare.Data.Model.Request.UserRegisterRequest;
import com.example.app_selfcare.Data.Model.Request.VerifyOtpRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ChatResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCategoryResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.Model.Response.PageResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.Model.Response.UserLoginResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

public interface ApiService {

    @POST("app/auth/login")
    Call<ApiResponse<UserLoginResponse>> login(@Body UserLoginRequest request);

    @POST("app/Users/Register")
    Call<ApiResponse<UserResponse>> register(@Body UserRegisterRequest request);

    @GET("app/userProfile/get-my-profile")
    Call<ApiResponse<UserResponse>> getUserProfile(
            @Header("Authorization") String token
    );

    @Multipart
    @PUT("app/userProfile/update")
    Call<ApiResponse<UserResponse>> updateProfile(
            @Part("gender") RequestBody gender,
            @Part("dateOfBirth") RequestBody dateOfBirth,
            @Part("height") RequestBody height,
            @Part("weight") RequestBody weight,
            @Part("healthGoal") RequestBody healthGoal,
            @Part MultipartBody.Part avatar
    );

    @POST("app/auth/forgot-password")
    Call<ApiResponse<String>> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("app/auth/verify-otp")
    Call<ApiResponse<String>> verifyOtp(@Body VerifyOtpRequest request);

    @POST("app/auth/reset-password")
    Call<ApiResponse<String>> resetPassword(@Body ResetPasswordRequest request);

    @POST("app/api/chat")
    Call<ApiResponse<ChatResponse>> chat(@Body ChatRequest request);

    // Change password (yêu cầu đã đăng nhập, dùng Bearer token qua ApiClient.getClientWithToken)
    @PUT("app/Users/change-password")
    Call<ApiResponse<String>> changePassword(@Body ChangePasswordRequest request);

    // Food APIs
    @GET("app/foods/all")
    Call<ApiResponse<List<FoodResponse>>> getAllFoods();

    @GET("app/foods/meal/{mealType}")
    Call<ApiResponse<List<FoodResponse>>> getFoodsByMealType(@Path("mealType") String mealType);

    @GET("app/foods/{foodId}")
    Call<ApiResponse<FoodResponse>> getFoodById(@Path("foodId") int foodId);

    // Search/Filter Foods (Admin)
    @POST("app/foods/search")
    Call<ApiResponse<PageResponse<FoodResponse>>> searchFoods(@Body FoodSearchRequest request);

    // Exercises APIs
    @GET("app/exercises/all")
    Call<ApiResponse<java.util.List<ExerciseResponse>>> getExercises();

    @GET("app/exercises/{exerciseId}")
    Call<ApiResponse<ExerciseResponse>> getExerciseById(@Path("exerciseId") int exerciseId);

    // ==================== Food Category APIs ====================
    @GET("app/categories/all")
    Call<ApiResponse<List<FoodCategoryResponse>>> getAllFoodCategories();

    @POST("app/categories/create")
    Call<ApiResponse<FoodCategoryResponse>> createFoodCategory(@Body FoodCategoryCreateRequest request);

    @PUT("app/categories/{categoryId}")
    Call<ApiResponse<FoodCategoryResponse>> updateFoodCategory(
            @Path("categoryId") long categoryId,
            @Body FoodCategoryCreateRequest request);

    @DELETE("app/categories/{categoryId}")
    Call<ApiResponse<Void>> deleteFoodCategory(@Path("categoryId") long categoryId);

    // ==================== Admin Food APIs ====================
    @Multipart
    @POST("app/foods/create")
    Call<ApiResponse<FoodResponse>> createFood(
            @Part("foodName") RequestBody foodName,
            @Part("caloriesPer100g") RequestBody caloriesPer100g,
            @Part("proteinPer100g") RequestBody proteinPer100g,
            @Part("fatPer100g") RequestBody fatPer100g,
            @Part("fiberPer100g") RequestBody fiberPer100g,
            @Part("sugarPer100g") RequestBody sugarPer100g,
            @Part("instructions") RequestBody instructions,
            @Part("prepTime") RequestBody prepTime,
            @Part("cookTime") RequestBody cookTime,
            @Part("servings") RequestBody servings,
            @Part("mealType") RequestBody mealType,
            @Part("difficultyLevel") RequestBody difficultyLevel,
            @Part("categoryId") RequestBody categoryId,
            @Part("goal") RequestBody goal,
            @Part MultipartBody.Part image
    );

    @Multipart
    @PUT("app/foods/{foodId}")
    Call<ApiResponse<FoodResponse>> updateFood(
            @Path("foodId") long foodId,
            @Part("foodName") RequestBody foodName,
            @Part("caloriesPer100g") RequestBody caloriesPer100g,
            @Part("proteinPer100g") RequestBody proteinPer100g,
            @Part("fatPer100g") RequestBody fatPer100g,
            @Part("fiberPer100g") RequestBody fiberPer100g,
            @Part("sugarPer100g") RequestBody sugarPer100g,
            @Part("instructions") RequestBody instructions,
            @Part("prepTime") RequestBody prepTime,
            @Part("cookTime") RequestBody cookTime,
            @Part("servings") RequestBody servings,
            @Part("mealType") RequestBody mealType,
            @Part("difficultyLevel") RequestBody difficultyLevel,
            @Part("categoryId") RequestBody categoryId,
            @Part("goal") RequestBody goal,
            @Part MultipartBody.Part image
    );

    @DELETE("app/foods/{foodId}")
    Call<ApiResponse<Void>> deleteFood(@Path("foodId") long foodId);

}