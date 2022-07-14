package com.there.src.post;

import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final JwtService jwtService;

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }



}
