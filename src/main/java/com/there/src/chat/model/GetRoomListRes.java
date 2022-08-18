package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRoomListRes {

    private int roomIdx;
    private int senderIdx;
    private int receiverIdx;
    private String lastContent;
    private String created_At;
    private String nickName;
    private String profileImgUrl;
    private int count;

}
