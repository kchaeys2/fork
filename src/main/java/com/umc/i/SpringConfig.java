package com.umc.i;


import com.umc.i.src.market.feed.MarketFeedDao;
import com.umc.i.src.market.feed.MarketFeedRepository;
import com.umc.i.src.member.jwt.JwtDao;
import com.umc.i.src.member.jwt.JwtRepository;
import com.umc.i.src.member.login.LoginDao;
import com.umc.i.src.member.login.LoginRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private final DataSource dataSource;

    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Bean
    public LoginRepository memberRepository() {
        return new LoginDao(dataSource);
    }

    @Bean
    public JwtRepository jwtRepository() {
        return new JwtDao(dataSource);
    }


    @Bean
    public MarketFeedRepository marketFeedRepository() {
        return new MarketFeedDao(dataSource);
    }
}
