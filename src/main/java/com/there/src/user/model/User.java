package com.there.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
public class User {

    @Id // pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increase

    private int userIdx;
    private String nickName;
    private String email;
    private String password;


}
