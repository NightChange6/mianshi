package com.xue.mianshi.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建题库请求
 *
 * @author xms xms
 * @from xms
 */
@Data
public class QuestionBankAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    private static final long serialVersionUID = 1L;
}