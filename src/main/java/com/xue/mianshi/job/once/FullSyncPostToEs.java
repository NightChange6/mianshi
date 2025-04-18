package com.xue.mianshi.job.once;


import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import com.xue.mianshi.esdao.QuestionEsDao;
import com.xue.mianshi.model.dto.QuestionEsDTO;
import com.xue.mianshi.model.entity.Question;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.xue.mianshi.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 全量同步习题到 es
 *
 * @author xms
 * @from 
 */

//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private QuestionService questionService;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
          private QuestionEsDao questionEsDao;

    @Override
    public void run(String... args) throws IOException {
//        //根据json文件创建索引库
//        CreateIndexRequest createIndexRequest = new CreateIndexRequest("question");
//        //读取json文件
//        String json = ResourceUtil.readUtf8Str("biz/post_es_mapping.json");
//        createIndexRequest.source(json, XContentType.JSON);
//        //发送请求
//        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);

        //500条一页分批插入es中
        int pageSize = 500;
        int pageNo =1;
        while (true) {
            //分页获取题目
            Page<Question> page = questionService.lambdaQuery().page(new Page<>(pageNo, pageSize));
            //获取数据
            List<Question> questionList = page.getRecords();
            if (CollUtil.isEmpty(questionList)) {
                return;
            }
            //将questionList中的每一个question转换为QuestionEsDTO
            List<QuestionEsDTO> questionEsDTOList = questionList
                    .stream()
                    .map(QuestionEsDTO::objToDto)
                    .collect(Collectors.toList());
            //向question索引库中批量添加文档
            BulkRequest bulkRequest = new BulkRequest("question");
            // 在FullSyncPostToEs类中添加：
            // 配置 JSON 序列化的日期格式
            JSONConfig jsonConfig = new JSONConfig();
            jsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
            for (QuestionEsDTO questionEsDTO : questionEsDTOList) {
//                String jsonStr = JSONUtil.toJsonStr(questionEsDTO,jsonConfig);
//                System.out.println(questionEsDTO);
                bulkRequest.add(new IndexRequest()
                        .id(String.valueOf(questionEsDTO.getId()))
                        .source(JSONUtil.toJsonStr(questionEsDTO,jsonConfig),XContentType.JSON));
            }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            //翻页
            pageNo++;
        }
    }
}
