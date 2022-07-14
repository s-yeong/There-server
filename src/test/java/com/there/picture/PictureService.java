package com.there.picture;

import com.there.config.BaseException;
import com.there.picture.model.PostPictureReq;
import com.there.picture.model.PostPictureRes;
import com.there.src.picture.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class PictureService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PictureDao pictureDao;
    private final PictureProvider pictureProvider;
    private final JwtService jwtService;


    @Autowired
    public PictureService(PictureDao pictureDao, PictureProvider pictureProvider, JwtService jwtService) {
        this.pictureDao = pictureDao;
        this.pictureProvider = pictureProvider;
        this.jwtService = jwtService;

    }


    // 사진 업로드
    public List<PostPictureRes> createPicture(List<PostPictureReq> postPictureReq) throws BaseException {

        try{

            List<PostPictureRes> postPicture = new ArrayList<>();
            for(int i=0; i< postPictureReq.size(); i++){
                int pictureIdx = pictureDao.insertPicture(postPictureReq.get(i));
                postPicture.get(i).setPictureIdx(pictureIdx);
            }


            return postPicture;
        }
        catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);

        }
    }

}