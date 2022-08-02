package com.there.src.point;


import com.there.src.point.model.AmountVO;
import com.there.src.point.model.GetTotalPointRes;
import com.there.src.point.model.GetchargePointListRes;
import com.there.src.point.model.PostPointReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PointDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 포인트 충전
    public int chargePoint(int userIdx, int amount, String tid) {
        String chargePointQuery = "insert into Point (userIdx, point, tid, totalpoint) values (?, ?,?,(select point from(select sum(point) as point from Point)point));";
        Object[] chargePointParams = new Object[]{userIdx, amount, tid};

        this.jdbcTemplate.update(chargePointQuery, chargePointParams);

        String lastPointIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastPointIdxQuery, int.class);
    }

    // 포인트 충전 내역 리스트 조회
    public List<GetchargePointListRes> selectChargePointList(int userIdx) {
        String selectChargePointQuery = "select point, created_At from Point\n" +
                "where userIdx =?;";
        int selectChargePointParam = userIdx;

        return this.jdbcTemplate.query(selectChargePointQuery,
                (rs, rowNum) -> new GetchargePointListRes(
                        rs.getString("point"),
                        rs.getString("created_At")
                ), selectChargePointParam);
    }

    public GetTotalPointRes selectTotalPoint(int userIdx){
        String selectTotalPointQuery = "select sum(point) as totalpoint from Point\n" +
                "where userIdx =?;";
        int selectTotalPointParam = userIdx;

        return this.jdbcTemplate.queryForObject(selectTotalPointQuery,
                (rs, rowNum) -> new GetTotalPointRes(
                        rs.getInt("totalpoint")
                ), selectTotalPointParam);
    }
}
