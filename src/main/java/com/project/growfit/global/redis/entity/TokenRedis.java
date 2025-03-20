package com.project.growfit.global.redis.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7)
public class TokenRedis {

    @Id
    private String id;

    @Indexed
    private String accessToken;

    private String refreshToken;

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}