package com.ssafy.sandbox.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponseDto {
    private String code;

    private ErrorResponseDto (String code){
        this.code = code;
    }

    public static ErrorResponseDto of (HttpStatus status) {
        if (HttpStatus.UNAUTHORIZED.equals(status)) {
            return new ErrorResponseDto("ERR_ACCESS_TOKEN_EXPIRED");
        } else if (HttpStatus.NOT_FOUND.equals(status)) {
            return new ErrorResponseDto("ERR_NOT_FOUND_MEMBER");
        } else if (HttpStatus.FORBIDDEN.equals(status)) {
            return new ErrorResponseDto("ERR_ALREADY_LOGGED_OUT");
        }

        else return null;
    }
}
