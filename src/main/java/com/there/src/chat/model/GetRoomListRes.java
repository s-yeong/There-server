package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRoomListRes {

    private int roomIdx;
    private int count;
    private String nickName;
    private String profileImgUrl;

}
