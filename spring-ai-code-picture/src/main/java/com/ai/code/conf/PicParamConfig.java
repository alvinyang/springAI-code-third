package com.ai.code.conf;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class PicParamConfig {
    @Value("${picture.param.watermark}")
    private String waterMark;

    @Value("${picture.param.negative_prompt}")
    private String negativePrompt;

    @Value("${picture.param.size}")
    private String size;

    @Value("${picture.param.num}")
    private Integer num;

    @Value("${picture.param.prompt_extend}")
    private String promptExtend;

    @Bean(name = "picparam")
    public Map<String, Object> getParams() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("watermark", this.waterMark);
        if (this.negativePrompt.isEmpty()) {
            result.put("negative_prompt", " ");
        } else {
            result.put("negative_prompt", this.negativePrompt);
        }
        result.put("n", this.num);
        result.put("prompt_extend", this.promptExtend);
        result.put("size", this.size);
        return result;
    }

    @Bean
    public MultiModalConversation getMultiModalConversation() {
        return new MultiModalConversation();
    }
}
