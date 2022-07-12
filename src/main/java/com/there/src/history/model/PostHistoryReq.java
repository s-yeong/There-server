package com.there.src.history.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PostHistoryReq {

    private int userIdx;    // 유저 로그인 API 구현시 삭제
    private int postIdx;
    private String title;
    private String content;

    private List<PostHistoryPicturesReq> postHistoryPicturesReq;  // 기록물 이미지 리스트

}
