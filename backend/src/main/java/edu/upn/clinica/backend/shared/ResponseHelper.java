package edu.upn.clinica.backend.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {

    private ResponseHelper() {}

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(OperationType.REGISTRO, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(OperationType.REGISTRO, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> updated(T data) {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.ACTUALIZACION, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> updated(String message, T data) {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.ACTUALIZACION, message, data));
    }

    public static ResponseEntity<ApiResponse<Void>> deleted() {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.ELIMINACION));
    }

    public static ResponseEntity<ApiResponse<Void>> deleted(String message) {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.ELIMINACION, message, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> accepted(T data) {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.ACEPTACION, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> accepted(String message, T data) {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.ACEPTACION, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> executed(T data) {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.EJECUCION, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> executed(String message, T data) {
        return ResponseEntity.ok(ApiResponse.ok(OperationType.EJECUCION, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.ok(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message) {
        return ResponseEntity.ok(ApiResponse.ok(message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(OperationType operation, String detail, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(operation, detail));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(OperationType operation, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(operation));
    }
}
