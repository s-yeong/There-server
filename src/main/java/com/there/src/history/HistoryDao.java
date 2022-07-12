package com.there.src.history;

import com.there.src.history.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class HistoryDao {

    private JdbcTemplate jdbcTemplate;
    List<GetHistoryPicturesRes> getHistoryPicturesRes;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 히스토리 조회 함수    - 기록물 사진 같은 경우 하나의 리스트가 칼럼으로 들어감   - updatedAt 기준으로 날짜 요일 조회
    public GetHistoryRes selectHistory(int historyIdx){
        String selectHistoryQuery = "select h.historyIdx, h.title, h.content,\n" +
                "      DATE(updated_At) as updatedAt,\n" +
                "    CASE DAYOFWEEK(updated_At)\n" +
                "    WHEN '1' THEN '(일)'\n" +
                "    WHEN '2' THEN '(월)'\n" +
                "    WHEN '3' THEN '(화)'\n" +
                "    WHEN '4' THEN '(수)'\n" +
                "    WHEN '5' THEN '(목)'\n" +
                "    WHEN '6' THEN '(금)'\n" +
                "    WHEN '7' THEN '(토)'\n" +
                "    END AS dayOfWeek\n" +
                "from History as h\n" +
                "where h.historyIdx = ?;";
        int selectHistoryParam = historyIdx;
        return this.jdbcTemplate.queryForObject(selectHistoryQuery,
                (rs, rowNum) -> new GetHistoryRes(
                        rs.getInt("historyIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("updatedAt"),
                        rs.getString("dayOfWeek"),
                        getHistoryPicturesRes = this.jdbcTemplate.query("select hp.pictureIdx as pictureIdx, hp.imgUrl as imgUrl\n" +
                                        "from historyPicture as hp\n" +
                                        "    join History as h on h.historyIdx = hp.historyIdx\n" +
                                        "where h.historyIdx = ? and hp.liked_ip = 'ACTIVE';",
                                (rk, rownum) -> new GetHistoryPicturesRes(
                                        rk.getInt("pictureIdx"),
                                        rk.getString("imgUrl")
                                ), rs.getInt("historyIdx"))
                ), selectHistoryParam);
    }

}
