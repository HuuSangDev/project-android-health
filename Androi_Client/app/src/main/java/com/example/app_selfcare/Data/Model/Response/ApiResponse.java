package com.example.app_selfcare.Data.Model.Response;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T result;



    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getResult() { return result; }

}
