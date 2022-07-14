package com.there.src.history.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PatchHistoryReq {


    private String title;
    private String content;

    private List<PatchHistoryPicturesReq> patchHistoryPictures;

}
