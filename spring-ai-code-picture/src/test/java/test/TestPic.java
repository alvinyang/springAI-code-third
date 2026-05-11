package test;

import com.ai.code.StartApp;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = StartApp.class)
public class TestPic {

    @Test
    public void text2Image() {
        // 以下为北京地域url，若使用新加坡地域的模型，需将url替换为：https://dashscope-intl.aliyuncs.com/api/v1
        Constants.baseHttpApiUrl = "https://dashscope.aliyuncs.com/api/v1";

        // 新加坡和北京地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
        // 若没有配置环境变量，请用百炼API Key将下行替换为：static String apiKey = "sk-xxx"
        String apiKey = System.getenv("ANTHROPIC_AUTH_TOKEN");
        String prompt = "一副典雅庄重的对联悬挂于厅堂之中，房间是个安静古典的中式布置，桌子上放着一些青花瓷，对联上左书“义本生知人机同道善思新”，右书“通云赋智乾坤启数高志远”， 横批“智启千问”，字体飘逸，在中间挂着一幅中国风的画作，内容是岳阳楼。";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prompt_extend", true); // 扩展优化提示词
        parameters.put("watermark", false); // 关闭水印
        parameters.put("negative_prompt", " "); // 负面提示词
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(apiKey)
                        // 当前仅qwen-image-plus、qwen-image模型支持异步接口
                        .model("qwen-image-plus")
                        .prompt(prompt)
                        .n(1)
                        .size("1664*928")
                        .parameters(parameters)
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---同步调用，请等待任务执行----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
    }
}
