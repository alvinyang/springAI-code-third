package com.ai.code.from;

import lombok.Data;

import java.util.List;

@Data
public class MyPictureForm {
    private List<String> images; //图片地址
    private String text;  //提示词
}
