package test;

import com.ai.code.StartApp;
import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(classes = StartApp.class)
public class EvaluationOptionsTest {
    @Autowired
    @Qualifier("writeClient")
    private ChatClient writeChatClient;

    @Autowired
    @Qualifier("evaClient")
    private ChatClient evalChatClient;

    @Autowired
    private StateGraph evaluationOptizimerWorkFlow;

    @Test
    public void test01(){
        //生成---评估--判断评估结果---通过--输出 -- 不通过--继续循环
        String prompt = "请编写一篇关于问界M7的介绍短文，内容包括发展历史、发展趋势以及优缺点内容，字数控制在200字左右";
        //调用
        String content = writeChatClient.prompt(prompt).call().content();//结果
        boolean isPass = false; //是否通过

        while(!isPass){
            String result = evalChatClient.prompt("请评估一下文章：如果评估通过则输出pass，否则输出请改进以及改进的建议：" + content).call().content();//评估结果

            if(result.contains("pass")){
                isPass = true;
                System.out.println("评估通过");
            }else{
                System.out.println("进行优化");
                content  = writeChatClient.prompt("请根据以下评估建议优化文章：" + result + "原始文章：" + content).call().content();
            }
        }

        System.out.println("最终文章为："+content);
    }

    @Test
    public void testAgent() throws GraphStateException {
        // 编译工作流，recursionLimit为递归次数，例如递归5（不包含5），即：生成-评估是否通过-生成-评估是否通过，第五次不在调大模型去生成，直接返回最后一次生成的内容
        CompiledGraph compiledGraph = evaluationOptizimerWorkFlow.compile(CompileConfig.builder().recursionLimit(5).build());
        NodeOutput lastOutPut = compiledGraph.stream(Map.of("input", "请编写一篇关于问界M7的介绍短文，" +
                "内容包括发展历史、发展趋势以及优缺点内容，字数控制在200字左右"))
                .doOnNext(nodeOutput -> {
                    if (nodeOutput instanceof StreamingOutput<?> streamingOutput) {
                        System.out.println("从节点输出：" + streamingOutput.node() + ":"
                                + streamingOutput.agent() + ":"
                                + streamingOutput.message().getText());
                    }
                })
                .blockLast();
        // 由于在评估后面添加的END，所以最后一次输出默认是评估的结果，这里通过data的方式获取到其中的content的结果，也就是作者写的文章
        AssistantMessage content = (AssistantMessage) lastOutPut.state().data().get("content");
        System.out.println("最后一次输出：\n" + content.getText());
    }
}
