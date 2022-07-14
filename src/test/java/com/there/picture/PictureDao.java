package com.there.picture;

import com.there.picture.model.PostPictureReq;
import com.there.src.picture.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Repository
public class PictureDao {

    private JdbcTemplate jdbcTemplate;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 사진 업로드 함수
    public int insertPicture(PostPictureReq postPictureReq){
        String insertPictureQuery = "insert into historyPicture(historyIdx, imgUrl) VALUES (?,?,?);";
        Object[] insertPictureParams = new Object[] {postPictureReq.getHistoryIdx(), postPictureReq.getImgUrl()};
        this.jdbcTemplate.update(insertPictureQuery,
                insertPictureParams);
        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }


}
