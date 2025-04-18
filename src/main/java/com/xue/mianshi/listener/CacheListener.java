package com.xue.mianshi.listener;

import com.xue.mianshi.model.entity.QuestionBankQuestion;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * mq消息监听者
 */
@Component
public class CacheListener {
    /**
     * 用于更新缓存
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue.cache"),
            exchange = @Exchange(name = "direct.ex.cache",type = ExchangeTypes.DIRECT),
            key = {"cache"}
    ))
    public void updateCache(List<QuestionBankQuestion> questionBankQuestions){
        System.out.println(questionBankQuestions.toString());
    }
}
