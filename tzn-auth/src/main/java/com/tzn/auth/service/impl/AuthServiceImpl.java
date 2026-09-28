package com.tzn.auth.service.impl;

import com.tzn.auth.dto.LoginRequest;
import com.tzn.auth.dto.AvatarUpdateRequest;
import com.tzn.auth.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecretKey signingKey;
    private final long expiresSeconds;

    public AuthServiceImpl(JdbcTemplate jdbcTemplate,
                           @Value("${auth.jwt.secret}") String jwtSecret,
                           @Value("${auth.jwt.expires}") long expiresSeconds) {
        this.jdbcTemplate = jdbcTemplate;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.expiresSeconds = expiresSeconds;
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT id, username, nickname, avatar_url AS avatarUrl, password, enabled FROM sys_user WHERE username = ?", request.username());
        if (users.isEmpty() || !passwordEncoder.matches(request.password(), String.valueOf(users.get(0).get("password")))) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        Map<String, Object> user = users.get(0);
        if (!isEnabled(user.get("enabled"))) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        List<String> roles = jdbcTemplate.queryForList(
                "SELECT r.role_code FROM sys_role r INNER JOIN sys_user_role ur ON ur.role_id = r.id WHERE ur.user_id = ?",
                String.class, user.get("id"));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", createToken(String.valueOf(user.get("username")), roles));
        result.put("username", user.get("username"));
        result.put("nickname", user.get("nickname"));
        result.put("avatarUrl", user.get("avatarUrl"));
        result.put("roles", roles);
        result.put("expiresIn", expiresSeconds);
        return result;
    }

    @Override
    public void register(String phone, String nickname, String password) {
        String username = phone.trim();
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user WHERE username = ?", Integer.class, username);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("该手机号已注册");
        }
        jdbcTemplate.update("INSERT INTO sys_user (username, nickname, password, enabled) VALUES (?, ?, ?, ?)",
                username, nickname.trim(), passwordEncoder.encode(password), true);
        Long userId = jdbcTemplate.queryForObject("SELECT id FROM sys_user WHERE username = ?", Long.class, username);
        Long roleId = jdbcTemplate.queryForObject("SELECT id FROM sys_role WHERE role_code = 'USER'", Long.class);
        jdbcTemplate.update("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)", userId, roleId);
    }
    @Override
    public void updateAvatar(String authorization, AvatarUpdateRequest request) {
        String username = currentUsername(authorization);
        String avatarUrl = request == null || request.avatarUrl() == null ? null : request.avatarUrl().trim();
        if (avatarUrl != null && avatarUrl.length() > 500) {
            throw new IllegalArgumentException("头像地址不能超过500个字符");
        }
        jdbcTemplate.update("UPDATE sys_user SET avatar_url = ?, update_time = NOW() WHERE username = ?", avatarUrl, username);
    }

    @Override
    public String currentUsername(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("请先登录");
        }
        try {
            return Jwts.parser().verifyWith(signingKey).build()
                    .parseSignedClaims(authorization.substring(7)).getPayload().getSubject();
        } catch (JwtException exception) {
            throw new IllegalArgumentException("登录已失效，请重新登录");
        }
    }

    private boolean isEnabled(Object enabled) {
        return enabled instanceof Number number && number.intValue() == 1 || Boolean.TRUE.equals(enabled);
    }

    private String createToken(String username, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder().subject(username).claim("roles", roles).issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiresSeconds))).signWith(signingKey).compact();
    }
}



