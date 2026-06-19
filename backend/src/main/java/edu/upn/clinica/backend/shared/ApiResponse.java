package edu.upn.clinica.backend.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String  message;
    private T       data;
    private String  operation;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data    = data;
    }

    private ApiResponse(boolean success, String message, T data, String operation) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.operation = operation;
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> ok(OperationType operation, String message, T data) {
        return new ApiResponse<>(true, message, data, operation.getCode());
    }

    public static <T> ApiResponse<T> ok(OperationType operation, T data) {
        return new ApiResponse<>(true, operation.getSuccessMessage(), data, operation.getCode());
    }

    public static <T> ApiResponse<T> ok(OperationType operation) {
        return new ApiResponse<>(true, operation.getSuccessMessage(), null, operation.getCode());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(OperationType operation, String detail) {
        return new ApiResponse<>(false, operation.getErrorMessage() + ": " + detail, null, operation.getCode());
    }

    public static <T> ApiResponse<T> error(OperationType operation) {
        return new ApiResponse<>(false, operation.getErrorMessage(), null, operation.getCode());
    }

    public boolean isSuccess() { return success; }
    public String  getMessage(){ return message;  }
    public T       getData()   { return data;     }
    public String  getOperation() { return operation; }
}
