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

    private String name;

    public PostJoinReq(){

    }
    public PostJoinReq(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;

    }
}

