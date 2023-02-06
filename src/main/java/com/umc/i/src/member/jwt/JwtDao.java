package com.umc.i.src.member.jwt;

import com.umc.i.src.member.login.model.PostLoginMemberReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JwtDao implements JwtRepository{

    private final JdbcTemplate jdbcTemplate;

    public JwtDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Optional<PostLoginMemberReq> findByLoginId(String id) {
        List<PostLoginMemberReq> result = jdbcTemplate.query("select * from Member where mem_idx = ?", memberRowMapper(), id);

        return result.stream().findAny();
    }

    @Override
    public void deleteRefreshToken(String memIdx) {
        try {
            int result = jdbcTemplate.update("delete from Auto_login where mem_idx = ?", memIdx);
        } catch (Exception e) {
            log.info("{}", e);
            ;
        }
    }

    @Override
    public void insertRefreshToken(String memIdx, String refreshToken) {
        try {
            int result = jdbcTemplate.update("insert into Auto_login(mem_idx, aul_key, aul_created_time) values (?, ?, now())", memIdx, refreshToken);
        } catch (Exception e) {
            log.info("{}", e);
            ;
        }
    }

    private RowMapper<PostLoginMemberReq> memberRowMapper() {
        return (rs, rowNum) -> {
            PostLoginMemberReq postLoginMemberReq = new PostLoginMemberReq();
            postLoginMemberReq.setId(rs.getLong("mem_idx"));
            return postLoginMemberReq;
        };
    }
}
