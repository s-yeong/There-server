package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessagechatContentRes {

    private int roomIdx;
    private String senderId;
    private String receiverId;
    private String content;
    private String created_At;

}
