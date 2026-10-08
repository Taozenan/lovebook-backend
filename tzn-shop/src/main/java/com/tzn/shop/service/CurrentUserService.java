package com.tzn.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.common.entity.SysUser;
import com.tzn.common.mapper.SysUserMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class CurrentUserService {
  private final SysUserMapper userMapper;
  private final SecretKey signingKey;

  public CurrentUserService(
          SysUserMapper userMapper, @Value("${auth.jwt.secret}") String jwtSecret) {
    this.userMapper = userMapper;
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public long requireUserId(String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      throw new IllegalArgumentException("请先登录");
    }
    try {
      String username =
              Jwts.parser()
                      .verifyWith(signingKey)
                      .build()
                      .parseSignedClaims(authorization.substring(7))
                      .getPayload()
                      .getSubject();
      SysUser user =
              userMapper.selectOne(
                      new LambdaQueryWrapper<SysUser>()
                              .eq(SysUser::getUsername, username)
                              .eq(SysUser::getEnabled, 1));
      if (user == null) {
        throw new IllegalArgumentException("用户不存在");
      }
      return user.getId();
    } catch (JwtException exception) {
      throw new IllegalArgumentException("登录已失效，请重新登录");
    }
  }
}
