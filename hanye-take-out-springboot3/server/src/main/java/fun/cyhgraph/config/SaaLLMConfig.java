package fun.cyhgraph.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.api.RedissonClient;

@Configuration
public class SaaLLMConfig {
    private final String QWEN_MODEL = "qwen3-max";
    @Bean(name = "qwen")
    public ChatModel qwen(){
         return DashScopeChatModel.builder()
                 .dashScopeApi(DashScopeApi.builder().apiKey(System.getenv("aliQwen-api")).build())
                 .defaultOptions( DashScopeChatOptions.builder().withModel(QWEN_MODEL).build())
                 .build();
    }

    @Bean(name = "qwenAgent")
    public ReactAgent qwenAgent(@Qualifier("qwen") ChatModel qwen,RedisSaver redisSaver){

         return ReactAgent.builder().name("qwenAgent").model(qwen).saver(redisSaver).build();
    }
}
