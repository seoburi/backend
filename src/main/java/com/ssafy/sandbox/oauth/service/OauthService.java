package com.ssafy.sandbox.oauth.service;

import com.ssafy.sandbox.oauth.dto.KakaoMemberResponseDto;
import com.ssafy.sandbox.oauth.dto.KakaoTokenResponseDto;
import com.ssafy.sandbox.oauth.dto.RefreshTokenRequestDto;
import com.ssafy.sandbox.oauth.dto.RequestAuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final String kakaoGetTokenUrl = "https://kauth.kakao.com/oauth/token";
    private final String kakaoGetUserUrl = "https://kapi.kakao.com/v2/user/me";
    private final String restApiKey = "0da56a0700a56821782b91c49ce03b42";
    private final String redirectUrl = "https://ssafysandbox.vercel.app/oauth/redirect";

    private final RestTemplate restTemplate;

    public KakaoTokenResponseDto getAccessTokenFromKakao(RequestAuthDto requestAuthDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", restApiKey);
        params.add("redirect_uri", redirectUrl);
        params.add("code", requestAuthDto.getCode());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        return restTemplate.postForObject(kakaoGetTokenUrl, httpEntity, KakaoTokenResponseDto.class);
    }

    public KakaoMemberResponseDto getMemberWithAccessToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("property_keys", "[\"kakao_account.profile.nickname\"]");


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        return restTemplate.postForObject(kakaoGetUserUrl, httpEntity, KakaoMemberResponseDto.class);
    }

    public KakaoTokenResponseDto getAccessTokenFromKakaoWithRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", restApiKey);
        params.add("refresh_token", refreshTokenRequestDto.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        return restTemplate.postForObject(kakaoGetTokenUrl, httpEntity, KakaoTokenResponseDto.class);
    }
}
