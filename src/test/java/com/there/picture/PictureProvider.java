package com.there.picture;

import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PictureProvider {

    private final PictureDao pictureDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PictureProvider(PictureDao pictureDao, JwtService jwtService) {
        this.pictureDao = pictureDao;
        this.jwtService = jwtService;
    }




}
