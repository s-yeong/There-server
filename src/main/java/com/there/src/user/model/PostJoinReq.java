package com.there.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*@AllArgsConstructor*/
public class PostJoinReq {
    private String email;
    private String password;

    private String checkpwd;
    private String nickName;

    public PostJoinReq(){

    }
    public PostJoinReq(String email, String password, String nickName, String checkpwd){
        this.email = email;
        this.password = password;
        this.checkpwd = checkpwd;
        this.nickName = nickName;

    }
}

