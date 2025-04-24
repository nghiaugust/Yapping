package com.yapping.dto;

public class ApiResponse {
    private int status;
    private boolean success;
    private String message;
    private Object data;

    public ApiResponse(int status, boolean success, String message, Object data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters và Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}