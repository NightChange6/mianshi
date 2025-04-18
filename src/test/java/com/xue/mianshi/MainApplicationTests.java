package com.xue.mianshi;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 主类测试
 *
 * @author xms 
 * @from 
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Test
    void contextLoads() {
        //测试向mq发送信息
        rabbitTemplate.convertAndSend("hello.mq1","test");
    }

}
