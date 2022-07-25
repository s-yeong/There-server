package com.there.src.chat;

import com.there.src.chat.config.*;
import com.there.src.chat.model.*;
import com.there.utils.JwtService;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.chat.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/chat")
public class ChatController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtService jwtService;

    private final ChatContentService chatContentService;
    private final ChatContentProvider chatContentProvider;
    private final ChatRoomService chatRoomService;
    private final ChatRoomProvider chatRoomProvider;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, JwtService jwtService, ChatContentService chatContentService, ChatContentProvider chatContentProvider, ChatRoomService chatRoomService, ChatRoomProvider chatRoomProvider) {
        this.messagingTemplate = messagingTemplate;
        this.jwtService = jwtService;
        this.chatContentService = chatContentService;
        this.chatContentProvider = chatContentProvider;
        this.chatRoomService = chatRoomService;
        this.chatRoomProvider = chatRoomProvider;
    }

    /**
     * ChatRoom 생성 API
     * /chat/room/{senderIdx}/receiverIdx}
     */
    @PostMapping("/room/{senderIdx}/{receiverIdx}")
    public BaseResponse<PostChatRoomRes> createRoom
            (@PathVariable("senderIdx") int senderIdx, @PathVariable("receiverIdx")int receiverIdx) {
        try {
            PostChatRoomRes postChatRoomRes = chatRoomService.createRoom(senderIdx, receiverIdx);
            return new BaseResponse<>(postChatRoomRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ChatRoom 조회 API
     * /char/user/{userIdx}
     */
    @ResponseBody
    @GetMapping("/user/{userIdx}")
    public BaseResponse<List<GetChatRoomRes>> getChatRooms(@PathVariable("userIdx")int userIdx) throws com.there.config.BaseException {
        List<GetChatRoomRes> getChatRoomResList = chatRoomProvider.retrieveChatRoom(userIdx);
        return new BaseResponse<>(getChatRoomResList);
    }

    /**
     * ChatRoom 삭제 API
     * /char/room/{roomIdx}
     */
    @ResponseBody
    @PatchMapping("/room/{roomIdx}")
    public BaseResponse<String> deleteChatRooms(@PathVariable("roomIdx")int roomIdx) {
        try {
            chatRoomService.deleteChatRoom(roomIdx);
            String result = "채팅방 삭제를 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Message 전송 API
     * app/chat/content/{sendIdx}/{receiverIdx}
     */
    @MessageMapping("content/{sendIdx}/{receiverIdx}")
    public void createContent
    (@PathVariable("senderIdx") int senderIdx, @PathVariable("receiverIdx")int receiverIdx,
     @Payload MessagechatContentReq messagechatContentReq) throws BaseException {

            // 메시지 생성 후 가져오기
            int contentIdx = chatContentService.createContent(senderIdx, receiverIdx, messagechatContentReq);
            MessagechatContentRes messagechatContentRes = chatContentProvider.getChatContent(senderIdx, receiverIdx, contentIdx);

            // Content 전달
            // user/{receiverId}/queue/message
            messagingTemplate.convertAndSendToUser
                    (messagechatContentRes.getReceiverId(), "/queue/message", messagechatContentRes);
    }

    /**
     * Content 조회 API
     */
    @ResponseBody
    @GetMapping("/room/{roomIdx}/user/{senderIdx}/{receiverIdx}")
    public BaseResponse<List<GetChatContentRes>> getChatContent
    (@PathVariable("roomIdx") int roomIdx, @PathVariable("senderIdx") int senderIdx, @PathVariable("receiverIdx") int receiverIdx) throws com.there.config.BaseException {

        try {
            List<GetChatContentRes> getChatContentList = chatContentProvider.retrieveChatContent(roomIdx, senderIdx, receiverIdx);
            return new BaseResponse<>(getChatContentList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Content 삭제 API
     */
    @ResponseBody
    @PatchMapping("deletion/{contentIdx}/users/{userIdx}")
    public BaseResponse<String> deletechatContent
    (@PathVariable("contentIdx") int contentIdx, @PathVariable("userIdx") int userIdx) throws com.there.config.BaseException {

        int userIdxByJwt = jwtService.getUserIdx();

        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

        try {
            chatContentService.deleteChatContent(contentIdx);
            String result = "메세지 삭제를 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
