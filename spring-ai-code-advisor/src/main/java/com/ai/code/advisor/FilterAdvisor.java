package com.ai.code.advisor;

import cn.hutool.dfa.WordTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

import java.util.List;

public class FilterAdvisor implements BaseAdvisor {
    private static final Logger log = LoggerFactory.getLogger(LogAdvisor.class);
    //项目启动时 初始化敏感词树【项目启动时只需要加载一次】
    private static  final WordTree WORD_TREE = new WordTree();
    static {
        List<String> words = List.of("违禁词A", "违禁词B", "违禁词C");
        //将违禁词添加到违禁词树
        WORD_TREE.addWords(words);
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        //如果有违禁词则直接拦截用户请求 直接抛出异常
        //1.获取用户的提示词
        String userInput = chatClientRequest.prompt().getUserMessage().getText();

        //isMatch 是否包含违禁词
        //match 将违禁词返回
        if(WORD_TREE.isMatch(userInput)){
            List<String> foundWords = WORD_TREE.matchAll(userInput); //获取所有的匹配的违禁词
            log.info("【拦截】包含违禁词：{}，用户输入：{}",foundWords,userInput);
            throw new RuntimeException("消息包含违禁词，请求被拦截");
        }

        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

