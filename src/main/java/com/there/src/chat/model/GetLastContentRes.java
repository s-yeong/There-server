package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetLastContentRes {

    private int roomIdx;
    private String content;
    private String created_At;
}
