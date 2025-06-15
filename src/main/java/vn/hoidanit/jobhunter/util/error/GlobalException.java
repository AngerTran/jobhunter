package vn.hoidanit.jobhunter.util.error;

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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.ApiResponse;

@RestControllerAdvice
public class GlobalException {

    /* =================== NOT FOUND (NoSuchElement) =================== */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NoSuchElementException ex) {
        return buildError(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), "NOT_FOUND");
    }

    /* =================== 404 URL =================== */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND,
                "404 Not Found. URL may not exist",
                ex.getMessage(),
                "NOT_FOUND_URL");
    }

    /* =================== BAD REQUEST (ID | Auth) =================== */
    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class
    })
    public ResponseEntity<ApiResponse<?>> handleIdException(Exception ex) {
        return buildError(HttpStatus.BAD_REQUEST,
                "Exception occurs",
                ex.getMessage(),
                "BAD_REQUEST");
    }

    /* =================== VALIDATION ERROR =================== */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        return buildError(HttpStatus.BAD_REQUEST,
                String.join("; ", errors),
                "VALIDATION_ERROR",
                "VALIDATION_ERROR");
    }

    /* =================== INTERNAL SERVER ERROR =================== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllException(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                "UNEXPECTED_ERROR",
                "INTERNAL_SERVER_ERROR");
    }

    /* ---------- Helper chung để tránh lặp code ---------- */
    private ResponseEntity<ApiResponse<?>> buildError(HttpStatus status,
            String message,
            String errorDetail,
            String errorCode) {

        ApiResponse<Object> body = new ApiResponse<>();
        body.setStatus(status.value());
        body.setMessage(message);
        body.setData(null);
        body.setErrorCode(errorCode != null ? errorCode : errorDetail);

        return ResponseEntity.status(status).body(body);
    }
}
