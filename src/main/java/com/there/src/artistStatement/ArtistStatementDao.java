package com.there.src.artistStatement;

import com.there.src.artistStatement.model.GetArtistStatementRes;
import com.there.src.artistStatement.model.PostArtistStatementReq;
import com.there.src.history.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class ArtistStatementDao {

    private JdbcTemplate jdbcTemplate;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 작가노트 조회
    public GetArtistStatementRes selectStatement(int userIdx) {
        String selectStatementQuery = "select * from ArtistStatement where userIdx = ?;";
        int selectStatementParam = userIdx;

        return this.jdbcTemplate.queryForObject(selectStatementQuery,
                (rs, rowNum) -> new GetArtistStatementRes(
                        rs.getInt("statementIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("selfIntroduction"),
                        rs.getString("workIntroduction"),
                        rs.getString("contact")),
                selectStatementParam);
    }

    // 작가노트 작성
    public int insertStatement(int userIdx, PostArtistStatementReq postArtistStatementReq) {
        String insertStatementQuery = "insert ArtistStatement(userIdx, selfIntroduction, workIntroduction, contact) values(?,?,?,?);";
        Object[] insertStatementParams = new Object[] {userIdx, postArtistStatementReq.getSelfIntroduction(), postArtistStatementReq.getWorkIntroduction(), postArtistStatementReq.getContact()};

        this.jdbcTemplate.update(insertStatementQuery,
                insertStatementParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

    }

    // 작가노트 체크
    public int checkStatementExist(int userIdx) {
        String checkStatementExistQuery = "select exists (select * from ArtistStatement where userIdx =?);";
        int checkStatementExistParam = userIdx;

        return this.jdbcTemplate.queryForObject(checkStatementExistQuery, int.class,
                checkStatementExistParam);
    }
}
