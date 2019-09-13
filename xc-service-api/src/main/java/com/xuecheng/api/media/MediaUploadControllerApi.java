package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName MediaUploadControllerApi
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/2619:55
 * @Version 1.0
 **/
@Api(value = "媒资管理接口",description = "媒资管理接口,提供文件上传,文件处理等接口")
public interface MediaUploadControllerApi {
    @ApiOperation("文件上传注册,以及校验文件是否存在")
    ResponseResult register(
            String fileMd5,
            String fileName,
            Long fileSize,
            String mimetype,
            String fileExt
    );

    /*
     *@Description 校验分块是否存在
     * @Author 邓元粮
     * @MethodName checkchunk
     * @Datetime 20:03 2019/8/26
     * @Param []
     * @return com.xuecheng.framework.domain.media.response.CheckChunkResult
     **/
    @ApiOperation("分块检查")
    CheckChunkResult checkchunk(
            String fileMd5,//
            String chunk,//分块下标
            Long chunkSize//分块大小
    );

    /*
     *@Description 上传分块
     * @Author 邓元粮
     * @MethodName uploadchunk
     * @Datetime 20:07 2019/8/26
     * @Param [file, chunk, fileMd5]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    @ApiOperation("上传分块")
    ResponseResult uploadchunk(MultipartFile file,
                                      Integer chunk,
                                      String fileMd5);

    /*
     *@Description 合并文件
     * @Author 邓元粮
     * @MethodName mergechunks
     * @Datetime 20:20 2019/8/26
     * @Param [fileMd5, fileName, fileSize, mimetype, fileExt]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    @ApiOperation("合并文件")
    ResponseResult mergechunks(String fileMd5,
                                      String fileName,
                                      Long fileSize,
                                      String mimetype,
                                      String fileExt);
}
