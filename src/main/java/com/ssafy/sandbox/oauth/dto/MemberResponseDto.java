package com.ssafy.sandbox.oauth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponseDto {
    private String nickname;


    private MemberResponseDto(String nickname) {
        this.nickname = nickname;
    }

    public static MemberResponseDto of(KakaoMemberResponseDto kakaoMemberResponseDto) {
        return new MemberResponseDto(kakaoMemberResponseDto.getKakaoAccount().getProfile().getNickname());
    }
}
