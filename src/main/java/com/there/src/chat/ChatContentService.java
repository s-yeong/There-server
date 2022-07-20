package com.there.src.chat;

import com.there.src.chat.model.ChatContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatContentService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatContentDao chatContentDao;

    @Autowired
    public ChatContentService(ChatContentDao chatContentDao) {
        this.chatContentDao = chatContentDao;
    }

}
