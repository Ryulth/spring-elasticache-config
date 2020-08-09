package com.ryulth.elasticache.example.config

import io.lettuce.core.ReadFrom
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration

@Configuration
class RedisConfig {
    @Profile("prod")
    @Bean
    @Primary
    fun RedisConnectionFactory(
        @Value("\${redis.host}") hostName: String,
        @Value("\${redis.port:6379}") port: Int
    ): LettuceConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration(hostName, port)
        val clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ofMillis(100))
                .build()

        return LettuceConnectionFactory(redisConfiguration, clientConfig)
    }

    @Profile("prod")
    @Bean
    @Primary
    fun RedisConnectionFactoryForAWS(
        @Value("\${redis.primary.host}") masterHostName: String,
        @Value("\${redis.primary.port:6379}") masterPort: Int,
        @Value("\${redis.reader.host}") replicaHostName: String,
        @Value("\${redis.reader.port:6379}") replicaPort: Int
    ): LettuceConnectionFactory {
        val elastiCache = RedisStaticMasterReplicaConfiguration(masterHostName, masterPort)
        elastiCache.addNode(replicaHostName, replicaPort)

        val clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build()

        return LettuceConnectionFactory(elastiCache, clientConfig)
    }
}