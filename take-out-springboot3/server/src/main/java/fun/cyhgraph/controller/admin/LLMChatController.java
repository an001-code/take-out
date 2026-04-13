package fun.cyhgraph.controller.admin;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.vo.LLMAnswerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequestMapping("/admin/chat")
public class LLMChatController {
    @Autowired
    @Qualifier("qwenAgent")
    ReactAgent qwenAgent;

    @GetMapping
    public Result<String> chat(@RequestParam(name="msg") String msg) throws GraphRunnerException {
        System.out.println("进入chat");

                RunnableConfig config = RunnableConfig.builder()
                        .threadId("1")  // 使用当前用户ID
                        .build();

                String response = qwenAgent.call(msg,config).getText();
                 LLMAnswerVO ans = new LLMAnswerVO(response);
                return Result.success(response);

    }
}
