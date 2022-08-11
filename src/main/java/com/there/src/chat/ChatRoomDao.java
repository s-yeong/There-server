package com.there.src.chat;

import com.there.src.chat.model.GetRoomInfoRes;
import com.there.src.chat.model.GetRoomListRes;
import com.there.src.chat.model.GetUserInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatRoomDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int createRoom(int senderIdx, int receiverIdx) {

        String createRoomQuery = "insert into chatRoom (senderIdx, receiverIdx) values (?, ?);";
        Object[] createRoomParams = new Object[]{senderIdx, receiverIdx};

        this.jdbcTemplate.update(createRoomQuery, createRoomParams);

        String lastRoomIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastRoomIdxQuery, int.class);

    }

    // 채팅방 목록 조회
    public List<GetRoomListRes> selectChatRoomList(int userIdx) {

        List<GetRoomListRes> RoomList = new ArrayList<GetRoomListRes>();
        List<GetRoomInfoRes> RoomInfo;
        List<GetUserInfoRes> UserInfo;

        String getChatRoomInfoQuery = "select      roomIdx, count(*) as count\n" +
                "from        chatContent\n" +
                "where       'check' = 0 and status = 'ACTIVE' and roomIdx in (select  roomIdx\n" +
                "                                                              from    chatRoom\n" +
                "                                                              where   senderIdx = ?)\n" +
                "group by    roomIdx;";

        String getUserInfoQuery = "select  nickName, profileImgUrl\n" +
                "from    User u\n" +
                "where   u.userIdx in (select receiverIdx from chatRoom where senderIdx = ? and u.status = 'ACTIVE');";

        int getChatRoomListParams = userIdx;

        // 채팅방 정보 조회
        RoomInfo = this.jdbcTemplate.query(getChatRoomInfoQuery, (rs, rowNum) -> new GetRoomInfoRes(


                rs.getInt("roomIdx"),
                rs.getInt("count")), getChatRoomListParams);


        // 유저 정보 조회
        UserInfo = this.jdbcTemplate.query(getUserInfoQuery, (rs, rowNum) -> new GetUserInfoRes(
                                rs.getString("nickName"),
                                rs.getString("profileImgUrl")), getChatRoomListParams);

        for (int i = 0; i < RoomInfo.size(); i++) {

            GetRoomListRes Room;
            GetRoomInfoRes tmp_Room = RoomInfo.get(i);
            GetUserInfoRes tmp_User = UserInfo.get(i);

            Room =
                    new GetRoomListRes(tmp_Room.getRoomIdx(), tmp_Room.getCount(), tmp_User.getNickName(), tmp_User.getProfileImgUrl());

            RoomList.add(Room);
        }

        return RoomList;

    }

    // 채팅방 삭제
    public int deleteChatRoom(int roomIdx) {

        String deleteChatRoomQuery = "UPDATE ChatRoom SET status = 'INACTIVE' WHERE roomIdx = ?";
        int deletePostParams = roomIdx;

        return this.jdbcTemplate.update(deleteChatRoomQuery, roomIdx);

    }

    // chatRoom 가져오기
    public int selectRoomIdx(int senderIdx, int receiverIdx) {

        String getRoomIdxQuery = "select roomIdx from chatRoom where sendIdx = ? and receiverIdx = ?";
        Object[] getRoomIdxParams = new Object[]{senderIdx, receiverIdx};

        return this.jdbcTemplate.queryForObject(getRoomIdxQuery, int.class, getRoomIdxParams);
    }


}
