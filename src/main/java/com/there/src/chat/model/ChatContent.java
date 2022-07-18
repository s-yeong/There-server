package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatContent {
    private int chatcontentIdx;
    private int chatIdx;
    private String content;
    private String created_At;


}
