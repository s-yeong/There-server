package com.there.src.history.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHistoryRes {

    private int historyIdx;
    private String title;
    private String content;
    private String createdAt;
    private String dayOfWeek;

    private List<GetHistoryPicturesRes> getHistoryPicturesRes;  // 기록물 이미지 리스트

}
