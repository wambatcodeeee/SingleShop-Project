package com.elice.team4.singleShop.user.jwt;

import com.elice.team4.singleShop.user.entity.User;
import com.elice.team4.singleShop.user.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 30;
    private final long REFRESHTOKEN_VALID_MILISECOND = 1000L * 60 * 60 * 24 * 7;

    @Value("${key.salt}")
    private String salt;

    private Key secretKey;

    private final UserDetailsServiceImpl userDetailsService;

    public JwtTokenProvider(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
        log.info("[init] JwtTokenProvider 내 SecretKey 초기화 완료");
    }

    public String createToken(String name, User.Role roles) {
        Claims claims = Jwts.claims().setSubject(name);
        claims.put("roles", roles);
        Date now = new Date();
        log.info("[createToken] 토큰 생성 완료");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_MILISECOND))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String name){
        Claims claims = Jwts.claims().setSubject(name);
        Date now = new Date();
        log.info("[createRefreshToken] Refresh 토큰 생성 완료");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESHTOKEN_VALID_MILISECOND))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        log.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = this.getUserDetailsInfo(token);
        log.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserPk(String token) {
        log.info("[getUserPk] 토큰 기반 회원 구별 정보 추출");
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String jwtToken) {
        log.info("[validateToken] 토큰 유효 체크 시작 ");
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }

    public UserDetails getUserDetailsInfo(String token){
        return userDetailsService.loadUserByUsername(this.getUserPk(token));
    }

    public User getUserInfo(String name){
        return userDetailsService.loadUserInfoByUsername(name);
    }
}
