package com.there.src.user.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*@AllArgsConstructor*/
public class PostLoginReq {
    private String email;
    private String password;




    public PostLoginReq(){

    }
    public PostLoginReq(String email, String password ){
        this.email = email;
        this.password = password;

    }
}

