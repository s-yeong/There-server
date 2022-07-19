package com.there.src.comment;

import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentDao commentDao;
    private final JwtService jwtService;

    @Autowired
    public CommentProvider(CommentDao commentDao, JwtService jwtService){
        this.commentDao = commentDao;
        this.jwtService = jwtService;
    }
}
