package com.there.src.artistStatement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetArtistStatementRes {

    private int statementIdx;
    private int userIdx;
    private String selfIntroduction;
    private String workIntroduction;
    private String contact;

}
