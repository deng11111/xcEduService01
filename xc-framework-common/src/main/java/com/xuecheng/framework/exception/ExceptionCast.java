package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @ClassName ExceptionCast
 * @Author 邓元粮
 * @Date 2019/1/20 13:46
 * @Version 1.0
 **/
public class ExceptionCast {
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
