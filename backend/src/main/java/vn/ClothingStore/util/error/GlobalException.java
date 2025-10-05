package vn.ClothingStore.util.error;

import jakarta.validation.ConstraintViolationException;
import vn.ClothingStore.domain.response.RestResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    // ===== Helpers =====
    private ResponseEntity<RestResponse<Object>> build(HttpStatus status, String error, Object message) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status.value());
        res.setError(error);
        res.setMessage(message);
        return ResponseEntity.status(status).body(res);
    }

    // ===== Authn/Authz (ưu tiên dùng AuthenticationEntryPoint/AccessDeniedHandler
    // ở Security filter) =====

    // Sai username hoặc mật khẩu -> 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        // KHÔNG phân biệt cụ thể để tránh lộ thông tin tài khoản
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Username hoặc mật khẩu không đúng");
    }

    // Cho phép hiển thị khi bạn đã setHideUserNotFoundExceptions(false)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    // Thiếu/Token sai
    @ExceptionHandler({ BadJwtException.class, JwtException.class })
    public ResponseEntity<RestResponse<Object>> handleJwt(Exception ex) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Token không hợp lệ hoặc đã hết hạn");
    }

    // Không đủ quyền -> 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Forbidden", "Bạn không có quyền truy cập tài nguyên này");
    }

    // ===== Validation/Request errors -> 400 =====

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<String> messages = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        Object message = messages.size() <= 1 ? (messages.isEmpty() ? "Yêu cầu không hợp lệ" : messages.get(0))
                : messages;
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .collect(Collectors.toList());
        Object message = messages.size() <= 1 ? (messages.isEmpty() ? "Yêu cầu không hợp lệ" : messages.get(0))
                : messages;
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class,

    })
    public ResponseEntity<RestResponse<Object>> handleBadRequest(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // ===== Routing/HTTP protocol =====

    // Sai method -> 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", ex.getMessage());
    }

    // Sai Content-Type -> 415
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RestResponse<Object>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type", ex.getMessage());
    }

    // Không tìm thấy endpoint -> 404
    @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
    public ResponseEntity<RestResponse<Object>> handleNotFound(Exception ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "URL không tồn tại");
    }

    // Thiếu path variable hoặc mismatch nghiêm trọng -> 400 (tuỳ chọn)
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<RestResponse<Object>> handleMissingPathVar(MissingPathVariableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // ===== App-specific =====

    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdInvalid(IdInvalidException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<RestResponse<Object>> handleIdInvalid(StorageException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // ===== Fallback: lỗi không lường trước -> 500 =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAll(Exception ex) {
        // Có thể ẩn message chi tiết trong production
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    // ===== Bắt lỗi parse Enum -> 400 =====
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestResponse<Object>> handleInvalidFormat(InvalidFormatException ex) {
        if (ex.getTargetType().isEnum()) {
            String message = "Giá trị không hợp lệ: " + ex.getValue()
                    + ". Hãy chọn một trong: "
                    + java.util.Arrays.toString(ex.getTargetType().getEnumConstants());

            return build(HttpStatus.BAD_REQUEST, "Invalid enum value", message);
        }
        return build(HttpStatus.BAD_REQUEST, "Invalid format", ex.getMessage());
    }
}
