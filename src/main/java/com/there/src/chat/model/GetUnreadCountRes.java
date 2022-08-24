package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class GetUnreadCountRes {

    private int roomIdx;
    private int rd;
    private int urd;

}
