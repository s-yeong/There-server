package com.there.src.chat;

import com.there.config.BaseException;
import com.there.config.BaseResponseStatus;
import com.there.src.chat.model.GetRoomListRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatRoomProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatRoomDao chatRoomDao;
    private final ChatContentDao chatContentDao;

    @Autowired
    public ChatRoomProvider(ChatRoomDao chatRoomDao, ChatContentDao chatContentDao) {
        this.chatRoomDao = chatRoomDao;
        this.chatContentDao = chatContentDao;
    }

    /**
     * 채팅방 목록 조회
     */
    public List<GetRoomListRes> retrieveChatRoom(int userIdx) {

        List<GetRoomListRes> getRoomInfoList = chatRoomDao.selectChatRoomList(userIdx);

        return getRoomInfoList;
    }

}
