package com.yhy.takeout.exception;


/**
 * 自定义异常处理
 */
public class CustomException extends RuntimeException{

    public CustomException(String msg){
        super(msg);
    }

}
