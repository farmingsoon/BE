package com.api.farmingsoon.common.util;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.BadRequestException;
import com.api.farmingsoon.common.exception.custom_exception.UnauthorizedException;
import com.api.farmingsoon.common.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final RedisService redisService;

    @Value("${custom.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;
    /**
     * @Description
     * Bearer토큰의 여부에 대해 검증한 뒤 토큰을 반환합니다.
     */
    public static String extractBearerToken(String token) {
        if (token != null) {
            if (!token.startsWith("Bearer")) {
                throw new BadRequestException(ErrorCode.INVALID_TYPE_TOKEN);
            }

            return token.split(" ")[1].trim();
        }
        return null;
    }
    public void rotateRefreshToken(String prevRefreshToken, String newRefreshToken, String email) {
        // 만료: redis 에서 삭제 후 재등록..
        deleteRefreshToken(prevRefreshToken, email);
        setRefreshToken(newRefreshToken, email);
    }

    public void setRefreshToken(String refreshToken, String email) {
        redisService.setData(email + ":" + refreshToken, "", refreshExpirationTime, TimeUnit.SECONDS);
    }

    public void deleteRefreshToken(String refreshToken, String email) {
        redisService.deleteData(email + ":" + refreshToken);
    }

    public static String getRefreshToken(HttpServletRequest request) {
        String refreshToken = CookieUtils.getRefreshTokenCookieValue(request);
        if(refreshToken == null) {
            throw new BadRequestException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }
        return refreshToken;
    }

    public void checkSnatch(String refreshToken, String email){
        if (redisService.getData(email + ":" + refreshToken) == null) {
            Set<String> keySet = redisService.getKeySet(email + ":*");
            keySet.forEach(redisService::deleteData);
            throw new UnauthorizedException(ErrorCode.SNATCH_TOKEN);
        }
    }
}
