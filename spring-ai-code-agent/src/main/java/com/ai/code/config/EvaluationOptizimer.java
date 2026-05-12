package com.ai.code.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class EvaluationOptizimer {
    private final DashScopeChatModel dashScopeChatModel;

    @Bean("writeClient")
    public ChatClient getChatClient(){
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你是一位非常编写汽车类的文案写作家，删除用于简介易懂的语言描述一款汽车")
                .build();
    }

    @Bean("evaClient")
    public ChatClient getEvalateChatClient() {
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你是专业的文案评估师，熟知各类文案写作规范与技巧，能精准评判文案内容的逻辑性、创新性以及语言表达的流畅性特别注意数据的准确性以及合理性")
                .build();
    }

    @Bean("writerAgent")
    public ReactAgent writerChatClient() {
        return ReactAgent.builder()
                .name("writer") // 全局唯一
                .model(dashScopeChatModel)
                // Agent指令模板，{input}和{content}为变量占位符
                .instruction("你是一个经验丰富的汽车类型文章编写作家，完成用户的各项需求：{input}")
                .outputKey("content")
                .build();
    }

    @Bean("evaAgent")
    public ReactAgent qualityEvaluatorChatAgent() {
        return ReactAgent.builder()
                .name("qualityEvaluator")
                .model(dashScopeChatModel)
                .instruction("你是十分严格的文案评估师，熟知各类文案写作规范与技巧，" +
                        "能精准评判文案内容的逻辑性、创新性以及语言表达的流畅性，" +
                        "确认通过在最后输出'WORK_SUCCESS'，" +
                        "不通过在最后输出'WORK_FAIL'，" +
                        "最少需要一次修正，" +
                        "作为评估师只需要提出建议不需要修改文稿" +
                        "下面是写作的文案：{content}")
                .outputKey("result")
                .build();
    }

    @Bean
    public StateGraph writeEvaWorkFlow(ReactAgent writerAgent, ReactAgent evaAgent)
            throws GraphStateException {
        // 1.配置工作流状态管理策略
        // 定义状态的更新规则：控制工作流中数据的覆盖以及追加逻辑
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();
            keyStrategyHashMap.put("input", new ReplaceStrategy());
            return keyStrategyHashMap;
        };
        // 创建状态图工作流实例
        StateGraph stateGraph = new StateGraph(keyStrategyFactory);
        // 将agent注册到工作流上，添加工作流节点
        stateGraph.addNode(writerAgent.name(),
                // 1. 包含上下文，让agent获得上下文 2.返回推理过程
                writerAgent.asNode(true,true));
        stateGraph.addNode(evaAgent.name(),
                evaAgent.asNode(true, true));
        // 定义执行流程，定义节点的顺序连接
        // 定义固定流程
        stateGraph.addEdge(StateGraph.START, writerAgent.name()); // 指定开始流程，写作为开始
        stateGraph.addEdge(writerAgent.name(), evaAgent.name()); // 工作的固定流程为：写作 评估
        // 定义分支流程【逻辑】，定义条件边，根据执行结果决定下一步流向，即，判断评估结果
        stateGraph.addConditionalEdges(evaAgent.name(),
                AsyncEdgeAction.edge_async(state -> {
                    AssistantMessage assistantMessage = (AssistantMessage) state.data().get("result");
                    String result = assistantMessage.getText();
                    System.out.println("评估结果为：" + result);
                    // 通过评估结果判断
                    if (result.contains("WORK_SUCCESS")) {
                        System.out.println("通过");
                        return "通过";
                    } else {
                        System.out.println("未通过");
                        return "修订";
                    }
                }),
                Map.of("通过", StateGraph.END, "修订", writerAgent.name())
        );
        return stateGraph;
    }
}
