package com.there.src.Post.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Post {

    private int postIdx;        // 게시글 고유 번호
    private int userIdx;        // 게시글 작성 유저 고유 번호
    private String imgUrl;      // 게시글 대표사진 Url
    private String Content;     // 게시글 내용
    private String created_At;  // 게시글 생성 날짜
    private String updated_At;  // 게시글 업데이트 날짜
    private String status;      // 게시글 상태(ACTIVE / INACTIVE)

}
