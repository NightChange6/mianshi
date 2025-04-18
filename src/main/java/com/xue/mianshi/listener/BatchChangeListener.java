package com.xue.mianshi.listener;

import com.xue.mianshi.common.ErrorCode;
import com.xue.mianshi.exception.BusinessException;
import com.xue.mianshi.exception.ThrowUtils;
import com.xue.mianshi.model.entity.QuestionBankQuestion;
import com.xue.mianshi.service.QuestionBankQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 批量处理监听器
 */
@Component
@Slf4j
public class BatchChangeListener {
    @Resource
    private QuestionBankQuestionService questionBankQuestionService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue.batch"),
            exchange = @Exchange(name = "direct.ex.batch",type = ExchangeTypes.DIRECT),
            key = {"batch"}
    ))
    @Transactional(rollbackFor = Exception.class)
    public void BatchChange(List<QuestionBankQuestion> questionBankQuestions){
        try {
            boolean result = questionBankQuestionService.saveBatch(questionBankQuestions);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在于该题库，无法重复添加");
        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        }

    }
}
