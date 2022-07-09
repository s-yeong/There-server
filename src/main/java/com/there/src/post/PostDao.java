package com.there.src.post;

import com.there.src.post.model.PatchPostsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 게시물 생성

    // 게시물 삭제
    public int deletePost(int postIdx) {

        String DeletePostQuery = "UPDATE Post SET status = 'INACTIVE' WHERE postIdx = ?";
        int DeletePostParams = postIdx;

        return this.jdbcTemplate.update(DeletePostQuery, postIdx);
    }

    // 게시물 수정




}
