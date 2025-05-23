package com.yapping.exception;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    // Xử lý lỗi 403 (Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage() != null ? e.getMessage() : "Bạn không có quyền truy cập tài nguyên này"
        );
        problemDetail.setTitle("Quyền truy cập bị từ chối");
        problemDetail.setType(URI.create("https://example.com/errors/forbidden"));
        problemDetail.setProperty("errorCode", "ACCESS_DENIED");

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    // Xử lý lỗi 401 (Unauthorized)
    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleAuthenticationException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                e.getMessage() != null ? e.getMessage() : "Thông tin xác thực không hợp lệ"
        );
        problemDetail.setTitle("Xác thực thất bại");
        problemDetail.setType(URI.create("https://example.com/errors/unauthorized"));
        problemDetail.setProperty("errorCode", "UNAUTHORIZED");

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    // Xử lý lỗi cấu hình
    @ExceptionHandler({BeanCreationException.class, ApplicationContextException.class})
    public ResponseEntity<ProblemDetail> handleConfigurationException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage() != null ? e.getMessage() : "Lỗi cấu hình hệ thống"
        );
        problemDetail.setTitle("Lỗi cấu hình");
        problemDetail.setType(URI.create("https://example.com/errors/configuration-error"));
        problemDetail.setProperty("errorCode", "CONFIGURATION_ERROR");

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage() != null ? e.getMessage() : "Yêu cầu không hợp lệ"
        );
        problemDetail.setTitle("Yêu cầu không hợp lệ");
        problemDetail.setType(URI.create("https://example.com/errors/invalid-request"));
        problemDetail.setProperty("errorCode", getErrorCode(e));

        // Dữ liệu bổ sung (nếu có)
        Map<String, Object> data = extractDataFromMessage(e.getMessage());
        if (!data.isEmpty()) {
            problemDetail.setProperty("data", data);
        }

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .filter(msg -> msg != null)
                .collect(Collectors.joining(", "));
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                errorMessage.isEmpty() ? "Dữ liệu đầu vào không hợp lệ" : errorMessage
        );
        problemDetail.setTitle("Lỗi xác thực dữ liệu");
        problemDetail.setType(URI.create("https://example.com/errors/validation-error"));
        problemDetail.setProperty("errorCode", "VALIDATION_FAILED");

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Dữ liệu yêu cầu không đúng định dạng"
        );
        problemDetail.setTitle("Lỗi định dạng yêu cầu");
        problemDetail.setType(URI.create("https://example.com/errors/invalid-request-body"));
        problemDetail.setProperty("errorCode", "INVALID_REQUEST_BODY");

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Đã xảy ra lỗi không mong muốn"
        );
        problemDetail.setTitle("Lỗi hệ thống");
        problemDetail.setType(URI.create("https://example.com/errors/server-error"));
        problemDetail.setProperty("errorCode", "SERVER_ERROR");

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private boolean isDevEnvironment() {
        return environment.getActiveProfiles().length == 0 ||
                environment.getActiveProfiles()[0].equals("dev");
    }

    private List<String> getLimitedStackTrace(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }

    private String getErrorCode(Exception e) {
        String message = e.getMessage();
        if (message.contains("Role") && message.contains("already assigned")) {
            return "ROLE_ALREADY_ASSIGNED";
        } else if (message.contains("User not found")) {
            return "USER_NOT_FOUND";
        } else if (message.contains("Role not found")) {
            return "ROLE_NOT_FOUND";        }
        return "INVALID_REQUEST";
    }

    // Xử lý lỗi 404 (Not Found) cho ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        problemDetail.setTitle("Tài nguyên không tìm thấy");
        problemDetail.setType(URI.create("https://example.com/errors/not-found"));
        problemDetail.setProperty("errorCode", "RESOURCE_NOT_FOUND");

        if (isDevEnvironment()) {
            problemDetail.setProperty("stackTrace", getLimitedStackTrace(e));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    private Map<String, Object> extractDataFromMessage(String message) {
        Map<String, Object> data = new HashMap<>();
        if (message.contains("Role") && message.contains("already assigned")) {
            String[] parts = message.split(" ");
            if (parts.length >= 8) {
                data.put("roleName", parts[1]);
                data.put("userId", parts[7]);
            }
        } else if (message.contains("User not found")) {
            data.put("userId", message.split(" ")[3]);
        } else if (message.contains("Role not found")) {
            data.put("roleName", message.split(" ")[2]);
        }
        return data;
    }
}