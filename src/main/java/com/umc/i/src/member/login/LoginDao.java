package com.umc.i.src.member.login;

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
public class LoginDao implements LoginRepository {

    private final JdbcTemplate jdbcTemplate;

    public LoginDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<PostLoginMemberReq> findByLoginEmail(String loginEmail) {
        List<PostLoginMemberReq> result = jdbcTemplate.query("select * from Member where mem_email = ?", memberRowMapper(), loginEmail);

        return result.stream().findAny();
    }

    private RowMapper<PostLoginMemberReq> memberRowMapper() {
        return (rs, rowNum) -> {
            PostLoginMemberReq postLoginMemberReq = new PostLoginMemberReq();
            postLoginMemberReq.setId(rs.getLong("mem_idx"));
            postLoginMemberReq.setEmail(rs.getString("mem_email"));
            postLoginMemberReq.setPassword(rs.getString("mem_password"));
            postLoginMemberReq.setNickname("mem_nickname");
            return postLoginMemberReq;
        };
    }
}
