package com.there.picture.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPictureReq {

    private int historyIdx;
    private String imgUrl;

}
