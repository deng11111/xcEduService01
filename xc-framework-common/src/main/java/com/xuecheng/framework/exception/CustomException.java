package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @ClassName CustomException
 * @Author 邓元粮
 * @Date 2019/1/20 13:40
 * @Version 1.0
 **/
public class CustomException extends RuntimeException {
    private ResultCode resultCode ;

    public CustomException(ResultCode resultCode) {
        super("错误代码为: "+ resultCode.code() + "错误信息为: " + resultCode.message());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
