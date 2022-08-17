package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class GetRoomInfoRes {

    private int roomIdx;
    private int senderIdx;
    private int receiverIdx;
    private String nickName;
    private String profileImgUrl;

}
