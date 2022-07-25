package com.there.src.search;

import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SearchService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchDao searchDao;
    private final SearchProvider searchProvider;
    private final JwtService jwtService;


    @Autowired
    public SearchService(SearchDao searchDao, SearchProvider searchProvider, JwtService jwtService) {
        this.searchDao = searchDao;
        this.searchProvider = searchProvider;
        this.jwtService = jwtService;
    }


}