package com.there.src.search;

import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SearchProvider {

    private final SearchDao searchDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SearchProvider(SearchDao searchDao, JwtService jwtService) {
        this.searchDao = searchDao;
        this.jwtService = jwtService;
    }



}
