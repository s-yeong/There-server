package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class GetUserInfoRes {

    private String nickName;
    private String profileImgUrl;

}
