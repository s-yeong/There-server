package com.there.src.point.model;

import lombok.Data;

import java.util.Date;

@Data
public class KakaoPayReadyVO {
    //response
    private String tid, next_redirect_pc_url;
    private Date created_at;
    private String amount;
    private String nickName;
}
