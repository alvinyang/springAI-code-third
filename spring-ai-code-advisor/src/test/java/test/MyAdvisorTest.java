package test;

import com.ai.code.StartApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StartApp.class)
public class MyAdvisorTest {
    @Resource
    private ChatClient chatClient;


    @Test
    public void test01(){
        System.out.println(
                chatClient.prompt()
                        .user("你好")
                        .call()
                        .content());
    }

    @Test
    public void test02(){
        System.out.println(
                chatClient.prompt()
                        .user("违禁词B")
                        .call()
                        .content());
    }
}
