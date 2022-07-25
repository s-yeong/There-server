package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetChatRoomRes {

    // 채팅방 Idx
    //private int rooIdx;

    // 채팅 상대 닉네임, 프로필 사진
    private String nickName;
    private String profileImgUrl;

    // 최근 대화 내용
    //private String content;

    // 안읽은 메시지 수
    // private String count;
}
