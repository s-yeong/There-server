package com.there.src.point;


import com.there.src.point.model.AmountVO;
import com.there.src.point.model.PostPointReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PointDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 포인트 충전
    public int chargePoint(int userIdx, int amount) {
        String chargePointQuery = "insert into point (userIdx, point) values (?,?);";
        Object[] chargePointParams = new Object[]{userIdx, amount};

        this.jdbcTemplate.update(chargePointQuery, chargePointParams);

        String lastPointIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastPointIdxQuery, int.class);
    }
}
