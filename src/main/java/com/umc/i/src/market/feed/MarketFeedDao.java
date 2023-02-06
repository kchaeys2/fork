package com.umc.i.src.market.feed;

import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import com.umc.i.src.member.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
public class MarketFeedDao implements MarketFeedRepository {

    private final JdbcTemplate jdbcTemplate;

    public MarketFeedDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int getFeedUserIdx(String marketIdx) {
        String query = "select mem_idx from market where market_idx = ?";

        try {
            List<Member> result = jdbcTemplate.query(query, getFeedMemberIdxRowMapper(), marketIdx);
            return result.get(0).getMemIdx();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return -1;
    }

    @Override
    public void postCoverImage(List<String> filesUrlList, String marketIdx) {
        String query = "update market set market_image = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, filesUrlList.get(0), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeedHitCount(String marketIdx) {
        String query = "update market set market_hit = market_hit + 1 where market_idx = ?";

        try {
            jdbcTemplate.update(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeed(String marketIdx, GetMarketFeedReq req) {
        String query = "update market set market_group = ?, market_price = ?, market_title = ?, market_content = ?, market_soldout = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, req.getCategory(), req.getPrice(), req.getTitle(), req.getContent(), req.getSoldout(), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFeedSoldout(String marketIdx, GetMarketFeedReq req) {
        String query = "update market set market_soldout = ? where market_idx = ?";

        try {
            jdbcTemplate.update(query, req.getSoldout(), marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void deleteFeed(String marketIdx) {
        String query = "delete from market where market_idx = ?";

        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public int postFeedImages(List<String> filesUrlList, int marketIdx) {
        String query = "insert into image_url (content_category, content_idx, image_url, image_order) values (?, ?, ?, ?)";

        int affectedRows = 0;
        for (String imageUrl : filesUrlList) {
            try {
                affectedRows += jdbcTemplate.update(query, 0, marketIdx, imageUrl, filesUrlList.indexOf(imageUrl));
            } catch (Exception e) {
                log.error(e.getMessage());
                return -1;
            }
        }
        return affectedRows;
    }

    public void deleteImages(int marketIdx) {
        String query = "delete from image_url where market_idx = ?";

        try {
            jdbcTemplate.update(query, marketIdx);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<GetMarketFeedRes> getFeedByMarketIdx(String marketIdx, String memIdx) {
        String query = "select g.*, group_concat(h.image_url) as image_url\n" +
                "from (\n" +
                "\tselect e.*, f.mem_nickname\n" +
                "\tfrom (\n" +
                "\t\tselect c.*, d.market_like_count\n" +
                "\t\tfrom (\n" +
                "\t\t\tselect \n" +
                "\t\t\t\ta.market_idx, \n" +
                "\t\t\t\ta.mem_idx, \n" +
                "\t\t\t\ta.market_title, \n" +
                "\t\t\t\ta.market_content, \n" +
                "\t\t\t\ta.market_soldout, \n" +
                "\t\t\t\ta.market_price, \n" +
                "\t\t\t\ta.market_image, \n" +
                "\t\t\t\ta.market_hit, \n" +
                "\t\t\t\tIF (b.mem_idx22 is null, false, true) as mem_liked, \n" +
                "\t\t\t\ta.market_created_at \n" +
                "\t\t\tfrom market a\n" +
                "\t\t\tleft join (\n" +
                "\t\t\t\tselect market_idx, mem_idx22\n" +
                "\t\t\t\tfrom market_like\n" +
                "\t\t\t\twhere mem_idx22 = ?\n" +
                "\t\t\t) b\n" +
                "\t\t\ton a.market_idx = b.market_idx\n" +
                "\t\t\twhere a.market_idx = ?\n" +
                "\t\t) c\n" +
                "\t\tleft join (\n" +
                "\t\t\tselect market_idx, count(*) as market_like_count\n" +
                "\t\t\tfrom market_like\n" +
                "\t\t\twhere market_idx = ?\n" +
                "\t\t\tgroup by market_idx\n" +
                "\t\t) d\n" +
                "\t\ton c.market_idx = d.market_idx\n" +
                "\t) e\n" +
                "\tleft join (\n" +
                "\t\tselect mem_idx, mem_nickname\n" +
                "\t\tfrom member\n" +
                "\t\t) f\n" +
                "\ton e.mem_idx = f.mem_idx\n" +
                ") g\n" +
                "join image_url h\n" +
                "on g.market_idx = h.content_idx\n" +
                "order by h.image_order;";

        try {
            List<GetMarketFeedRes> feedResult = jdbcTemplate.query(query, marketFeedByMarketIdxRowMapper(), memIdx, marketIdx, marketIdx);
            return feedResult;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public int postNewFeed(GetMarketFeedReq marketFeed) {
        String query = "insert into market (mem_idx, market_group, market_price, market_title, market_content) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement preparedStatement = connection.prepareStatement(query, new String[]{"market_idx"});
            preparedStatement.setInt(1, marketFeed.getUserIdx());
            preparedStatement.setString(2, marketFeed.getCategory());
            preparedStatement.setInt(3, marketFeed.getPrice());
            preparedStatement.setString(4, marketFeed.getTitle());
            preparedStatement.setString(5, marketFeed.getContent());
            return preparedStatement;
        };

        try {
            jdbcTemplate.update(preparedStatementCreator, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return -1;
    }

    @Override
    public List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page) {

        String query = "select a.*, b.market_like_count\n" +
                "from (\n" +
                "\tselect \n" +
                "\t\tm.market_idx,\n" +
                "\t\tm.mem_idx, \n" +
                "\t\tm.market_group, \n" +
                "\t\tm.market_price, \n" +
                "\t\tm.market_title, \n" +
                "\t\tm.market_image, \n" +
                "\t\tm.market_soldout, \n" +
                "\t\tm.market_hit, \n" +
                "\t\tm.market_created_at, \n" +
                "\t\tIF (ml.mem_idx22 is null, false, true) as mem_liked\n" +
                "\tfrom market m \n" +
                "\tleft join (\n" +
                "\t\tselect market_idx, mem_idx22 \n" +
                "\t\tfrom market_like \n" +
                "\t\twhere mem_idx22 = ?) ml\n" +
                "\ton m.market_idx = ml.market_idx \n" +
                "\twhere m.market_group = ? and m.market_soldout = ?\n" +
                ") a\n" +
                "left join (\n" +
                "\tselect market_idx, count(*) as market_like_count\n" +
                "\tfrom market_like\n" +
                "\tgroup by market_idx\n" +
                ") b\n" +
                "on a.market_idx = b.market_idx\n" +
                "order by a.market_created_at DESC\n" +
                "limit ?, ?;";

        try {
            List<GetMarketFeedRes> result = this.jdbcTemplate.query(query, marketFeedByCategoryRowMapper(), userIdx, category, soldout, page * 9, (page * 9) + Constant.FEED_PER_PAGE);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public void feedLike(int userIdx, int marketIdx, String isLike, int feedUserIdx) {

        if (isLike.equals("False")) {
            String deleteQuery = "delete from market_like where market_idx = ? and mem_idx22 = ?";

            try {
                jdbcTemplate.update(deleteQuery, marketIdx, userIdx);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        else {
            String query = "insert into market_like values (null, ?, ?, ?, ?)";

            try {
                jdbcTemplate.update(query, marketIdx, feedUserIdx, 1, userIdx);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public List<GetMarketFeedRes> getFeedByUserIdx(int userIdx) {
        String query = "select m.market_idx, m.mem_idx, m.market_title, m.market_soldout, m.market_price, m.market_image, m.market_like_count, IF (ml.mem_idx22 is null, false, true) as mem_liked  from market m left join (select market_idx, mem_idx22 from market_like where mem_idx22 = ?) ml on m.market_idx = ml.market_idx where m.mem_idx = ? order by m.market_created_at DESC";

        try {
            List<GetMarketFeedRes> result = jdbcTemplate.query(query, marketFeedByUserIdxRowMapper(), userIdx, userIdx);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private RowMapper<GetMarketFeedRes> marketFeedByMarketIdxRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes feedRes = new GetMarketFeedRes();
            feedRes.setMarketIdx(rs.getInt("market_idx"));
            feedRes.setUserIdx(rs.getInt("mem_idx"));
            feedRes.setUserNickname(rs.getString("mem_nickname"));
            feedRes.setTitle(rs.getString("market_title"));
            feedRes.setContent(rs.getString("market_content"));
            feedRes.setPrice(rs.getInt("market_price"));
            feedRes.setSoldout(rs.getString("market_soldout"));
            feedRes.setImage(rs.getString("image_url"));
            feedRes.setLikeCount(rs.getInt("market_like_count"));
            feedRes.setUserLiked(rs.getBoolean("mem_liked"));
            feedRes.setHit(rs.getInt("market_hit"));
            feedRes.setCreatedAt(rs.getTimestamp("market_created_at"));
            return feedRes;
        };
    }

    private RowMapper<GetMarketFeedRes> marketFeedByCategoryRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("market_idx"));
            marketFeed.setUserIdx(rs.getInt("mem_idx"));
            marketFeed.setGroup(rs.getString("market_group"));
            marketFeed.setTitle(rs.getString("market_title"));
//            marketFeed.setContent(rs.getString("m.market_content"));
            marketFeed.setPrice(rs.getInt("market_price"));
            marketFeed.setSoldout(rs.getString("market_soldout"));
            marketFeed.setImage(rs.getString("market_image"));
            marketFeed.setLikeCount(rs.getInt("market_like_count"));
            marketFeed.setHit(rs.getInt("market_hit"));
            marketFeed.setCreatedAt(rs.getTimestamp("market_created_at"));
            marketFeed.setUserLiked(rs.getBoolean("mem_liked"));
            return marketFeed;
        };
    }

    private RowMapper<GetMarketFeedRes> marketFeedByUserIdxRowMapper() {
        return (rs, rowNum) -> {
            GetMarketFeedRes marketFeed = new GetMarketFeedRes();
            marketFeed.setMarketIdx(rs.getInt("m.market_idx"));
            marketFeed.setUserIdx(rs.getInt("mem_idx"));
            marketFeed.setTitle(rs.getString("m.market_title"));
            marketFeed.setPrice(rs.getInt("m.market_price"));
            marketFeed.setSoldout(rs.getString("m.market_soldout"));
            marketFeed.setImage(rs.getString("m.market_image"));
            marketFeed.setLikeCount(rs.getInt("m.market_like_count"));
            marketFeed.setUserLiked(rs.getBoolean("mem_liked"));
            return marketFeed;
        };
    }

    private RowMapper<Member> getFeedMemberIdxRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemIdx(rs.getInt("mem_idx"));
            return member;
        };
    }
}
