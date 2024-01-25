package com.api.farmingsoon.common.util;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.BadRequestException;
import com.api.farmingsoon.common.redis.RedisService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
        deleteRefreshToken(prevRefreshToken);
        setRefreshToken(newRefreshToken, email);
    }

    public void setRefreshToken(String refreshToken, String email) {
        redisService.setData(refreshToken, email, refreshExpirationTime, TimeUnit.SECONDS);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisService.deleteData(refreshToken);
    }

    public static String getRefreshToken(HttpServletRequest request) {
        String refreshToken = extractBearerToken(request.getHeader("refreshToken"));
        if(refreshToken.isBlank()) {
            throw new BadRequestException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }
        return refreshToken;
    }

}
