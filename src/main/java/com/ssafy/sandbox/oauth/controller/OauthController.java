package com.ssafy.sandbox.oauth.controller;


import com.ssafy.sandbox.oauth.dto.*;
import com.ssafy.sandbox.oauth.service.OauthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @PostMapping("/auth")
    public ResponseEntity<ResponseAuthDto> getAccessTokenFromKakao(@RequestBody RequestAuthDto requestAuthDto) {
        KakaoTokenResponseDto kakaoTokenResponseDto = oauthService.getAccessTokenFromKakao(requestAuthDto);
        return ResponseEntity.ok()
                .header("Set-Cookie", "refreshToken=" + kakaoTokenResponseDto.getRefreshToken() +"; sameSite=None; httponly; secure")
                .body(new ResponseAuthDto(kakaoTokenResponseDto.getAccessToken()));
    }

    @GetMapping("/member")
    public ResponseEntity<?> getMemberWithAccessToken(@RequestHeader(value = "Authorization", required = false) String authcode) {

        //Authorizaion Code가 전달되지 않은 경우 예외 처리 -> 401에러 리턴
        if (authcode == null || authcode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponseDto.of(HttpStatus.UNAUTHORIZED));
        }

        KakaoMemberResponseDto kakaoMemberResponseDto = oauthService.getMemberWithAccessToken(authcode.substring(7));

//        //사용자를 찾을 수 없는 경우 -> 404에러 리턴
//        if (memberResponseDto == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(ErrorResponseDto.of(HttpStatus.NOT_FOUND));
//        }

        return ResponseEntity.ok().body(MemberResponseDto.of(kakaoMemberResponseDto));
    }

    @GetMapping("/reissue")
    public ResponseEntity<?> getNewAccessTokenFromRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.equals("")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponseDto.of(HttpStatus.UNAUTHORIZED));
        }
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto(refreshToken);
        KakaoTokenResponseDto kakaoTokenResponseDto = oauthService.getAccessTokenFromKakaoWithRefreshToken(refreshTokenRequestDto);
        return ResponseEntity.ok().body(new ResponseAuthDto(kakaoTokenResponseDto.getAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    cookie.setValue(null);
                    break;
                }
            }
        }

//        if (refreshToken == null || refreshToken.equals("")) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(ErrorResponseDto.of(HttpStatus.UNAUTHORIZED));
//        } else if (refreshToken.equals("...")) {
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(ErrorResponseDto.of(HttpStatus.FORBIDDEN));
//        }


        return ResponseEntity.ok()
                .header("Set-Cookie", "refreshToken=...")
                .body(null);
    }


    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();
    }
}
