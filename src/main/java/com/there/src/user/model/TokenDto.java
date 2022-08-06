package com.there.src.user.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String granType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireDate;
}
