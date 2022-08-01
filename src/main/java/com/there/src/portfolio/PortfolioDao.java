package com.there.src.portfolio;

import com.there.src.portfolio.model.GetPortfolioListRes;
import com.there.src.portfolio.model.Portfolio;
import com.there.src.portfolio.model.PostPortfolioReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PortfolioDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     *  Portfolio 생성
     */
    public int createPortfolios(int userIdx, PostPortfolioReq postPortfolioReq) {

        String createPortfolioQuery = "insert into Portfolio (userIdx, title) values (?, ?);";
        String lastPortfolioIdx = "select last_insert_id()";
        Object[] createPortfolioParams = new Object[]{userIdx, postPortfolioReq.getTitle()};

        this.jdbcTemplate.update(createPortfolioQuery, createPortfolioParams);

        return this.jdbcTemplate.queryForObject(lastPortfolioIdx, int.class);
    }

    /**
     * Portfolio List 조회
     * @param userIdx
     * @return
     */
    public List<GetPortfolioListRes> getPortfolioList(int userIdx) {

        String getPortfolioListQuery = "select pf.portfolioIdx, title, Post_count\n" +
                "from Portfolio pf\n" +
                "        left join (select portfolioIdx, count(portfolioIdx) Post_count from Portfolio_Post) pp on pf.portfolioIdx = pp.portfolioIdx\n" +
                "where userIdx = ?\n" +
                "group by pf.portfolioIdx;";
        int getPortfolioListParams = userIdx;

        return this.jdbcTemplate.query(getPortfolioListQuery, (rs, rowNum) -> new GetPortfolioListRes(
                rs.getInt("portfolioIdx"),
                rs.getString("title"),
                rs.getInt("Post_count")), getPortfolioListParams);
    }
}
