package org.jeecg.modules.bill.exception;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author dbc
 * @create 2023-05-31 18:59
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public <T> Result<T> exceptionHandler(Exception e){
        log.error(e.getMessage(), e);
        //这里先判断拦截到的Exception是不是我们自定义的异常类型
        if(e instanceof ByxException){
            ByxException byxException = (ByxException)e;
            return Result.error(byxException.getCode(),byxException.getMsg());
        }
        //如果拦截的异常不是我们自定义的异常(例如：数据库主键冲突)
        return Result.error(500,"服务器端异常");
    }
}