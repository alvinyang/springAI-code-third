package com.ai.code.service;

import com.ai.code.from.MyPictureForm;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestService {
    @Resource
    @Qualifier(value = "picparam")
    private Map<String,Object> params;

    @Resource
    private MultiModalConversation multiModalConversation;

    public List<String> getPicture(MyPictureForm form) {
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue()) //指定角色 是用户
                .content(Arrays.asList(
                        Collections.singletonMap("image", form.getImages().get(0)),
                        Collections.singletonMap("image", form.getImages().get(1)),
                        Collections.singletonMap("text", form.getText())
                )).build();

        String apiKey = System.getenv("ANTHROPIC_AUTH_TOKEN");
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(apiKey)
                .model("qwen-image-2.0-pro")
                .messages(Collections.singletonList(userMessage))
                .parameters(params)
                .build();

        MultiModalConversationResult result = null; //对多模态大模型发起调用
        try {
            result = this.multiModalConversation.call(param);
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (UploadFileException e) {
            throw new RuntimeException(e);
        }
        // 如需查看完整响应，请取消下行注释
        List<Map<String, Object>> contentList = result.getOutput().getChoices().get(0).getMessage().getContent();
        int imageIndex = 1;
        ArrayList<String> returnResult = new ArrayList<String>();
        for (Map<String, Object> content : contentList) {
            if (content.containsKey("image")) {
                // System.out.println("输出图像" + imageIndex + "的URL：" + content.get("image"));
                returnResult.add(content.get("image").toString());
                imageIndex++;
            }
        }
        return returnResult;
    }
}
