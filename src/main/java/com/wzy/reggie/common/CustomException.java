package com.wzy.reggie.common;

/**
 * 自定义的一个业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
