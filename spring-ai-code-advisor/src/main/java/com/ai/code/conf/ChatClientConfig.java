package com.ai.code.conf;

import com.ai.code.advisor.FilterAdvisor;
import com.ai.code.advisor.LogAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    @Resource
    private DashScopeChatModel dashScopeChatModel;

    @Bean
    public ChatClient getChatClient() {
        return ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(
                        new LogAdvisor(),
                        new FilterAdvisor()
                ) //指定chatClient的advisor
                .build();
    }
}
