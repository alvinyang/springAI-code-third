package com.ai.code.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

public class LogAdvisor implements BaseAdvisor {

    private static final Logger log = LoggerFactory.getLogger(LogAdvisor.class); //获取日志工厂

    //在调用大模型之前调用的方法
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        log.info("【请求日志】用户输入：{}，使用模型：{}",
                chatClientRequest.prompt().getUserMessage().getText(),
                chatClientRequest.prompt().getOptions().getModel());
        return chatClientRequest;
    }

    //调用大模型之后执行的方法
    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        log.info("【响应日志】AI回复：{}", chatClientResponse.chatResponse().getResult().getOutput().getText());
        return chatClientResponse;
    }

    //指定Advisor的执行优先级 返回值只能是非负整数 值越小优先级越高
    @Override
    public int getOrder() {
        return 2;
    }
}

