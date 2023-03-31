package com.winmanboo.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@UtilityClass
public class JwtHelper {
  private final static long tokenExpiration = 24 * 60 * 60 * 1000;
  private final static Key tokenSignKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  public static String createToken(Long userId, String username) {
    return Jwts.builder()
        // 分类
        .setSubject("AUTH-USER")
        // 设置 token 有效时长
        .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
        // 设置主体部分
        .claim("userId", userId)
        .claim("username", username)
        // 签名部分
        .signWith(tokenSignKey)
        .compressWith(CompressionCodecs.GZIP)
        .compact();
  }

  public static Long getUserId(String token) {
    try {
      if (!StringUtils.hasText(token)) return null;

      Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(tokenSignKey).build().parseClaimsJws(token);
      Claims claims = claimsJws.getBody();
      Integer userId = (Integer) claims.get("userId");
      return userId.longValue();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String getUsername(String token) {
    try {
      if (!StringUtils.hasText(token)) return "";

      Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(tokenSignKey).build().parseClaimsJws(token);
      Claims claims = claimsJws.getBody();
      return (String) claims.get("username");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void main(String[] args) {
    String token = JwtHelper.createToken(1L, "admin");
    System.out.println(token);
    System.out.println(JwtHelper.getUserId(token));
    System.out.println(JwtHelper.getUsername(token));
  }
}
