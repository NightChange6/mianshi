package com.xue.mianshi.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.xue.mianshi.model.dto.QuestionEsDTO;
import com.xue.mianshi.model.entity.Question;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 用于监听数据库变化从而更改ES
 */
@Component
public class ElasticSearchListener {
    @Resource
    private RestHighLevelClient restHighLevelClient;
    /**
     * 向es中添加新数据
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue.es"),
            exchange = @Exchange(name = "direct.ex.es",type = ExchangeTypes.DIRECT),
            key = {"es.add"}
    ))
    public void addToES(QuestionEsDTO questionEsDTO) throws IOException {
        //向question索引库中添加新的文档
        IndexRequest questionRequest = new IndexRequest("question").id(questionEsDTO.getId().toString());
        //准备json文档
        questionRequest.source(JSONUtil.toJsonStr(questionEsDTO), XContentType.JSON);
        restHighLevelClient.index(questionRequest, RequestOptions.DEFAULT);
    }

    /**
     * 更改es中已经存在的数据
     * @param questionEsDTO
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue.es"),
            exchange = @Exchange(name = "direct.ex.es",type = ExchangeTypes.DIRECT),
            key = {"es.update"}
    ))
    public void updateES(QuestionEsDTO questionEsDTO) throws IOException {
        //向question索引库中添加新的文档
        IndexRequest questionRequest = new IndexRequest("question").id(questionEsDTO.getId().toString());
        //准备json文档
        questionRequest.source(JSONUtil.toJsonStr(questionEsDTO), XContentType.JSON);
        restHighLevelClient.index(questionRequest, RequestOptions.DEFAULT);
    }
}
