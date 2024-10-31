package com.ssafy.sandbox.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoMemberResponseDto {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
}
