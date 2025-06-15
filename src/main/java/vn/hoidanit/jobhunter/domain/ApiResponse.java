package vn.hoidanit.jobhunter.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int status;
    private String errorCode;
    private Object message;
    private T data;

    private LocalDateTime timestamp = LocalDateTime.now();

}
