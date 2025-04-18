package com.xue.mianshi.listener;

import cn.hutool.json.JSONUtil;
import com.xue.mianshi.common.ErrorCode;
import com.xue.mianshi.config.CaffeineConfig;
import com.xue.mianshi.exception.ThrowUtils;
import com.xue.mianshi.model.entity.Question;
import com.xue.mianshi.model.vo.QuestionVO;
import com.xue.mianshi.service.QuestionService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;



/**
 * mq消息监听者
 */
@Component
public class CacheListener {
    @Resource
    private QuestionService questionService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用于更新缓存
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue.cache"),
            exchange = @Exchange(name = "direct.ex.cache",type = ExchangeTypes.DIRECT),
            key = {"cache"}
    ))
    public QuestionVO updateCache(Long questionId){
        // 查询数据库
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        QuestionVO questionVO= questionService.getQuestionVO(question);
        //构建本地缓存
        String key = "question:id:"+questionId;
        String jsonStr = JSONUtil.toJsonStr(questionVO);
        CaffeineConfig.put(key,jsonStr);
        //构建redis缓存
        stringRedisTemplate.opsForValue().set(key,jsonStr,5L, TimeUnit.MINUTES);
        return questionVO;
    }
}
