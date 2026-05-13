package test;

import com.ai.code.StartApp;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SupervisorAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

/**
 * 编排Agent模式 测试
 */
@SpringBootTest(classes = StartApp.class)
public class ArrangementWorkTest {
    @Autowired
    private SupervisorAgent supervisorAgent; // 主Agent

    @Test
    public void test01() throws GraphRunnerException {
        // 使用 - 监督者会根据任务自动路由并支持多步骤处理
        Optional<OverAllState> result = supervisorAgent.invoke("帮我写一篇关于春天的文章，200字左右，并翻译英文形式给我");
        System.out.println(result);
    }
}
