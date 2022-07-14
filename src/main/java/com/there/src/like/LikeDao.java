package com.there.src.like;

import com.there.src.like.model.DeleteLikeReq;
import com.there.src.like.model.GetLikeRes;
import com.there.src.like.model.PostLikeReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LikeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 좋아요 및 감정표현 생성
    public int createLikes(PostLikeReq postLikeReq) {

        String insertLikeQuery = "insert into postLikes (userIdx, postIdx, emotion) values (?, ?, ?);";
        Object[] insertLikeParams = new Object[]{postLikeReq.getUserIdx(), postLikeReq.getPostIdx(), postLikeReq.getEmotion()};
        String lastinsertIdxQuery = "select last_insert_id()";

        this.jdbcTemplate.update(insertLikeQuery, insertLikeParams);

        return this.jdbcTemplate.queryForObject(lastinsertIdxQuery, int.class);
    }

    // 좋아요 및 감정표현 조회
    public List<GetLikeRes> selectLikes(int postIdx) {
        String selectLikeQuery = "select emotion, count(*) count from postLikes where postIdx = ? group by emotion;";
        int selectLikeParams = postIdx;

        return this.jdbcTemplate.query(selectLikeQuery, (rs, rowNum) -> new GetLikeRes(
                rs.getInt("emotion"),
                rs.getInt("count"))
                , selectLikeParams);
    }

    // 좋아요 및 감정표현 수정
    public int updateLikes(int userIdx, int postIdx, int emotion) {
        String updateLikesQuery = "UPDATE postLikes SET emotion = ? WHERE userIdx = ? and postIdx = ?";
        Object[] updateLikesParams = new Object[]{emotion, userIdx, postIdx};

        return this.jdbcTemplate.update(updateLikesQuery, updateLikesParams);
    }

    // 좋아요 및 감정표현 삭제
    public int deleteLikes(DeleteLikeReq deleteLikeReq) {
        String insertLikeQuery = "delete from postLikes where userIdx = ? and postIdx = ?;";
        Object[] insertLikeParams = new Object[]{deleteLikeReq.getUserIdx(), deleteLikeReq.getPostIdx()};

        return this.jdbcTemplate.update(insertLikeQuery, insertLikeParams);
    }
}
