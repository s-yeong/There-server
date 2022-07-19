package com.there.src.comment;

import com.there.src.comment.model.GetCommentListRes;
import com.there.src.comment.model.PostCommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 댓글 생성
    public int createComment(int postIdx, int userIdx, PostCommentReq postCommentReq) {
        String createCommentQuery = "insert into Comment (postIdx,userIdx, content) values (?,?, ?);";
        Object[] createCommentParams = new Object[]{postIdx, userIdx, postCommentReq.getContent()};

        this.jdbcTemplate.update(createCommentQuery, createCommentParams);

        String lastcommentIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastcommentIdxQuery, int.class);
    }

    // 댓글 조회
    public List<GetCommentListRes> selectCommentList(int postIdx) {
        String selectCommentQuery = "select nickName, profileImgUrl, content, created_At\n" +
                "from Comment c join User as u on u.userIdx = c.userIdx\n" +
                "where postIdx = ? ;";
        int selectCommentParam = postIdx;

        return this.jdbcTemplate.query(selectCommentQuery, (rs, rowNum) -> new GetCommentListRes(
                rs.getString("nickName"),
                rs.getString("profileImgUrl"),
                rs.getString("content"),
                rs.getString("created_At")
        ), selectCommentParam);
    }
}
