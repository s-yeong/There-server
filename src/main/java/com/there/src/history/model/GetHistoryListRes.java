package com.there.src.history.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHistoryListRes {

    private int historyIdx;
    private String title;
    private String createdAt;
    private String dayOfWeek;


}
