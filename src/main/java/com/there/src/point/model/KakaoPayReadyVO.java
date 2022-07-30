package com.there.src.point.model;

import lombok.Data;

import java.util.Date;

// 응답을 받는 객체
@Data
public class KakaoPayReadyVO {
    //response
    private String tid, next_redirect_pc_url;
    private Date created_at;
    public KakaoPayReadyVO() {

    }
    public KakaoPayReadyVO(String tid, String next_redirect_pc_url, Date created_at) {
        this.tid = tid;
        this.next_redirect_pc_url = next_redirect_pc_url;
        this.created_at = created_at;
    }
}
