package com.there.src.point;


import com.there.src.point.model.*;
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
    public int chargePoint(int userIdx, int amount, int tax_free_amount, String tid) {
        String chargePointQuery = "insert into Point (userIdx, amount, tax_free_amount,tid) " +
                "values (?, ?,?,?)";
        Object[] chargePointParams = new Object[]{userIdx, amount, tax_free_amount, tid};
        this.jdbcTemplate.update(chargePointQuery, chargePointParams);

        String lastPointIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastPointIdxQuery, int.class);
    }

    // 포인트 충전 내역 리스트 조회
    public List<GetchargePointListRes> selectChargePointList(int userIdx) {
        String selectChargePointQuery = "select amount, created_At from Point\n" +
                "where userIdx =?;";
        int selectChargePointParam = userIdx;

        return this.jdbcTemplate.query(selectChargePointQuery,
                (rs, rowNum) -> new GetchargePointListRes(
                        rs.getString("amount"),
                        rs.getString("created_At")
                ), selectChargePointParam);
    }

    public GetTotalPointRes selectTotalPoint(int userIdx){
        String selectTotalPointQuery = "select sum(amount) as totalpoint from Point\n" +
                "where userIdx =?;";
        int selectTotalPointParam = userIdx;

        return this.jdbcTemplate.queryForObject(selectTotalPointQuery,
                (rs, rowNum) -> new GetTotalPointRes(
                        rs.getInt("totalpoint")
                ), selectTotalPointParam);
    }

    public GetPointRes selectPoint(int pointIdx) {
        String selectPoinQuery = "select tid, amount, tax_free_amount from Point where pointIdx =?";
        int selectPostParam = pointIdx;
        return this.jdbcTemplate.queryForObject(selectPoinQuery,
                (rs, rowNum) -> new GetPointRes(
                        rs.getString("tid"),
                        rs.getInt("amount"),
                        rs.getInt("tax_free_amount")
                ),selectPostParam);
    }

    public int cancelPoint(int pointIdx) {
        String cancelPointQuery = "update Point set status = 'DELETED' where pointIdx=?";
        int cancelPointParam = pointIdx;
        return this.jdbcTemplate.update(cancelPointQuery, cancelPointParam);
    }
}
