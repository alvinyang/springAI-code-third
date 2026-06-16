package com.ai.code;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import com.alibaba.cloud.ai.graph.OverAllState;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.Optional;

/**
 * 渐进式技能示例
 * 
 * 演示如何使用 Spring AI Alibaba 的 Skills 功能：
 * 1. 从 classpath 加载技能
 * 2. 智能体自动发现可用技能
 * 3. 按需加载技能详情
 * 4. 根据技能指导完成任务
 * 
 * 技能目录结构：
 * src/main/resources/skills/
 * ├── data-analysis/
 * │   └── SKILL.md
 * └── code-review/
 *     └── SKILL.md
 */
public class SkillsExample {

    public static void main(String[] args) throws Exception {
        // 创建 ChatModel
        ChatModel chatModel = createChatModel();
        
        // 创建技能注册中心（从 classpath 加载）
        SkillRegistry registry = ClasspathSkillRegistry.builder()
                .classpathPath("skills")
                .build();
        
        // 创建技能钩子
        SkillsAgentHook skillsHook = SkillsAgentHook.builder()
                .skillRegistry(registry)
                .build();
        
        // 创建带技能的智能体
        ReactAgent agent = ReactAgent.builder()
                .name("skills-agent")
                .model(chatModel)
                .saver(new MemorySaver())
                .hooks(List.of(skillsHook))
                .outputKey("result_msg")
                .build();
        
        // 示例1：查询可用技能
        System.out.println("=== 示例1：查询可用技能 ===");
        Optional<OverAllState> result1 = agent.invoke("你有哪些技能？");
        if (result1.isPresent()) {
            System.out.println(result1.get().value("result_msg").get());
        }
        System.out.println();
        
        // 示例2：使用数据分析技能
        System.out.println("=== 示例2：使用数据分析技能 ===");
        Optional<OverAllState> result2 = agent.invoke("请分析以下销售数据：2024年Q1销售额100万，Q2销售额150万，Q3销售额120万，Q4销售额180万");
        if (result2.isPresent()) {
            System.out.println(result2.get().value("result_msg").get());
        }
        System.out.println();
        
        // 示例3：使用代码审查技能
        System.out.println("=== 示例3：使用代码审查技能 ===");
        String code = """
                public class Calculator {
                    public int divide(int a, int b) {
                        return a / b;
                    }
                }
                """;
        Optional<OverAllState> result3 = agent.invoke("请审查这段 Java 代码：" + code);
        if (result3.isPresent()) {
            System.out.println(result3.get().value("result_msg").get());
        }
    }
    
    private static ChatModel createChatModel() {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("ANTHROPIC_AUTH_TOKEN"))
                .build();
        
        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();
    }
}
