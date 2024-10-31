package com.ssafy.sandbox.oauth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoAccount {

    @JsonProperty("profile")
    private Profile profile;
}
