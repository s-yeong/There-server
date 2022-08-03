package com.there.src.portfolio;

import com.there.src.portfolio.model.GetPortfolioListRes;
import com.there.src.portfolio.model.GetPortfolioRes;
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
     * PortPolio에 Post 추가 API
     * @param portfolioIdx
     * @param postIdx
     * @return
     */
    public int createPostInPortfolio(int portfolioIdx, int postIdx) {
        String createPostInPortfolioQuery = "insert into Portfolio_Post (portfolioIdx, postIdx) values (?, ?);";
        String LastContentIdx = "select last_insert_id()";
        Object[] createPostInPortfolioParams = new Object[]{portfolioIdx, postIdx};

        this.jdbcTemplate.update(createPostInPortfolioQuery, createPostInPortfolioParams);

        return this.jdbcTemplate.queryForObject(LastContentIdx, int.class);
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
                "where userIdx = ? and status = 'ACTIVE'\n" +
                "group by pf.portfolioIdx;";
        int getPortfolioListParams = userIdx;

        return this.jdbcTemplate.query(getPortfolioListQuery, (rs, rowNum) -> new GetPortfolioListRes(
                rs.getInt("portfolioIdx"),
                rs.getString("title"),
                rs.getInt("Post_count")), getPortfolioListParams);
    }

    /**
     * Portfolio 조회 API
     * @param portfolioIdx
     * @return
     */
    public List<GetPortfolioRes> getPortfolios(int portfolioIdx) {

        String getPortfolioQuery = "select contentIdx, pp.postIdx, imgUrl\n" +
                "from (select * from Portfolio_Post where portfolioIdx = ?) pp left join Post p\n" +
                "on p.postIdx = pp.postIdx\n" +
                "WHERE status = 'ACTIVE';";
        int getPortfolioParams = portfolioIdx;

        return this.jdbcTemplate.query(getPortfolioQuery, (rs, rowNum) -> new GetPortfolioRes(
                rs.getInt("contentIdx"),
                rs.getInt("postIdx"),
                rs.getString("ImgUrl")), getPortfolioParams);

    }
}
