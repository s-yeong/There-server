package com.there.src.chat;

import com.there.config.BaseException;
import com.there.src.chat.model.GetRoomInfoRes;
import com.there.src.chat.model.GetUserInfoRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatRoomDao chatRoomDao;

    @Autowired
    public ChatRoomProvider(ChatRoomDao chatRoomDao) {
        this.chatRoomDao = chatRoomDao;
    }

    /**
     * 채팅방 목록 조회
     */
    public List<GetRoomInfoRes> retrieveChatRoom(int userIdx) throws BaseException {

        List<GetRoomInfoRes> getRoomInfoList = chatRoomDao.selectChatRoomList(userIdx);

        return getRoomInfoList;
    }
}
