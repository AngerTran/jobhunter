package vn.hoidanit.jobhunter.util.error;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.hoidanit.jobhunter.domain.ApiResponse;

@RestControllerAdvice
public class GobalException {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NoSuchElementException ex) {
        var rs = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "handleNotFound", null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rs);
    }

    // class cha cho tat ca cac execp chua dinh nghia
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ApiResponse<?>> handleAllException(Exception ex) {
    // var rs = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
    // "handleAllException", null, ex.getMessage());
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rs);
    // }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        String errors = String.join("; ", errorList);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, errors, null, "VALIDATION_ERROR");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllException(Exception ex) {
        var result = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null,
                "INTERNAL_SERVER_ERROR");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(Exception ex) {
        ApiResponse<Object> res = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "Authentication failed: " + ex.getMessage(),
                null,
                "AUTHENTICATION_ERROR");
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}
