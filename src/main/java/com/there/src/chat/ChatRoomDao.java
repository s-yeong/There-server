package com.there.src.chat;

import com.there.src.chat.model.GetRoomInfoRes;
import com.there.src.chat.model.GetRoomListRes;
import com.there.src.chat.model.GetUnreadCountRes;
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
        List<GetUnreadCountRes> CountInfo;

        String getChatRoomInfoQuery = "select roomIdx, senderIdx, receiverIdx, nickName, profileImgUrl\n" +
                "from chatRoom cr LEFT JOIN User u on cr.receiverIdx = u.userIdx\n" +
                "where senderIdx = ? and u.status = 'ACTIVE' and cr.status = 'ACTIVE';";

        String getUnreadCountQuery = "select      roomIdx, count(*) as count\n" +
                "from        chatContent\n" +
                "where       'check' = 0 and status = 'ACTIVE' and roomIdx in (select  roomIdx\n" +
                "                                                              from    chatRoom\n" +
                "                                                              where   senderIdx = ?)\n" +
                "group by    roomIdx;";

        int getChatRoomListParams = userIdx;

        // 채팅방 정보 조회
        RoomInfo = this.jdbcTemplate.query(getChatRoomInfoQuery, (rs, rowNum) -> new GetRoomInfoRes(

                rs.getInt("roomIdx"),
                rs.getInt("senderIdx"),
                rs.getInt("receiverIdx"),
                rs.getString("nickName"),
                rs.getString("profileImgUrl")), getChatRoomListParams);


        // 채팅방 안 읽은 메시지 조회
        CountInfo = this.jdbcTemplate.query(getUnreadCountQuery, (rs, rowNum) -> new GetUnreadCountRes(
                                rs.getInt("roomIdx"),
                                rs.getInt("count")), getChatRoomListParams);

        for (int i = 0; i < RoomInfo.size(); i++) {

            GetRoomListRes Room = null;
            GetRoomInfoRes tmp_Room = RoomInfo.get(i);
            GetUnreadCountRes tmp_Count = CountInfo.get(i);

            // 채팅방 Idx가 동일할 때 채팅방 정보에 대한 객체 생성
            if (tmp_Room.getRoomIdx() == tmp_Count.getRoomIdx()) {
                Room = new GetRoomListRes
                        (tmp_Room.getRoomIdx(), tmp_Room.getSenderIdx(), tmp_Room.getReceiverIdx(),
                                tmp_Room.getNickName(), tmp_Room.getProfileImgUrl(), tmp_Count.getCount());
            }

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
