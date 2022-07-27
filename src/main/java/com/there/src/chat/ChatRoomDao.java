package com.there.src.chat;

import com.there.src.chat.model.GetRoomInfoRes;
import com.there.src.chat.model.GetUserInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatRoomDao {

    private JdbcTemplate jdbcTemplate;
    private GetUserInfoRes getUserInfoList;

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
    public List<GetRoomInfoRes> selectChatRoomList(int userIdx) {

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

        return this.jdbcTemplate.query(getChatRoomInfoQuery, (rs, rowNum) -> new GetRoomInfoRes(

                // roomIdx, 안 읽은 메시지 수
                rs.getInt("roomIdx"),
                rs.getInt("count"),

                // 유저 정보
                getUserInfoList = this.jdbcTemplate.queryForObject(getUserInfoQuery,
                        (rk, rownum) -> new GetUserInfoRes(
                                rk.getString("nickName"),
                                rk.getString("profileImgUrl"))
                        , getChatRoomListParams)), getChatRoomListParams);
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
