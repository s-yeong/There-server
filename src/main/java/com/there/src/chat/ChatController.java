package com.there.src.chat;

import com.there.src.chat.config.*;
import com.there.src.chat.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.chat.config.BaseResponseStatus.INVALID_USER_JWT;

@Api
@RestController
@RequestMapping("/chat")
public class ChatController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtService jwtService;
    private final SimpMessagingTemplate messagingTemplate;

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

    @ApiOperation(value="채팅방 생성 API", notes="상대방 프로필에서 메시지 클릭 시 ChatRoom 생성")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
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

    @ApiOperation(value="채팅방 리스트 조회 API", notes="하단바에서 메세지 클릭 시 채팅방 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/room/user/{userIdx}")
    public BaseResponse<List<GetRoomInfoRes>> getChatRooms
    (@PathVariable("userIdx")int userIdx) throws com.there.config.BaseException {

        // 채팅방 조회
        List<GetRoomInfoRes> getRoomInfoList = chatRoomProvider.retrieveChatRoom(userIdx);
        return new BaseResponse<>(getRoomInfoList);

    }

    @ApiOperation(value="채팅방 삭제 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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

    @ApiOperation(value="메세지 전송 API", notes="메세지 입력 후 보내기 버튼 클릭 시 전송")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @MessageMapping("/{sendIdx}/{receiverIdx}")
    @SendTo("/user/{sendIdx}/{receiverIdx}")
    public MessagechatContentRes sendContent
    (@DestinationVariable("senderIdx") int senderIdx, @DestinationVariable("receiverIdx")int receiverIdx,
     @Payload MessagechatContentReq messagechatContentReq) throws BaseException {

            String receiverId = Integer.toString(receiverIdx);

            // 메시지 생성 후 가져오기
            int contentIdx = chatContentService.createContent(senderIdx, receiverIdx, messagechatContentReq);
            MessagechatContentRes messagechatContentRes = chatContentProvider.getChatContent(senderIdx, receiverIdx, contentIdx);

            // 메시지 전달 user/{receiverIdx}
            return messagechatContentRes;
    }

    @ApiOperation(value="메세지 조회 API", notes="채팅방 목록에서 채팅방 클릭 시 메시지 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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

    @ApiOperation(value="메세지 삭제 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/deletion/{contentIdx}/users/{userIdx}")
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
