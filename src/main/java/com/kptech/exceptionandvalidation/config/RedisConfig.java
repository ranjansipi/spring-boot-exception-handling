package com.kptech.exceptionandvalidation.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    // Update RedisTemplate to support generic Object handling (Product and List<Product>)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Use String serializer for the key
        template.setKeySerializer(new StringRedisSerializer());

        // Use Jackson2Json serializer for the value (for both Product and List<Product>)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    // Define the Redis connection factory
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Customize connection properties for Redis (e.g., host, port)
        return new LettuceConnectionFactory("localhost", 6379); // Assuming Redis is running on localhost with default port
    }
}
