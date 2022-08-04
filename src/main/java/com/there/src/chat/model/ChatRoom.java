package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoom {

    private int roomIdx;
    private int senderIdx;
    private int receiverIdx;
    private String created_At;
    private String status;
}
