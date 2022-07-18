package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chat {

    private int chatIdx;
    private int sendIdx;
    private int receiveIdx;
    private String created_At;
}
