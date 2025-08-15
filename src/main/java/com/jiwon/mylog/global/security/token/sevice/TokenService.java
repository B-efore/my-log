package com.jiwon.mylog.global.security.token.sevice;

import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.InvalidTokenException;
import com.jiwon.mylog.global.redis.RedisUtil;
import com.jiwon.mylog.global.redis.key.RedisKey;
import com.jiwon.mylog.global.security.token.dto.request.TokenRequest;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final RedisUtil redisUtil;

    public void saveToken(TokenRequest tokenRequest) {
        String key = RedisKey.REFRESH_TOKEN.createKey(tokenRequest.getUserId().toString());
        redisUtil.set(key, tokenRequest.getRefreshToken(), Duration.ofDays(30));
    }

    public void validateRefreshToken(Long userId, String refreshToken) {
        String key = RedisKey.REFRESH_TOKEN.createKey(userId.toString());
        String savedToken = redisUtil.get(key);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }
    }
}
