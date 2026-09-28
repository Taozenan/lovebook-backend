package com.tzn.shop.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final JdbcTemplate jdbcTemplate;
    private final SecretKey signingKey;

    public CurrentUserService(JdbcTemplate jdbcTemplate, @Value("${auth.jwt.secret}") String jwtSecret) {
        this.jdbcTemplate = jdbcTemplate;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public long requireUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("请先登录");
        }
        try {
            String username = Jwts.parser().verifyWith(signingKey).build()
                    .parseSignedClaims(authorization.substring(7)).getPayload().getSubject();
            List<Long> ids = jdbcTemplate.queryForList("SELECT id FROM sys_user WHERE username = ?", Long.class, username);
            if (ids.isEmpty()) {
                throw new IllegalArgumentException("用户不存在");
            }
            return ids.get(0);
        } catch (JwtException exception) {
            throw new IllegalArgumentException("登录已失效，请重新登录");
        }
    }
}
