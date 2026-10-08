package com.tzn.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.auth.dto.AvatarUpdateRequest;
import com.tzn.auth.dto.LoginRequest;
import com.tzn.auth.service.AuthService;
import com.tzn.auth.vo.LoginVo;
import com.tzn.common.entity.SysRole;
import com.tzn.common.entity.SysUser;
import com.tzn.common.entity.SysUserRole;
import com.tzn.common.mapper.SysRoleMapper;
import com.tzn.common.mapper.SysUserMapper;
import com.tzn.common.mapper.SysUserRoleMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecretKey signingKey;
    private final long expiresSeconds;

    public AuthServiceImpl(
            SysUserMapper userMapper,
            SysRoleMapper roleMapper,
            SysUserRoleMapper userRoleMapper,
            @Value("${auth.jwt.secret}") String jwtSecret,
            @Value("${auth.jwt.expires}") long expiresSeconds) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.expiresSeconds = expiresSeconds;
    }

    @Override
    public LoginVo login(LoginRequest request) {
        SysUser user =
                userMapper.selectOne(
                        new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.username()));
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        if (!Integer.valueOf(1).equals(user.getEnabled())) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        List<Long> roleIds =
                userRoleMapper
                        .selectList(
                                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()))
                        .stream()
                        .map(SysUserRole::getRoleId)
                        .toList();
        List<String> roles =
                roleIds.isEmpty()
                        ? List.of()
                        : roleMapper.selectBatchIds(roleIds).stream().map(SysRole::getRoleCode).toList();
        return new LoginVo(
                createToken(user.getUsername(), roles),
                user.getUsername(),
                user.getNickname(),
                user.getAvatarUrl(),
                roles,
                expiresSeconds);
    }

    @Override
    @Transactional
    public void register(String phone, String nickname, String password) {
        String username = phone.trim();
        if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username))
                > 0) {
            throw new IllegalArgumentException("该手机号已注册");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setNickname(nickname.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        SysRole role =
                roleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, "USER"));
        if (role == null) {
            throw new IllegalArgumentException("默认用户角色不存在");
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);
    }

    @Override
    public void updateAvatar(String authorization, AvatarUpdateRequest request) {
        SysUser user = currentUserEntity(authorization);
        String avatarUrl =
                request == null || request.avatarUrl() == null ? null : request.avatarUrl().trim();
        if (avatarUrl != null && avatarUrl.length() > 500) {
            throw new IllegalArgumentException("头像地址不能超过500个字符");
        }
        user.setAvatarUrl(avatarUrl);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public String currentUsername(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("请先登录");
        }
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(authorization.substring(7))
                    .getPayload()
                    .getSubject();
        } catch (JwtException exception) {
            throw new IllegalArgumentException("登录已失效，请重新登录");
        }
    }

    private SysUser currentUserEntity(String authorization) {
        SysUser user =
                userMapper.selectOne(
                        new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::getUsername, currentUsername(authorization)));
        if (user == null || !Integer.valueOf(1).equals(user.getEnabled())) {
            throw new IllegalArgumentException("用户不存在或已禁用");
        }
        return user;
    }

    private String createToken(String username, List<String> roles) {
        Instant now = Instant.now();
        JwtBuilder builder =
                Jwts.builder().subject(username).claim("roles", roles).issuedAt(Date.from(now));
        if (expiresSeconds > 0) {
            builder.expiration(Date.from(now.plusSeconds(expiresSeconds)));
        }
        return builder.signWith(signingKey).compact();
    }
}
