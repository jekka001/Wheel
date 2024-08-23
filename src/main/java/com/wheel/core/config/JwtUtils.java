package com.wheel.core.config;

import com.wheel.core.entity.main.User;
import com.wheel.core.exception.ValidateTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.wheel.core.utils.Constants.DAY_JWT_EXIST;
import static com.wheel.core.utils.UrlConstants.*;

@Component
@RequiredArgsConstructor
public class JwtUtils implements InitializingBean {
    private final static long EXISTENCE_TIME_MILLIS = TimeUnit.DAYS.toMillis(DAY_JWT_EXIST);
    @Value("${token.header}")
    private String tokenHeader;
    @Value("${token.prefix}")
    private String tokenPrefix;
    @Value("${secret.key}")
    private String secretKey;
    private byte[] secretKeyBytes;
    private Key sign;

    public String createToken(User user) {
        return Jwts.builder()
                .setClaims(createClaims(user))
                .setExpiration(createExpirationTime())
                .signWith(sign)
                .compact();
    }

    private Claims createClaims(User user) {

        return Jwts.claims().setSubject(user.getLogin());
    }

    private Date createExpirationTime() {
        long tokenCreateTime = new Date().getTime();

        return new Date(tokenCreateTime + EXISTENCE_TIME_MILLIS);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);

        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }

        throw new ValidateTokenException();
    }

    public boolean validateToken(String accessToken) throws AuthenticationException {
        Claims claims = resolveClaims(accessToken);

        return claims.getExpiration().after(new Date());
    }

    public Claims resolveClaims(String accessToken) {
        if (accessToken != null) {
            return parseJwtClaims(accessToken);
        }

        throw new ValidateTokenException();
    }

    private Claims parseJwtClaims(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKeyBytes).build();

        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> getSkipUrls() {
        List<String> skipUrls = new ArrayList<>();

        skipUrls.add("/" + LOGIN);
        skipUrls.add("/" + SWAGGER_URL + "/*");
        skipUrls.add("/" + SWAGGER_API_DOCS_URL + "/*");
        skipUrls.add("/" + SWAGGER_API_DOCS_URL);

        return skipUrls;
    }

    @Override
    public void afterPropertiesSet() {
        this.secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKey);
        this.sign = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}