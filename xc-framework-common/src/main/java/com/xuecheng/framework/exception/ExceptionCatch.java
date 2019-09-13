package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName ExceptionCatch
 * @Author 邓元粮
 * @Date 2019/1/20 14:09
 * @Version 1.0
 **/
@ControllerAdvice
public class ExceptionCatch {
    private Logger logger = LoggerFactory.getLogger(ExceptionCatch.class);


    protected static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTION_MAPS;

    private static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static {
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public  ResponseResult customException(CustomException e){
        logger.error("catch exception : {}\r\nexception: ",e.getMessage(), e);
        ResultCode resultCode = e.getResultCode();
        ResponseResult responseResult = new ResponseResult(resultCode);
        return responseResult;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public  ResponseResult customException(Exception e){
        if (null == EXCEPTION_MAPS){
            EXCEPTION_MAPS = builder.build();
        }
        ResultCode resultCode = EXCEPTION_MAPS.get(e.getClass());
        logger.error("catch exception : {}\r\nexception: ",e.getMessage(), e);
        if (null != resultCode){
            ResponseResult responseResult = new ResponseResult(resultCode);
            return responseResult;
        }else{
            return new ResponseResult(CommonCode.SERVER_ERROR) ;
        }
    }
}
