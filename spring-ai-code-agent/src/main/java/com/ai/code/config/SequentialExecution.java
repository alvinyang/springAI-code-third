package com.ai.code.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 连接模式
 */
@Configuration
@RequiredArgsConstructor
public class SequentialExecution {
    private final DashScopeChatModel dashScopeChatModel;

    // 创建顺序Agent
    @Bean
    public SequentialAgent blogAgent(ReactAgent writerArticleAgent, ReactAgent reviewerAgent) {
        return SequentialAgent.builder()
                .name("blog_agent")
                .description("根据用户给定的主题写一篇文章，然后将文章交给评论员进行评论")
                .subAgents(List.of(writerArticleAgent, reviewerAgent))
                .build();
    }
}
