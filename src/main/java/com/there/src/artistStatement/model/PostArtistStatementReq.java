package com.there.src.artistStatement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostArtistStatementReq {

    private String selfIntroduction;
    private String workIntroduction;
    private String contact;

}
