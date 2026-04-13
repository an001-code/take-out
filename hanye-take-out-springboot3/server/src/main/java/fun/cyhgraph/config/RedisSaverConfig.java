package fun.cyhgraph.config;

import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisSaverConfig {
    @Bean
    public RedisSaver redisSaver(RedissonClient  redissonClient){
        return RedisSaver.builder().redisson(redissonClient).build();
    }
}
