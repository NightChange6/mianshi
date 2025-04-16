package com.xue.mianshi.esdao;

import com.xue.mianshi.model.dto.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 题目 ES 操作
 */
public interface QuestionEsDao 
    extends ElasticsearchRepository<QuestionEsDTO, Long> {

}
