package com.yhy.takeout.exception;

import com.yhy.takeout.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * SQL异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        return Result.error("the account was used!");
    }

    /**
     * CustomException处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){
        return Result.error(ex.getMessage());
    }
}
