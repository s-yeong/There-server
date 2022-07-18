package com.there.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class PatchUserReq {
    private String nickName;
    private String profileImgUrl;
    private String name;
    private String info;


    public PatchUserReq(){

    }
    public PatchUserReq(String nickName, String profileImgUrl, String name, String info){
        this.nickName = nickName;
        this.profileImgUrl = profileImgUrl;
        this.name = name;
        this.info = info;
    }

}
