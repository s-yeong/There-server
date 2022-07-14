package com.there.picture;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.picture.model.PostPictureReq;
import com.there.picture.model.PostPictureRes;
import com.there.src.picture.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pictures")
public class PictureController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PictureProvider pictureProvider;
    @Autowired
    private final PictureService pictureService;
    @Autowired
    private final JwtService jwtService;




    public PictureController(PictureProvider pictureProvider, PictureService pictureService, JwtService jwtService){
        this.pictureProvider = pictureProvider;
        this.pictureService = pictureService;
        this.jwtService = jwtService;
    }

/*
    *//**
     * 사진 업로드 API
     * [POST] /pictures
     * @return BaseResponse<postPictureRes>
     *//*
    @ResponseBody
    @PostMapping("")
    public BaseResponse<List<PostPictureRes>> createPicture(@RequestBody List<PostPictureReq> postPictureReq) {


        try {

            List<PostPictureRes> postPictureRes = pictureService.createPicture(postPictureReq);
            return new BaseResponse<>(postPictureRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    */
    /**
     * 사진 업로드 API
     * [POST] /pictures
     * @return BaseResponse<postPictureRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<List<PostPictureRes>> createPicture(@RequestBody List<PostPictureReq> postPictureReq) {


        try {

            List<PostPictureRes> postPictureRes = pictureService.createPicture(postPictureReq);
            return new BaseResponse<>(postPictureRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}

