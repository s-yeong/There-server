package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatContent {

    private int contentIdx;
    private int roomIdx;
    private String content;
    private String created_At;
    private String status;

}
