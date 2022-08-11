package com.there.src.chat;

import com.there.config.BaseException;
import com.there.config.BaseResponseStatus;
import com.there.src.chat.model.GetRoomInfoRes;
import com.there.src.chat.model.GetUserInfoRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

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
    public List<GetRoomInfoRes> retrieveChatRoom(int userIdx) {

        List<GetRoomInfoRes> getRoomInfoList = chatRoomDao.selectChatRoomList(userIdx);

        // 읽은 메시지로 상태 변경 (단, 읽지 않은 메시지가 있을 때만)
        if (chatContentDao.selectUnCheckCount(userIdx) > 0) chatContentDao.checkChatContent(userIdx);

        return getRoomInfoList;
    }

}
