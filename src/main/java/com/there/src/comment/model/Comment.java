package com.there.src.comment.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Comment {
    private int commentIdx;
    private int postIdx;
    private int userIdx;
    private String content;
    private String created_At;
    private String status;
    private String update_At;
}
