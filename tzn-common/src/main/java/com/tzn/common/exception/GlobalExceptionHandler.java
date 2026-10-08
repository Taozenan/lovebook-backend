package com.tzn.common.exception;

import com.tzn.common.api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** 为所有服务提供一致的 API 异常响应。 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
      IllegalArgumentException exception) {
    return badRequest(exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(
      MethodArgumentNotValidException exception) {
    FieldError error = exception.getBindingResult().getFieldError();
    return badRequest(error == null ? "请求参数不合法" : error.getDefaultMessage());
  }

  @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
  public ResponseEntity<ApiResponse<Void>> handleParameter(Exception exception) {
    return badRequest("请求参数不合法");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(
      HttpMessageNotReadableException exception) {
    return badRequest("请求体格式不正确");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.failure(500, "服务器内部错误"));
  }

  private ResponseEntity<ApiResponse<Void>> badRequest(String message) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(400, message));
  }
}
