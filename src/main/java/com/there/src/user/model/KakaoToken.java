package com.there.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoToken {
    private String accesstoken;
    private String refreshtoken;
}
