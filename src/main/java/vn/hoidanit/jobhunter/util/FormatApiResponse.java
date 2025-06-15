package vn.hoidanit.jobhunter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.hoidanit.jobhunter.domain.ApiResponse;

@RestControllerAdvice
@RequiredArgsConstructor
public class FormatApiResponse implements ResponseBodyAdvice<Object> {

    private final ObjectMapper mapper;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true; // Apply to all responses
    }

    public Object beforeBodyWrite(Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        // Đã bọc rồi thì giữ nguyên
        if (body instanceof ApiResponse<?>) {
            return body;
        }

        HttpServletResponse servletResp = ((ServletServerHttpResponse) response).getServletResponse();
        HttpStatus httpStatus = HttpStatus.valueOf(servletResp.getStatus());

        /* ========== SUCCESS (2xx) ========== */
        if (httpStatus.is2xxSuccessful()) {

            ApiResponse<Object> wrapped = new ApiResponse<>();
            wrapped.setStatus(httpStatus.value());
            wrapped.setMessage("CALL API SUCCESS");
            wrapped.setData(body); // giữ nguyên data
            wrapped.setErrorCode(null); // không lỗi

            // Nếu controller trả String, phải serialize thủ công
            if (body instanceof String) {
                servletResp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                try {
                    return mapper.writeValueAsString(wrapped);
                } catch (Exception e) {
                    throw new RuntimeException("Serialize ApiResponse error", e);
                }
            }
            return wrapped;
        }

        /* ========== ERROR (>=400) ========== */
        String errorDetail = (body == null) ? null : body.toString();

        ApiResponse<Object> errorWrap = new ApiResponse<>();
        errorWrap.setStatus(httpStatus.value());
        errorWrap.setMessage("CALL API ERROR");
        errorWrap.setData(null);
        errorWrap.setErrorCode(errorDetail); // bạn có thể map mã lỗi tùy ý

        if (body instanceof String) {
            servletResp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try {
                return mapper.writeValueAsString(errorWrap);
            } catch (Exception e) {
                throw new RuntimeException("Serialize ApiResponse error", e);
            }
        }
        return errorWrap;
    }
}
