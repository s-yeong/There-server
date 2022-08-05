package com.there.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@Data
@AllArgsConstructor
public class GetCommentListRes {

    private String nickName;
    private String profileImgUrl;
    private String content;
    private String created_At;

}
