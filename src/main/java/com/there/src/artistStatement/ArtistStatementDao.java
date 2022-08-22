package com.there.src.artistStatement;

import com.there.src.artistStatement.model.GetArtistStatementRes;
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


}
