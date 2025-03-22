package com.project.growfit.global.redis.repository;

import com.project.growfit.global.redis.entity.TokenRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRedisRepository extends CrudRepository<TokenRedis, String> {

    Optional<TokenRedis> findByAccessToken(String accessToken);

    Optional<TokenRedis> findById(String user_id);
}