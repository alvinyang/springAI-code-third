package test;

import com.ai.code.StartApp;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.flow.agent.LlmRoutingAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest(classes = StartApp.class)
public class RouteAgentTest {

    @Autowired
    private LlmRoutingAgent llmRoutingAgent;

    @Test
    public void test01() throws GraphRunnerException {
        // LLM会路由到 writerAgent
        Optional<OverAllState> result1 = llmRoutingAgent.invoke("帮我写一篇关于春天的散文，100字以内");
        System.out.println(result1.get().value("writer_output"));
        // LLM会路由到 reviewerAgent
        Optional<OverAllState> result2 = llmRoutingAgent.invoke("请帮我修改这篇文章：春天来了，花开了。100字以内");
        System.out.println(result2.get().value("reviewer_output"));
        // LLM会路由到 translatorAgent
        Optional<OverAllState> result3 = llmRoutingAgent.invoke("请将以下内容翻译成英文：春暖花开");
        System.out.println(result3.get().value("translator_output"));
    }
}
