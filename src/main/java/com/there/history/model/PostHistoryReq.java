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

    private int postIdx;
    private String title;
    private String content;

    private List<PostHistoryPicturesReq> postHistoryPictures;  // 기록물 이미지 리스트

}
